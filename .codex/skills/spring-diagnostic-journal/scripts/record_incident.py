#!/usr/bin/env python3
"""Append a redacted troubleshooting record and retain a local log excerpt."""

from __future__ import annotations

import argparse
import re
from datetime import datetime, timezone
from pathlib import Path

MAX_LOG_CHARS = 30_000
ROOT = Path(__file__).resolve().parents[4]
JOURNAL = ROOT / "docs" / "troubleshooting" / "INCIDENTS.md"
LOG_DIR = ROOT / ".codex" / "troubleshooting" / "logs"

REDACTIONS = (
    (re.compile(r"(?i)(password|passwd|secret|token|api[-_]?key)\s*([=:])\s*([^\s,;]+)"), r"\1\2[REDACTED]"),
    (re.compile(r"(?i)(authorization\s*:\s*bearer)\s+[^\s]+"), r"\1 [REDACTED]"),
    (re.compile(r"(?i)(jdbc:[^\s]*//[^:/\s]+:)([^@/\s]+)(@)"), r"\1[REDACTED]\3"),
)


def redact(value: str) -> str:
    for pattern, replacement in REDACTIONS:
        value = pattern.sub(replacement, value)
    return value


def text_or_dash(value: str | None) -> str:
    return redact(value).strip() if value else "-"


def journal_header() -> str:
    return "# Troubleshooting Journal\n\nRedacted records of reproducible failures and their validation.\n"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--title", required=True)
    parser.add_argument("--category", required=True, choices=("test", "build", "runtime", "api", "database", "concurrency", "security", "performance"))
    parser.add_argument("--status", required=True, choices=("open", "investigating", "resolved", "not-reproduced"))
    parser.add_argument("--command", required=True)
    parser.add_argument("--exit-code", required=True, type=int)
    parser.add_argument("--symptom", required=True)
    parser.add_argument("--root-cause")
    parser.add_argument("--change")
    parser.add_argument("--validation")
    parser.add_argument("--log-file", type=Path)
    return parser.parse_args()


def save_log(log_file: Path | None, stamp: str) -> str:
    if log_file is None:
        return "-"
    source = log_file if log_file.is_absolute() else ROOT / log_file
    raw = source.read_text(encoding="utf-8", errors="replace")
    excerpt = redact(raw[-MAX_LOG_CHARS:])
    LOG_DIR.mkdir(parents=True, exist_ok=True)
    target = LOG_DIR / f"{stamp}.log"
    target.write_text(excerpt, encoding="utf-8")
    return target.relative_to(ROOT).as_posix()


def main() -> None:
    args = parse_args()
    now = datetime.now(timezone.utc).astimezone()
    stamp = now.strftime("%Y%m%d-%H%M%S")
    log_path = save_log(args.log_file, stamp)
    JOURNAL.parent.mkdir(parents=True, exist_ok=True)
    if not JOURNAL.exists():
        JOURNAL.write_text(journal_header(), encoding="utf-8")

    record = f"""

## {now.strftime('%Y-%m-%d %H:%M %z')} - {text_or_dash(args.title)}

- Status: `{args.status}`
- Category: `{args.category}`
- Command: `{text_or_dash(args.command)}`
- Exit code: `{args.exit_code}`
- Symptom: {text_or_dash(args.symptom)}
- Root cause: {text_or_dash(args.root_cause)}
- Change: {text_or_dash(args.change)}
- Validation: {text_or_dash(args.validation)}
- Local log excerpt: `{log_path}`
"""
    with JOURNAL.open("a", encoding="utf-8") as journal:
        journal.write(record)
    print(f"Recorded diagnostic entry in {JOURNAL.relative_to(ROOT)}")
    if log_path != "-":
        print(f"Saved redacted local log excerpt to {log_path}")


if __name__ == "__main__":
    main()
