import json
import re
from os import environ

from bs4 import BeautifulSoup as BSoup, Tag
from requests import get, Response

"""
Append more keys if needed, refrain from taking all.
More elements mean more loading time.
"""
contributors_keys = ['login', 'id', 'name', 'avatar_url', 'html_url', 'contributions']
def filter_keys(entry: dict) -> None:
    # Keys must be converted to list to
    # prevent "modify-during-loop"
    for key in list(entry.keys()):
        if key not in contributors_keys:
            del entry[key]


"""
This pattern matches this string below:
- https://avatars.githubusercontent.com/u/68310158?v=4
with a few keys to notice:
1. HTTP and HTTPS are accepted
2. User's id (68310158) must be a number
3. `?v=4` is totally optional
"""
avatar_url_regex = r"https?:\/\/avatars\.githubusercontent\.com\/u\/(\d+)(\?v=\d+)?"
def extract_user_id(img_tag: Tag) -> str:
    """
    Extracts and returns user's id from <img> tag.
    If the id is not found, `-1` will be returned

    :param img_tag: Must be img tag.
    :return: user's id from "src"
    :rtype: str
    :raise ValueError: if [img_tag] isn't <img>
    """

    if img_tag.name != 'img':
        raise ValueError('not a <img> tag')

    match = re.match(avatar_url_regex, img_tag['src'])
    return match.group(1) if match else -1


contributors_url = f'https://api.github.com/repos/{environ["GITHUB_REPOSITORY"]}/contributors'
def get_latest_contributors() -> list[dict]:
    """
    Gets all the contributors of this repository via GitHub API.
    """

    response: Response = get(contributors_url)
    # Convert JSON string returned from GitHub API
    # to dict-like object (easier to work on).
    latest_contributors = json.loads(response.text)

    #
    # Filter out unwanted keys of each dev to save some space
    #

    # Each 'entry' represents a developer
    for entry in latest_contributors:
        filter_keys(entry)

    return latest_contributors


def get_recognized_contributor_ids() -> list[int]:
    """
    Reads all devs listed in README.md and extracts
    each dev "id" from their profile's url
    """

    with open('README.md', 'r') as readme_file:
        file_data: str = readme_file.read()

    # Parse only HTML code from README.md
    soup: BSoup = BSoup(file_data, 'html.parser')
    table: Tag = soup.find('table')

    user_ids: list[int] = []
    for img in table.find_all('img'):
        dev_id: str = extract_user_id(img)

        # Convert `str` to `int`
        try:
            user_ids.append(int(dev_id))
        except ValueError:
            # Don't do anything
            pass

    return user_ids


#
#    It STARTS here
#
if __name__ == '__main__':
    # Get latest contributors
    contributors: list[dict] = get_latest_contributors()
    # Get recognized contributor ids
    recognized_contributors_ids: list[int] = get_recognized_contributor_ids()

    #
    #  Filter out recognized contributors
    #
    recognized_contributors: list[dict] = []
    for contributor in contributors:
        if contributor['id'] in recognized_contributors_ids:
            recognized_contributors.append(contributor)

    #
    #   Write contributors to a file named `contributors.json`
    #
    json_format: str = json.dumps(recognized_contributors)
    with open('contributors.json', 'w') as file:
        file.write(json_format)
