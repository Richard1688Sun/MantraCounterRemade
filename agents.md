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
- **Task:** Reconstruct features in the Target repo using modern Kotlin (KMP).
- **Goal:** Implement features using 2026 best practices (Coroutines, Ktor, Multiplatform Settings).
- **Reorganization:** Authorized to flatten packages and refactor "God Objects" into clean ViewModels.

### 3. The Cleanup Crew (Refiner)
- **Task:** Verify Kotlin idiomatic standards and null-safety.
- **Goal:** Eliminate "Java-isms" and ensure the new repo is 100% Kotlin-native.

---

## Workflow Rules (Feature Implementation Phase)
1. **Evolution over Translation:** If a Java feature used a "beginner" pattern (e.g., manual thread sleep, nested callbacks), replace it with the most "correct" 2026 equivalent.
2. **Ambiguity Protocol:** If a feature's functionality is unclear or the prompt is vague:
   - **Step A:** Deep-scan the Source folder for relevant logic.
   - **Step B:** If still unclear, **STOP** and ask for clarification via a "Request for Info" (RFI) artifact. Do NOT hallucinate logic.
3. **Artifacts:** - Maintain a `migration_report.md` in the Target repo.
   - For every complex feature, generate an `Implementation_Plan` artifact and wait for approval.
4. **Budget Guardrail:** Use **Gemini 3 Flash** for repetitive boilerplate. Escalate to **Gemini 3 Deep Think** for complex architectural mapping between Java and KMP.

---

## Special Instructions
- **Structure:** Antigravity is authorized to fully re-organize the codebase structure to work with modern Kotlin standards.
- **Legacy Awareness:** Acknowledge that the Source was a learning project; prioritize modern reliability over mimicking the original code structure.