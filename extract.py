import json

def extract_specific_fields(input_data):
    # Initialize result list
    result = []
    
    # Process each item in the input data
    for item in input_data:
        # Create new dict with only the required fields
        filtered_item = {
            'id': item['id'],
            'old_comment_raw': item['old_comment_raw'],
            'new_comment_raw': item['new_comment_raw'],
            'old_code_raw': item['old_code_raw'],
            'new_code_raw': item['new_code_raw']
        }
        result.append(filtered_item)
    
    return result

def main():
    # Read input JSON
    with open('test3.json', 'r') as f:
        data = json.load(f)
    
    # Extract specific fields
    filtered_data = extract_specific_fields(data)
    
    # Write output to new JSON file
    with open('output3.json', 'w') as f:
        json.dump(filtered_data, f, indent=2)

if __name__ == '__main__':
    main()