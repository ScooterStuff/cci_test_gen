import json
import logging
from dataclasses import dataclass
from typing import List, Dict
import openai
import os


@dataclass
class CodeChange:
    id: str
    old_comment: str
    new_comment: str
    old_code: str
    new_code: str


class TestGenerator:
    def __init__(self, openai_key: str, output_dir: str = "test_results"):
        self.logger = logging.getLogger(__name__)
        logging.basicConfig(level=logging.INFO)
        openai.api_key = openai_key
        self.output_dir = output_dir

        # Ensure the output directory exists
        os.makedirs(self.output_dir, exist_ok=True)

    def parse_json(self, json_file: str) -> List[CodeChange]:
        """Parse the input JSON file containing code changes."""
        with open(json_file, 'r') as f:
            data = json.load(f)
        return [
            CodeChange(
                id=item['id'],
                old_comment=item['old_comment_raw'],
                new_comment=item['new_comment_raw'],
                old_code=item['old_code_raw'],
                new_code=item['new_code_raw'],
            )
            for item in data
        ]

    def generate_smart_test_with_gpt(self, change: CodeChange) -> str:
        """Generate a smart test case that passes the old code but fails the new code."""
        prompt = f"""
        You are a Java testing expert. Your task is to analyze the following:

        Old Comment (behavior expectations for the test case):
        {change.old_comment}

        New Comment (provides context about changes):
        {change.new_comment}

        Old Code:
        {change.old_code}

        New Code:
        {change.new_code}

        Your tasks:
        1. Create a Java test case that aligns strictly with the behavior described in the old comment.
        2. Ensure the test case focuses on the behavioral assumptions of the old comment.
        3. Include edge cases where the old behavior is expected to pass but should fail in the new implementation due to changes.

        Note:
        - Do not test new behavior explicitly, but consider how the new implementation diverges from the old behavior.
        - Focus the test on detecting missing or altered functionality based on the old comment.

        Provide a single Java test case, with no explanations or extra text, formatted as follows:

        @Test
        public void testKeyDifference() {{
            // Your test case implementation
        }}
        """
        try:
            response = openai.ChatCompletion.create(
                model="gpt-4",
                messages=[
                    {"role": "system", "content": "You are a Java testing and code analysis expert."},
                    {"role": "user", "content": prompt},
                ],
                temperature=0.2,
                max_tokens=800,
            )
            test_case = response.choices[0].message.content.strip()
            self.logger.info(f"Generated test case for {change.id}:\n{test_case}\n")
            return test_case
        except Exception as e:
            self.logger.error(f"Error generating test with GPT: {str(e)}")
            return None


    def simulate_test_execution_with_gpt(self, change: CodeChange, test_case: str) -> Dict[str, str]:
        """
        Simulate whether the test case passes or fails for old and new code
        and generate explanations for inconsistencies.
        """
        prompt = f"""
        You are a Java expert. I will provide you with:
        - A Java test case.
        - An old Java method implementation.
        - A new Java method implementation.

        Your task:
        - Analyze the provided test case and determine the exact scenarios being tested.
        - Simulate the test case execution for both the old and new code.
        - For each part of the test case, tell me if it PASSES or FAILS for the old implementation.
        - For each part of the test case, tell me if it PASSES or FAILS for the new implementation.
        - Provide an explanation for any discrepancies.

        Test Case:
        {test_case}

        Old Code:
        {change.old_code}

        New Code:
        {change.new_code}

        Provide your response in this format:
        Scenario 1: Description of the scenario
        Old Code: PASS/FAIL
        New Code: PASS/FAIL
        Explanation: Reason for the pass/fail result (if applicable)

        Scenario 2: Description of the scenario
        Old Code: PASS/FAIL
        New Code: PASS/FAIL
        Explanation: Reason for the pass/fail result (if applicable)
        """
        try:
            response = openai.ChatCompletion.create(
                model="gpt-3.5-turbo",
                messages=[
                    {"role": "system", "content": "You are a Java testing and code analysis expert."},
                    {"role": "user", "content": prompt},
                ],
                temperature=0,
                max_tokens=1000,
            )
            result_text = response.choices[0].message.content.strip()
            self.logger.info(f"Simulation result for change {change.id}:\n{result_text}")
            return result_text
        except Exception as e:
            self.logger.error(f"Error simulating test execution with GPT: {str(e)}")
            return ""

    def run_experiment(self, json_file: str):
        """Run the experiment and generate results."""
        changes = self.parse_json(json_file)
        results = []

        for change in changes:
            if change.old_comment == change.new_comment:
                self.logger.info(f"Skipping {change.id} as comments are identical.")
                continue

            self.logger.info(f"Processing change {change.id}")

            # Generate a smart test using GPT-4
            test_case = self.generate_smart_test_with_gpt(change)
            if not test_case:
                continue

            # Simulate test execution with GPT-4
            simulation_result = self.simulate_test_execution_with_gpt(change, test_case)

            # Save test case to a file
            self._save_test_case(change.id, test_case)

            results.append({
                'id': change.id,
                'old_comment': change.old_comment,
                'new_comment': change.new_comment,
                'test_case': test_case,
                'simulation_result': simulation_result,
            })

        self._generate_report(results)

    def _save_test_case(self, change_id: str, test_case: str):
        """Save the generated test case to a file."""
        test_case_file = os.path.join(self.output_dir, f"{change_id}_test_case.java")
        with open(test_case_file, 'w') as f:
            f.write(test_case)
        self.logger.info(f"Test case saved to {test_case_file}")

    def _generate_report(self, results: List[Dict]):
        """Generate a report of the experiment results with test cases and explanations."""
        report_file = os.path.join(self.output_dir, "experiment_report.json")
        with open(report_file, 'w') as f:
            json.dump(results, f, indent=4)
        self.logger.info(f"Experiment report saved to {report_file}")

        # Log summary
        self.logger.info("\n=== Experiment Report ===")
        total_changes = len(results)
        inconsistent_changes = sum(1 for r in results if "FAIL" in r['simulation_result'])
        self.logger.info(f"Total changes analyzed: {total_changes}")
        self.logger.info(f"Potentially inconsistent changes: {inconsistent_changes}")
        self.logger.info(f"Consistency rate: {((total_changes - inconsistent_changes) / total_changes) * 100:.2f}%\n")


def main():
    openai_key = ''  # Replace with your OpenAI API key
    if not openai_key:
        raise ValueError("Please set the OPENAI_API_KEY environment variable")
    generator = TestGenerator(openai_key)
    generator.run_experiment('trial_final.json')  # Replace 'trial2.json' with your JSON file


if __name__ == "__main__":
    main()
