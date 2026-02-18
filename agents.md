# Antigravity Mission: Cross-Repo Architectural Evolution

## Workspace Configuration
- **Source (ReadOnly):** `C:\Users\richa\Documents\Personal_Projects\AndroidStudioProjects\MantraCounter`
- **Target (Primary):** `C:\Users\richa\Documents\Personal_Projects\CrossPlatApps\MantraCounterRemade`

## Team Directives
### 1. The Historian (Analyzer)
- **Task:** Read Java files from the Source repo.
- **Goal:** Identify core data models and business logic "intent."
- **Note:** Do NOT modify the Source repo.

### 2. The Architect (Migration Lead)
- **Task:** Initialize a fresh KMP project in the Target repo using the `npx create-kmp-app` (or similar 2026 CLI).
- **Goal:** Reconstruct the Source logic into the Target repo using modern Kotlin.
- **Reorganization:** You are encouraged to create a better, cleaner folder structure in the Target repo.

### 3. The Cleanup Crew (Refiner)
- **Task:** Verify that no "Java-isms" (like null-unsafe code) leaked into the new repo.
- **Goal:** Ensure the Target repo is 100% Kotlin-native and idiomatic.

---

## Workflow Rules
1. **Evolution over Translation:** If the Java code used a "beginner" pattern, the Architect must replace it with a 2026 best practice (e.g., replace manual threads with Coroutines).
2. **Artifacts:** Generate a `migration_report.md` in the Target repo listing every file that was successfully "evolved."