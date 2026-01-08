# Initial Quality & Security Assessment  
## SonarCloud Static Analysis – Baseline Report

## 1. Context and Scope

This section presents the **initial baseline assessment** of the project’s code quality and security posture, obtained through a static analysis performed using **SonarCloud**.

The objective of this phase is not remediation, but **understanding the starting point** with precision:  
- What types of issues are present  
- How severe they are  
- Which quality attributes are most impacted  
- Which areas require immediate attention  

The analysis is based on the results extracted from two SonarCloud issue reports (`issuesSonar.json`, `issuesSonar2.json`) and represents the **state of the codebase before any corrective action**.

---

## 2. Global Overview of Detected Issues

Across the analyzed codebase, SonarCloud reported a **total of 882 issues**, distributed across different severity levels and issue types.

### 2.1 Severity Distribution

| Severity   | Number of Issues |
|------------|------------------|
| BLOCKER    | 17               |
| CRITICAL   | 278              |
| MAJOR      | 294              |
| MINOR      | 293              |
| **Total**  | **882**          |

From a risk-oriented perspective, **295 issues (BLOCKER + CRITICAL)** represent an **immediate threat** to the system’s security, reliability, or correctness and therefore require the highest remediation priority.

---

### 2.2 Issue Type Distribution

| Issue Type      | Number of Issues |
|-----------------|------------------|
| BUG             | 312              |
| VULNERABILITY   | 42               |
| CODE_SMELL      | 528              |

Although **Code Smells** represent the majority numerically, the analysis deliberately assigns **higher weight** to:
- **VULNERABILITIES** (security exposure)
- **BUGS** (reliability and correctness)

pie title 📊 1. Severity Distribution
    "BLOCKER" : 17
    "CRITICAL" : 278
    "MAJOR" : 294
    "MINOR" : 293

graph BT
    subgraph Severity
    B[BLOCKER]
    C[CRITICAL]
    MA[MAJOR]
    MI[MINOR]
    end

subgraph Issue_Type
    BUG[BUG]
    CS[CODE_SMELL]
    VUL[VULNERABILITY]
    end

  BUG --- B
  BUG --- C
  BUG --- MI
  CS --- MA
  CS --- MI
  VUL --- C

These categories have a **direct impact on dependability**, unlike maintainability issues, which are addressed in later phases.

---

## 3. Security and Reliability Focus

### 3.1 Vulnerabilities (Security)

The analysis identified **42 Vulnerabilities**, which are explicitly classified by SonarCloud as security-related issues.

These vulnerabilities may lead to:
- Data exposure
- Injection attacks
- Authentication or authorization bypass
- Weak cryptographic practices

Given their nature, **all Vulnerabilities are treated as high-priority**, regardless of whether they are marked as MAJOR or CRITICAL.

---

### 3.2 Bugs (Reliability)

A total of **312 Bugs** were detected, many of which fall into **CRITICAL** and **MAJOR** severities.

These issues affect:
- Functional correctness
- Runtime stability
- Error handling
- Edge-case behavior

From a dependability standpoint, Bugs directly compromise **system reliability** and must be addressed before any large-scale refactoring or optimization activity.

---

## 4. Criticality Breakdown by Quality Dimension

To better guide remediation, issues were analyzed by **severity and quality impact**:

- **BLOCKER (17 issues)**  
  These represent conditions that may cause system failure, security breaches, or invalid program states. The codebase cannot be considered safe or production-ready while these issues are present.

- **CRITICAL (278 issues)**  
  High-risk problems affecting security, reliability, or data integrity. These issues form the **core technical risk** of the system.

- **MAJOR (294 issues)**  
  Issues with significant impact that may not cause immediate failure but increase the likelihood of bugs, vulnerabilities, or future regressions.

- **MINOR (293 issues)**  
  Mostly stylistic or low-risk maintainability concerns. These are intentionally deprioritized in the initial remediation phase.

---

## 5. Initial Remediation Strategy and Priority Plan

Based on the analysis, the remediation plan is structured according to **risk-first principles**:

### Phase 1 – Security Hardening (Highest Priority)
- Fix **all Vulnerabilities**
- Resolve **BLOCKER** and **CRITICAL** issues related to security
- Ensure compliance with secure coding practices

### Phase 2 – Reliability Stabilization
- Address **CRITICAL and MAJOR Bugs**
- Improve error handling and defensive programming
- Reduce runtime failure risks

### Phase 3 – Structural Quality Improvements
- Tackle selected **MAJOR Code Smells** that affect readability or extensibility
- Prepare the codebase for long-term maintainability

### Phase 4 – Maintainability Cleanup (Lowest Priority)
- Resolve remaining **MINOR Code Smells**
- Apply cosmetic and stylistic improvements

%%{init: {'themeVariables': { 'barColor': '#ff4d4d'}}}%%
xychart-beta
    title "📊 3. Risk-Oriented Classification"
    x-axis ["Security (VULN)", "Reliability (BUG)", "Maintainability (SMELL)"]
    y-axis "Issues Count"
    bar [42, 312, 528]

This phased approach ensures that **security and dependability are treated as first-class concerns**, while maintainability is improved once the system is stable and safe.
graph TD
    A[BLOCKER + CRITICAL] -->|Priority 1| B(Security Vulnerabilities)
    B -->|Priority 2| C(Critical Bugs)
    C -->|Priority 3| D(Major Bugs)
    D -->|Priority 4| E(Code Smells)
  
  style A fill:#f96,stroke:#333,stroke-width:4px
  style B fill:#f96,stroke:#333,stroke-width:2px
---

## 6. Traceability: Issue Identifiers by Severity

To guarantee full traceability and auditability, each detected issue is uniquely identified by its SonarCloud issue key.

## 5. Traceability: Issue-Based Analysis (SonarCloud Rules)

The following table reports the **most frequently detected violations**, classified by **SonarCloud rule ID**, including issue type, severity level, occurrence count, and representative locations within the codebase.

| Issue ID (Rule) | Type          | Severity  | Count | Main Locations (Examples) |
|-----------------|---------------|-----------|-------|---------------------------|
| Web:S7930       | BUG           | CRITICAL  | 226   | `Account.jsp`, `chat.jsp`, `Conversazioni.jsp` |
| java:S125       | CODE_SMELL    | MAJOR     | 131   | `Resto.java`, `Studente.java`, `Topic.java` |
| java:S1128      | CODE_SMELL    | MINOR     | 99    | `Resto.java`, `Studente.java`, `IndexServlet.java` |
| Web:ItemTagNotWithinContainer | BUG | MINOR | 61 | `Account.jsp`, `Conversazioni.jsp`, `aula.jsp` |
| java:S1989      | VULNERABILITY | MINOR     | 42    | `EdificioServlet.java`, `IndexServlet.java`, `ChatServlet.java` |
| Web:S6844       | CODE_SMELL    | MAJOR     | 40    | `Account.jsp`, `chat.jsp`, `Conversazioni.jsp` |
| javascript:S3504| CODE_SMELL    | CRITICAL  | 23    | `formOrario.js`, `formChat.js`, `trovaNonAttivati.js` |
| java:S106       | CODE_SMELL    | MAJOR     | 22    | `IndexServlet.java`, `inviaMessaggioChatServlet.java` |
| java:S2190      | BUG           | BLOCKER   | 17    | `cercaOrario.java` (infinite recursion) |
| Web:S5254       | BUG           | MAJOR     | 15    | `Conversazioni.jsp`, `ErroreAccesso.jsp`, `Login.jsp` |

