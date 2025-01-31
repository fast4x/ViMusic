import re
import requests
import json

        
        
contributors: list[dict]
headers = {
    'Authorization': 'Bearer ${{ secrets.GITHUB_TOKEN }}',
    'Accept': 'application/vnd.github.v3+json'
}

needed_keys = ['login', 'id', 'avatar_url', 'html_url', 'contributions']
def filter_keys(entry: dict):
    """
    Deletes keys that are not defined in 'needed_keys'.
    The list may be appended in the future
    """
    for key in list(entry.keys()):
        if not key in needed_keys:
            del entry[key]


contributors_url: str = 'https://api.github.com/repos/fast4x/rimusic/contributors'
def fetch_contributors() -> None:
    """
    Gets all the contributors of this repository with Github API.
    """
    global contributors

    response = requests.get(contributors_url, headers=headers)
    # Convert response from Github API to list of entries
    contributors = json.loads(response.text)

    # Filter out unused keys to save space
    #for entry in contributors:
    #    filter_keys(entry)


filename: str = 'README.md'
section: str = '###  **Developer / Designer that contribute:**'
hyperlink_pattern = r'^-\s\[.*]\((https?://github.com/.*)\)$'
def registered_devs() -> list[str]:
    """
    Devs that are recognized in README.md
    Returns a list of urls
    """
    results: list[str] = []

    with open(filename, 'r') as file:
        found_header: bool = False
        for line in file.readlines():

            if found_header:
                match = re.match(hyperlink_pattern, line)
                
                if match:
                    results.append(match.group(1))
                else:
                    # Stop when there's no more matches
                    break

            if not found_header and line.startswith(section):
                found_header = True

    return results


if __name__ == '__main__':
    fetch_contributors()
    if len(contributors) == 0:
        print(f'Could not fetch contributors')

    registered = registered_devs()

    devs: dict = {}
    for entry in contributors:
        dev_url: str = entry['html_url']
        
        if dev_url in registered:
            devs[dev_url] = entry

    github_id_pattern = r'https?://github.com/(.*)'
    for dev_url in registered:
        if not dev_url in list(devs.keys()):
            match = re.match(github_id_pattern, dev_url)
            if not match:
                continue

            response = requests.get(f'https://api.github.com/users/{match.group(1)}')
            if response.status_code != 200:
                print(f'Failed to get developer\'s information. Skipping...')
                print(response.text)
                continue

            dev_json = json.loads(response.text)
            filter_keys(dev_json)

            devs[dev_url] = dev_json

    with open('contributors.json', 'w') as file:
        file.write(json.dumps(list(devs.values()), indent=2))


