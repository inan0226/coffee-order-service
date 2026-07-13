#!/usr/bin/env python3
"""민감정보를 가린 트러블슈팅 기록과 로컬 로그 발췌를 저장합니다."""

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

STATUS_LABELS = {
    "open": "열림",
    "investigating": "조사 중",
    "resolved": "해결",
    "not-reproduced": "재현되지 않음",
}
CATEGORY_LABELS = {
    "test": "테스트",
    "build": "빌드",
    "runtime": "실행 환경",
    "api": "API",
    "database": "데이터베이스",
    "concurrency": "동시성",
    "security": "보안",
    "performance": "성능",
}


def redact(value: str) -> str:
    for pattern, replacement in REDACTIONS:
        value = pattern.sub(replacement, value)
    return value


def text_or_dash(value: str | None) -> str:
    return redact(value).strip() if value else "-"


def journal_header() -> str:
    return (
        "# 트러블슈팅 저널\n\n"
        "테스트와 실행 중 발견한 문제의 재현 방법, 원인, 변경 사항, 검증 결과를 남깁니다. "
        "민감한 값은 기록하지 않습니다.\n"
    )


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--title", required=True, help="기록 제목")
    parser.add_argument("--category", required=True, choices=tuple(CATEGORY_LABELS), help="문제 분류")
    parser.add_argument("--status", required=True, choices=tuple(STATUS_LABELS), help="현재 상태")
    parser.add_argument("--command", required=True, help="재현 또는 검증에 사용한 명령")
    parser.add_argument("--exit-code", required=True, type=int, help="명령 종료 코드")
    parser.add_argument("--symptom", required=True, help="관찰한 증상")
    parser.add_argument("--root-cause", help="확인한 원인")
    parser.add_argument("--change", help="적용한 변경")
    parser.add_argument("--validation", help="검증 결과")
    parser.add_argument("--log-file", type=Path, help="민감정보가 없는 원본 로그 파일")
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

- 상태: `{STATUS_LABELS[args.status]}({args.status})`
- 분류: `{CATEGORY_LABELS[args.category]}({args.category})`
- 실행 명령: `{text_or_dash(args.command)}`
- 종료 코드: `{args.exit_code}`
- 증상: {text_or_dash(args.symptom)}
- 원인: {text_or_dash(args.root_cause)}
- 변경: {text_or_dash(args.change)}
- 검증: {text_or_dash(args.validation)}
- 로컬 로그 발췌: `{log_path}`
"""
    with JOURNAL.open("a", encoding="utf-8") as journal:
        journal.write(record)
    print(f"진단 기록을 저장했습니다: {JOURNAL.relative_to(ROOT)}")
    if log_path != "-":
        print(f"민감정보를 가린 로컬 로그 발췌를 저장했습니다: {log_path}")


if __name__ == "__main__":
    main()
