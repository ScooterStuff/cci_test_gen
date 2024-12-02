import json
import javalang
import subprocess
import os
from typing import List, Dict
import tempfile
import logging
from dataclasses import dataclass
import openai
import re

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

    def extract_method_info(self, code: str) -> dict:
        """Extract method information from Java code."""
        try:
            # Add class wrapper if not present
            if not code.strip().startswith("public class"):
                code = f"public class TempClass {{ {code} }}"
            
            tree = javalang.parse.parse(code)
            for path, node in tree.filter(javalang.tree.MethodDeclaration):
                return {
                    'name': node.name,
                    'return_type': node.return_type.name if node.return_type else 'void',
                    'parameters': [(param.type.name, param.name) for param in node.parameters] if node.parameters else []
                }
        except Exception as e:
            self.logger.error(f"Error parsing method: {str(e)}")
            return None

    def generate_test_with_gpt(self, change: CodeChange, method_info: dict) -> str:
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
                assertTrue(instance.isSorted(new ArrayList<>()));
            }}

            @Test
            public void testInvalidInputs() {{
                assertFalse(instance.isSorted(Arrays.asList(5, 4, 3, 2, 1)));
            }}
        }}
        """

        try:
            response = openai.ChatCompletion.create(
                model="gpt-4",
                messages=[
                    {"role": "system", "content": "You are a Java testing expert. Generate ONLY the exact Java code following the template. NO public modifiers allowed."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.7,
                max_tokens=2000
            )
            
            test_code = response.choices[0].message.content.strip()
            # Clean up response and ensure no public modifiers
            test_code = test_code.replace("```java", "").replace("```", "").strip()
            test_code = test_code.replace("public class", "class")
            
            self.logger.info(f"\nGenerated test for {change.id}:\n{test_code}\n")
            
            return test_code
        except Exception as e:
            self.logger.error(f"Error generating test with GPT: {str(e)}")
            return None

    def create_test_file(self, test_code: str, temp_dir: str, file_name: str) -> str:
        """Create a temporary Java test file."""
        # Clean up the file name for Java
        safe_file_name = file_name.replace('-', '_')
        
        # Create package directory
        package_dir = os.path.join(temp_dir, "src", "test")
        os.makedirs(package_dir, exist_ok=True)

        # Remove any public modifiers
        test_code = test_code.replace("public class", "class")

        # Write file
        file_path = os.path.join(package_dir, f"{safe_file_name}.java")
        with open(file_path, 'w') as f:
            f.write(test_code)
            
        return file_path

    def compile_and_run_tests(self, test_file: str, temp_dir: str) -> bool:
        """Compile and run the generated test."""
        try:
            # Setup directories
            class_path = os.path.join(temp_dir, "target", "classes")
            src_dir = os.path.join(temp_dir, "src")
            os.makedirs(class_path, exist_ok=True)

            # Build classpath
            classpath_elements = [
                class_path,
                os.path.join(temp_dir, "junit-4.13.2.jar"),
                os.path.join(temp_dir, "hamcrest-core-1.3.jar"),
                src_dir
            ]
            classpath = os.pathsep.join(classpath_elements)

            # Compile
            compile_cmd = f'javac -d "{class_path}" -cp "{classpath}" "{test_file}"'
            self.logger.info(f"Compiling with command: {compile_cmd}")
            
            compile_result = subprocess.run(
                compile_cmd,
                shell=True,
                capture_output=True,
                text=True
            )

            if compile_result.returncode != 0:
                self.logger.error(f"Compilation failed:\n{compile_result.stderr}")
                return False

            # Get class name without extension and with package
            class_name = "test." + os.path.basename(test_file)[:-5]

            # Run tests
            run_classpath = os.pathsep.join([class_path] + classpath_elements)
            run_cmd = f'java -cp "{run_classpath}" org.junit.runner.JUnitCore {class_name}'
            self.logger.info(f"Running tests with command: {run_cmd}")
            
            run_result = subprocess.run(
                run_cmd,
                shell=True,
                capture_output=True,
                text=True
            )

            if run_result.returncode != 0:
                self.logger.error(f"Test execution failed:\n{run_result.stdout}")
            else:
                self.logger.info(f"Test execution successful:\n{run_result.stdout}")
            
            return run_result.returncode == 0

        except Exception as e:
            self.logger.error(f"Error: {str(e)}")
            return False

    def download_dependencies(self, temp_dir: str):
        """Download only essential JUnit dependencies."""
        dependencies = {
            "junit-4.13.2.jar": "https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar",
            "hamcrest-core-1.3.jar": "https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"
        }
        
        for filename, url in dependencies.items():
            filepath = os.path.join(temp_dir, filename)
            subprocess.run(['curl', '-o', filepath, url], check=True)

    def run_experiment(self, json_file: str):
        """Run the complete experiment."""
        changes = self.parse_json(json_file)
        results = []

        with tempfile.TemporaryDirectory() as temp_dir:
            # Download dependencies
            self.download_dependencies(temp_dir)

            for change in changes:
                self.logger.info(f"Processing change {change.id}")
                
                # Extract method information
                method_info = self.extract_method_info(change.old_code)
                if not method_info:
                    continue

                # Generate test using GPT
                test_code = self.generate_test_with_gpt(change, method_info)
                if not test_code:
                    continue

                # Test old code
                old_test_file = self.create_test_file(test_code, temp_dir, f"Test_{change.id}")
                old_code_result = self.compile_and_run_tests(old_test_file, temp_dir)

                # Modify test for new code
                new_test_code = self.modify_test_for_new_code(test_code, change)
                new_test_file = self.create_test_file(new_test_code, temp_dir, f"Test_{change.id}_new")
                new_code_result = self.compile_and_run_tests(new_test_file, temp_dir)

                results.append({
                    'id': change.id,
                    'comment': change.old_comment,
                    'old_code_passes': old_code_result,
                    'new_code_passes': new_code_result,
                    'potentially_inconsistent': old_code_result != new_code_result
                })

        self._generate_report(results)

    def modify_test_for_new_code(self, test_code: str, change: CodeChange) -> str:
        """Modify the test code to use the new implementation."""
        # Escape special characters in the old code
        old_code_escaped = re.escape(change.old_code.strip())
        new_code = change.new_code.strip()
        
        # Replace the old code with the new code
        return re.sub(old_code_escaped, new_code, test_code)

    def _generate_report(self, results: List[Dict]):
        """Generate a report of the experiment results."""
        self.logger.info("\n=== Comment Inconsistency Report ===")
        
        total_changes = len(results)
        inconsistent_changes = sum(1 for r in results if r['potentially_inconsistent'])
        
        self.logger.info(f"\nTotal changes analyzed: {total_changes}")
        self.logger.info(f"Potentially inconsistent changes: {inconsistent_changes}")
        self.logger.info(f"Consistency rate: {((total_changes - inconsistent_changes) / total_changes) * 100:.2f}%\n")
        
        for result in results:
            self.logger.info(f"\nChange ID: {result['id']}")
            self.logger.info(f"Comment: {result['comment']}")
            self.logger.info(f"Old code test passed: {result['old_code_passes']}")
            self.logger.info(f"New code test passed: {result['new_code_passes']}")
            if result['potentially_inconsistent']:
                self.logger.warning("POTENTIAL INCONSISTENCY DETECTED: Comment may no longer be accurate")

def main():
    openai_key = ''
    
    generator = TestGenerator(openai_key)
    generator.run_experiment('trial2.json')

if __name__ == "__main__":
    main()