"""
Builds docs/COVERAGE_MATRIX.csv: every workbook test case (LOGIN-TC-001..117)
mapped to the scenario that automates it, or to the reason it is not automated.

Run from the repository root:  python3 docs/build_coverage_matrix.py path/to/Test_Suit_1.xlsx
"""
import csv
import glob
import os
import re
import sys

import openpyxl

NOT_AUTOMATED = {
    "LOGIN-TC-027": "Needs the OTP service stopped; requires environment control",
    "LOGIN-TC-030": "Needs real OTP expiry window; Firebase test codes do not expire",
    "LOGIN-TC-060": "Needs real OTP expiry window and exact expiry copy check",
    "LOGIN-TC-062": "Firebase test number always issues 123456, so old and new codes cannot be told apart",
    "LOGIN-TC-066": "Needs network offline emulation (DevTools CDP); planned",
    "LOGIN-TC-067": "Needs backend stopped mid-flow; requires environment control",
    "LOGIN-TC-068": "Needs backend started with AUTH_MODE=deny; separate environment",
    "LOGIN-TC-069": "Needs a UI build without VITE_FIREBASE_* values; separate build",
    "LOGIN-TC-070": "Needs a real non-test number on the Firebase free plan; real SMS is out of scope",
    "LOGIN-TC-071": "Needs backend down at page load; requires environment control",
    "LOGIN-TC-074": "Needs RM staff test account and mobile number (not supplied)",
    "LOGIN-TC-075": "Needs SRM staff test account and mobile number (not supplied)",
    "LOGIN-TC-076": "Needs Admin staff test account and mobile number (not supplied)",
    "LOGIN-TC-077": "Needs a number registered as both client and RM (test data)",
    "LOGIN-TC-078": "Needs two client records sharing a mobile number (test data)",
    "LOGIN-TC-079": "Needs a suspended staff test account (test data)",
    "LOGIN-TC-083": "Needs clarification of how the saved return path is set",
    "LOGIN-TC-084": "Google account chooser needs a real Google account; manual",
    "LOGIN-TC-085": "Google popup close needs a real Google popup; manual",
    "LOGIN-TC-086": "Browser popup blocking is a browser setting; manual",
    "LOGIN-TC-087": "Needs a non-company Google account (test data)",
    "LOGIN-TC-088": "Needs an unregistered company Google account (test data)",
    "LOGIN-TC-091": "Needs a Google-only test account to reach the link-mobile step (test data)",
    "LOGIN-TC-093": "Needs a Google account and a phone for the link flow (test data)",
    "LOGIN-TC-094": "Needs a Google account already registered elsewhere (test data)",
    "LOGIN-TC-099": "Two-tab idle timing; planned as @slow",
    "LOGIN-TC-100": "Closing the only browser window cannot be asserted reliably in Selenium; manual",
    "LOGIN-TC-102": "Closing the whole browser; manual",
    "LOGIN-TC-103": "Needs 12-hour server session expiry; manual",
    "LOGIN-TC-108": "Needs unknown, duplicate and suspended numbers to compare messages (test data)",
    "LOGIN-TC-115": "Cross-browser coverage; planned as a CI browser matrix (Chrome only today)",
    "LOGIN-TC-116": "200% zoom visual check; manual",
    "LOGIN-TC-117": "Splash on slow network; manual timing check",
}


def read_workbook(path):
    cases = []
    wb = openpyxl.load_workbook(path, read_only=True)
    for ws in wb.worksheets[:2]:  # the two login tabs
        for row in ws.iter_rows(values_only=True):
            if row and row[0] and str(row[0]).startswith("LOGIN-TC-"):
                cases.append({
                    "id": str(row[0]).strip(),
                    "sheet": ws.title,
                    "title": str(row[3] or "").strip(),
                    "workbook_status": str(row[8] or "").strip(),
                })
    return cases


def read_features(root):
    found = {}
    for path in sorted(glob.glob(os.path.join(root, "src/test/resources/features/login/*.feature"))):
        pending_tags = []
        for line in open(path, encoding="utf-8"):
            s = line.strip()
            if s.startswith("@"):
                pending_tags.extend(re.findall(r"@[\w-]+", s))
            elif s.startswith("Scenario"):
                tags = set(pending_tags)
                for tc in (t[1:] for t in tags if t.startswith("@LOGIN-TC-")):
                    found[tc] = {
                        "file": os.path.basename(path),
                        "scenario": s.split(":", 1)[-1].strip(),
                        "tags": sorted(t for t in tags if not t.startswith("@LOGIN-TC-")),
                    }
                pending_tags = []
            elif s:
                pending_tags = []
    return found


def automation_status(entry):
    tags = set(entry["tags"])
    if "@known-defect" in tags:
        return "Automated - known defect (expected to fail until fixed)"
    if tags & {"@needs-test-data"}:
        return "Automated - blocked on test data"
    if tags & {"@needs-clarification"}:
        return "Automated - blocked on requirement clarification"
    if tags & {"@pending-feature"}:
        return "Automated - feature not built yet"
    if tags & {"@destructive"}:
        return "Automated - destructive (own CI job)"
    if tags & {"@slow"}:
        return "Automated - slow (nightly)"
    if "@smoke" in tags:
        return "Automated - PR smoke"
    return "Automated - regression"


def main(workbook):
    root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    cases = read_workbook(workbook)
    features = read_features(root)
    out = os.path.join(root, "docs", "COVERAGE_MATRIX.csv")
    with open(out, "w", newline="", encoding="utf-8") as fh:
        w = csv.writer(fh)
        w.writerow(["Test case ID", "Workbook tab", "Title", "Workbook status",
                    "Automation status", "Feature file", "Scenario", "Tags", "Reason if not automated"])
        counts = {}
        for c in cases:
            if c["id"] in features:
                f = features[c["id"]]
                status = automation_status(f)
                w.writerow([c["id"], c["sheet"], c["title"], c["workbook_status"], status,
                            f["file"], f["scenario"], " ".join(f["tags"]), ""])
            else:
                status = "Not automated"
                w.writerow([c["id"], c["sheet"], c["title"], c["workbook_status"], status,
                            "", "", "", NOT_AUTOMATED.get(c["id"], "Not yet mapped")])
            key = status.split(" - ")[0] if status.startswith("Automated") else status
            counts[key] = counts.get(key, 0) + 1
    print(f"wrote {out}")
    for k, v in sorted(counts.items()):
        print(f"  {k}: {v}")


if __name__ == "__main__":
    main(sys.argv[1] if len(sys.argv) > 1 else "/mnt/user-data/uploads/Test_Suit_1.xlsx")
