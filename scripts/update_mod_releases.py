#!/usr/bin/env python3
"""
Scan step20-mods/Dockerfile for lines calling ./downloadAndInstallMod.sh owner/repo/releases/<id>
Query GitHub API to find the latest release numeric id for each owner/repo and produce an updated Dockerfile
Writes: step20-mods/Dockerfile.updated

Usage: set GITHUB_TOKEN env var (optional) to increase rate limits
"""
import os
import re
import sys
import json
from urllib.request import Request, urlopen
from urllib.error import HTTPError, URLError
from datetime import datetime

DOCKERFILE = os.path.join(os.path.dirname(__file__), '..', 'step20-mods', 'Dockerfile')
OUT_FILE = DOCKERFILE + '.updated'
GITHUB_API = 'https://api.github.com'
TOKEN = os.environ.get('GITHUB_TOKEN')

pattern = re.compile(r"(downloadAndInstallMod\.sh\s+)([A-Za-z0-9_.-]+)/([A-Za-z0-9_.-]+)/releases/(\d+)")

headers = {'User-Agent': 'luanti-updater-script/1.0'}
if TOKEN:
    headers['Authorization'] = f'token {TOKEN}'


def gh_get(url):
    req = Request(url, headers=headers)
    try:
        with urlopen(req, timeout=30) as resp:
            return json.load(resp)
    except HTTPError as e:
        return {'__http_error__': e.code, 'message': getattr(e, 'reason', str(e))}
    except URLError as e:
        return {'__http_error__': None, 'message': str(e)}


def get_latest_release_id(owner, repo):
    # Try /repos/{owner}/{repo}/releases/latest
    url = f'{GITHUB_API}/repos/{owner}/{repo}/releases/latest'
    data = gh_get(url)
    if isinstance(data, dict) and data.get('__http_error__'):
        # 404 likely means no releases
        # Fallback to list releases
        url2 = f'{GITHUB_API}/repos/{owner}/{repo}/releases?per_page=100'
        data2 = gh_get(url2)
        if isinstance(data2, dict) and data2.get('__http_error__'):
            return None, f'API error listing releases: {data2.get("message")}'
        if isinstance(data2, list) and data2:
            # pick most recent by published_at
            best = max(data2, key=lambda r: r.get('published_at') or '')
            return best.get('id'), None
        # no releases, try tags
        url3 = f'{GITHUB_API}/repos/{owner}/{repo}/tags?per_page=1'
        data3 = gh_get(url3)
        if isinstance(data3, dict) and data3.get('__http_error__'):
            return None, f'API error listing tags: {data3.get("message")}'
        if isinstance(data3, list) and data3:
            tag = data3[0].get('name')
            return None, f'No releases, latest tag: {tag}'
        return None, 'No releases or tags found'
    # success
    if isinstance(data, dict) and 'id' in data:
        return data['id'], None
    return None, 'Unexpected response'


def main():
    with open(DOCKERFILE, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    replacements = []  # tuples (line_no, owner, repo, old_id, new_id, note)
    cache = {}

    for i, line in enumerate(lines):
        stripped = line.lstrip()
        if stripped.startswith('#'):
            continue
        m = pattern.search(line)
        if not m:
            continue
        owner, repo, old_id = m.group(2), m.group(3), m.group(4)
        key = (owner, repo)
        if key in cache:
            new_id, note = cache[key]
        else:
            new_id, note = get_latest_release_id(owner, repo)
            cache[key] = (new_id, note)
        replacements.append((i, owner, repo, old_id, new_id, note))

    if not replacements:
        print('No matching lines found.')
        sys.exit(0)

    # Print summary
    print('Found entries:')
    for (i, owner, repo, old_id, new_id, note) in replacements:
        ln = i+1
        if new_id is None:
            print(f'  line {ln}: {owner}/{repo} old={old_id} -> (SKIP) note={note}')
        else:
            print(f'  line {ln}: {owner}/{repo} old={old_id} -> new={new_id}')

    # Create updated content
    new_lines = lines.copy()
    for (i, owner, repo, old_id, new_id, note) in replacements:
        if new_id is None:
            continue
        old_segment = f'{owner}/{repo}/releases/{old_id}'
        new_segment = f'{owner}/{repo}/releases/{new_id}'
        new_lines[i] = new_lines[i].replace(old_segment, new_segment)

    # Write output file
    with open(OUT_FILE, 'w', encoding='utf-8') as f:
        f.writelines(new_lines)

    print('\nWrote updated Dockerfile to:', OUT_FILE)
    print('\nRun: diff -u', DOCKERFILE, OUT_FILE)

if __name__ == '__main__':
    main()

