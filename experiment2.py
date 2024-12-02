import json
import logging
from dataclasses import dataclass
from typing import List, Dict
import openai

@dataclass
class CodeChange:
    id: str
    old_comment: str
    old_code: str
    new_code: str

class TestGenerator:
    def __init__(self, openai_key: str):
        self.logger = logging.getLogger(__name__)
        logging.basicConfig(level=logging.INFO)
        openai.api_key = openai_key

    def parse_json(self, json_file: str) -> List[CodeChange]:
        """Parse the input JSON file containing code changes."""
        with open(json_file, 'r') as f:
            data = json.load(f)
        return [CodeChange(
            id=item['id'],
            old_comment=item['old_comment_raw'],
            old_code=item['old_code_raw'],
            new_code=item['new_code_raw']
        ) for item in data]

    def generate_test_with_gpt(self, change: CodeChange) -> str:
        """Generate test cases using GPT-4."""
        safe_class_name = f"Test_{change.id.replace('-', '_')}"
        prompt = f"""
        Generate a Java test class exactly following this template - no explanations or additional text:

        package test;

        import org.junit.Test;
        import org.junit.Before;
        import static org.junit.Assert.*;
        import java.util.*;

        // Implementation class
        class TestedClass {{
            {change.old_code.strip()}
        }}

        // Test class - DO NOT use 'public' modifier
        class {safe_class_name} {{
            private TestedClass instance;

            @Before
            public void setup() {{
                instance = new TestedClass();
            }}

            @Test
            public void testBasicFunctionality() {{
                assertTrue(instance.isSorted(Arrays.asList(1, 2, 3, 4, 5)));
            }}

            @Test
            public void testEdgeCases() {{
                assertTrue(instance.isSorted(new ArrayList<>())); // Replace with relevant edge cases
            }}

            @Test
            public void testInvalidInputs() {{
                assertFalse(instance.isSorted(Arrays.asList(5, 4, 3, 2, 1))); // Replace with invalid inputs
            }}
        }}
        """
        try:
            response = openai.ChatCompletion.create(
                model="gpt-4",
                messages=[
                    {"role": "system", "content": "You are a Java testing expert."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.7,
                max_tokens=2000
            )
            test_code = response.choices[0].message.content.strip()
            test_code = test_code.replace("```java", "").replace("```", "").strip()
            self.logger.info(f"Generated test for {change.id}:\n{test_code}\n")
            return test_code
        except Exception as e:
            self.logger.error(f"Error generating test with GPT: {str(e)}")
            return None

    def simulate_test_execution_with_gpt_percentage(self, change: CodeChange, test_code: str) -> Dict[str, float]:
        """
        Use GPT-3 to simulate whether the test case scenarios pass or fail for old and new code
        and calculate passing percentages.
        """
        prompt = f"""
        You are a Java expert. I will provide you with:
        1. A Java test case.
        2. An old Java method implementation.
        3. A new Java method implementation.

        Your task:
        - Evaluate multiple scenarios for the test case.
        - For each scenario, tell me if it PASSES or FAILS for the old implementation.
        - For each scenario, tell me if it PASSES or FAILS for the new implementation.

        Test Scenarios:
        1. A list of integers sorted in ascending order.
        2. A list of integers sorted in descending order.
        3. A list with duplicate elements sorted in ascending order.
        4. An empty list.
        5. A list with a single element.
        6. A list containing null elements.
        7. A list containing non-integer elements (e.g., strings, doubles).

        Here is the information:

        Old Code:
        {change.old_code}

        New Code:
        {change.new_code}

        Test Case:
        {test_code}

        Provide your answer in this format:
        Old Code:
        - Scenario 1: PASS/FAIL
        - Scenario 2: PASS/FAIL
        ...
        New Code:
        - Scenario 1: PASS/FAIL
        - Scenario 2: PASS/FAIL
        ...
        """
        try:
            response = openai.ChatCompletion.create(
                model="gpt-3.5-turbo",
                messages=[
                    {"role": "system", "content": "You are a Java testing and code analysis expert."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0,
                max_tokens=1000
            )
            result_text = response.choices[0].message.content.strip()
            self.logger.info(f"Simulation result for change {change.id}:\n{result_text}")

            # Parse results
            old_code_results = [line.split(": ")[1] for line in result_text.split("Old Code:\n")[1].split("\nNew Code:")[0].strip().split("\n")]
            new_code_results = [line.split(": ")[1] for line in result_text.split("New Code:\n")[1].strip().split("\n")]

            old_code_passes = old_code_results.count("PASS")
            new_code_passes = new_code_results.count("PASS")

            total_scenarios = len(old_code_results)

            return {
                'old_code_pass_percentage': (old_code_passes / total_scenarios) * 100,
                'new_code_pass_percentage': (new_code_passes / total_scenarios) * 100
            }
        except Exception as e:
            self.logger.error(f"Error simulating test execution with GPT: {str(e)}")
            return {'old_code_pass_percentage': 0.0, 'new_code_pass_percentage': 0.0}


    def run_experiment_with_percentage(self, json_file: str):
        """Run the experiment with passing percentages."""
        changes = self.parse_json(json_file)
        results = []

        for change in changes:
            self.logger.info(f"Processing change {change.id}")
            
            # Generate test using GPT-4
            test_code = self.generate_test_with_gpt(change)
            if not test_code:
                continue

            # Simulate test execution with percentages
            simulation_result = self.simulate_test_execution_with_gpt_percentage(change, test_code)

            results.append({
                'id': change.id,
                'comment': change.old_comment,
                'old_code_pass_percentage': simulation_result['old_code_pass_percentage'],
                'new_code_pass_percentage': simulation_result['new_code_pass_percentage'],
                'potentially_inconsistent': simulation_result['old_code_pass_percentage'] != simulation_result['new_code_pass_percentage']
            })

        self._generate_percentage_report(results)

    def _generate_percentage_report(self, results: List[Dict]):
        """Generate a report of the experiment results with percentages."""
        self.logger.info("\n=== Comment Inconsistency Report with Percentages ===")
        total_changes = len(results)
        inconsistent_changes = sum(1 for r in results if r['potentially_inconsistent'])
        self.logger.info(f"\nTotal changes analyzed: {total_changes}")
        self.logger.info(f"Potentially inconsistent changes: {inconsistent_changes}")
        self.logger.info(f"Consistency rate: {((total_changes - inconsistent_changes) / total_changes) * 100:.2f}%\n")
        for result in results:
            self.logger.info(f"\nChange ID: {result['id']}")
            self.logger.info(f"Comment: {result['comment']}")
            self.logger.info(f"Old code pass percentage: {result['old_code_pass_percentage']:.2f}%")
            self.logger.info(f"New code pass percentage: {result['new_code_pass_percentage']:.2f}%")
            if result['potentially_inconsistent']:
                self.logger.warning("POTENTIAL INCONSISTENCY DETECTED: Comment may no longer be accurate")


def main():
    openai_key = ''
    generator = TestGenerator(openai_key)
    generator.run_experiment_with_percentage('trial2.json')

if __name__ == "__main__":
    main()
