#!/usr/bin/env python3
"""Run one Gradle test class repeatedly and stop at the first failed attempt."""

from __future__ import annotations

import argparse
import os
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[4]
DEFAULT_TEST = "com.example.coffeeorderservice.point.PointConcurrencyIntegrationTest"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--test", default=DEFAULT_TEST, help=f"Fully qualified test class (default: {DEFAULT_TEST})")
    parser.add_argument("--runs", type=int, default=5, help="Number of executions (default: 5)")
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    if args.runs < 1:
        print("--runs must be at least 1", file=sys.stderr)
        return 2

    gradlew = ROOT / ("gradlew.bat" if os.name == "nt" else "gradlew")
    if not gradlew.is_file():
        print(f"Gradle wrapper not found: {gradlew}", file=sys.stderr)
        return 2

    for attempt in range(1, args.runs + 1):
        print(f"[concurrency-check] {attempt}/{args.runs}: {args.test}")
        result = subprocess.run(
            [str(gradlew), "test", "--tests", args.test, "--rerun-tasks"],
            cwd=ROOT,
            check=False,
        )
        if result.returncode != 0:
            print(f"[concurrency-check] failed at run {attempt} with exit code {result.returncode}", file=sys.stderr)
            return result.returncode

    print(f"[concurrency-check] passed {args.runs}/{args.runs} runs")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
