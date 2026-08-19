#!/usr/bin/env bash

set -euo pipefail

# Build all images by executing each step*/build.sh in path alphabetical order.

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${REPO_ROOT}"

mapfile -t build_scripts < <(find step* -maxdepth 1 -type f -name build.sh | sort)

if [ "${#build_scripts[@]}" -eq 0 ]; then
  echo "No step*/build.sh scripts found."
  exit 1
fi

echo "Found ${#build_scripts[@]} build script(s):"
printf ' - %s\n' "${build_scripts[@]}"

for build_script in "${build_scripts[@]}"; do
  echo
  echo "Running ${build_script}"
  # Each build.sh uses paths relative to its own directory (./Dockerfile, .),
  # so it must be run with that directory as the CWD, not the repo root.
  step_dir="$(dirname "${build_script}")"
  script_name="$(basename "${build_script}")"
  ( cd "${REPO_ROOT}/${step_dir}" && bash "${script_name}" )
done

echo
echo "All builds completed."
