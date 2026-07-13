#!/usr/bin/env python3
"""Run local, read-only pre-release checks for coffee-order-service."""

from __future__ import annotations

import argparse
import os
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[4]
REQUIRED_PATHS = (
    "README.md",
    "docs/API_SPEC.md",
    "src/main/resources/db/migration",
)


def run(command: list[str]) -> bool:
    return subprocess.run(command, cwd=ROOT, check=False).returncode == 0


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--skip-tests", action="store_true", help="Skip the full Gradle suite for a quick local inspection")
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    failures: list[str] = []

    for required in REQUIRED_PATHS:
        if (ROOT / required).exists():
            print(f"PASS required path: {required}")
        else:
            failures.append(f"missing required path: {required}")

    if run(["git", "diff", "--check"]):
        print("PASS git diff --check")
    else:
        failures.append("git diff --check failed")

    status = subprocess.run(["git", "status", "--short"], cwd=ROOT, text=True, capture_output=True, check=False)
    if status.stdout.strip():
        print("WARN worktree has uncommitted changes")
    else:
        print("PASS worktree is clean")

    if args.skip_tests:
        print("WARN Gradle suite skipped")
    else:
        gradlew = ROOT / ("gradlew.bat" if os.name == "nt" else "gradlew")
        if gradlew.is_file() and run([str(gradlew), "test", "--rerun-tasks"]):
            print("PASS Gradle test suite")
        else:
            failures.append("Gradle test suite failed")

    if failures:
        for failure in failures:
            print(f"FAIL {failure}", file=sys.stderr)
        return 1
    print("PASS release preflight completed")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
