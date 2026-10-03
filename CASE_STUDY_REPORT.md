# 📘 ACADEMIC CASE STUDY REPORT
## Vehicle Service & Fleet Maintenance Record System
**Course:** B.Tech Computer Science & Engineering (Semester III)  
**Subject:** Object-Oriented Programming with Java  
**Academic Year:** 2025 – 2026  

---

## 📑 TABLE OF CONTENTS
1. [Abstract](#1-abstract)
2. [Problem Statement & Background](#2-problem-statement--background)
3. [Project Objectives](#3-project-objectives)
4. [System Architecture & Design Patterns](#4-system-architecture--design-patterns)
5. [In-Depth Analysis of Core Java Concepts](#5-in-depth-analysis-of-core-java-concepts)
   - 5.1 HashMap: Fast O(1) Asset Lookup
   - 5.2 LinkedList: Chronological Service History Preservation
   - 5.3 BigDecimal: Arbitrary-Precision Financial Math
   - 5.4 Custom Exception Handling: Robust Domain Validation
   - 5.5 Object-Oriented Composition: Has-A Relationships
   - 5.6 Graphical User Interface (Java Swing Architecture)
6. [Mathematical Formulations & Business Rules](#6-mathematical-formulations--business-rules)
7. [Module-by-Module Technical Breakdown](#7-module-by-module-technical-breakdown)
8. [Testing, Edge Cases & Verification](#8-testing-edge-cases--verification)
9. [Conclusion & Future Enhancements](#9-conclusion--future-enhancements)
10. [References](#10-references)

---

## 1. Abstract

In commercial transport, logistics, and automotive fleet operations, unscheduled vehicle downtime directly degrades profitability and road safety. Traditional paper ledgers and rudimentary spreadsheet systems are prone to human entry errors, odometer rollbacks, floating-point rounding inaccuracies in invoices, and delayed preventive servicing.

This case study presents the design and implementation of an **Enterprise Vehicle Service & Fleet Maintenance System** developed in Java. Built upon Object-Oriented Programming (OOP) paradigms and the Java Collections Framework, the system utilizes a **`HashMap`** for $O(1)$ constant-time asset retrieval and duplicate prevention, a **`LinkedList`** for chronological audit trails, **`BigDecimal`** for exact financial calculations, and **Custom Exceptions** for domain constraint enforcement. The entire system is packaged behind an interactive, multi-tab **Java Swing Graphical User Interface (GUI)** providing real-time fleet analytics, service logging, and inventory tracking.

---

## 2. Problem Statement & Background

Commercial transport companies manage fleets ranging from tens to hundreds of vehicles (buses, trucks, cargo vans, and inspection cars). Fleet managers face three critical challenges:

1. **Preventive Maintenance Drift:** Vehicles require servicing at strictly calibrated odometer intervals (e.g., every 10,000 km). Without automated odometer tracking, vehicles miss critical maintenance, leading to costly engine breakdowns.
2. **Financial Inaccuracies:** Traditional computerized billing systems utilizing IEEE-754 primitive floating-point types (`float`, `double`) introduce cumulative roundoff discrepancies when totaling labor hours and spare parts costs.
3. **Audit Trail Fragmentations:** Associating parts consumed, labor costs, and technician notes with the correct vehicle over several years requires an ordered, tamper-evident chronological ledger.

---

## 3. Project Objectives

The primary objectives of this software project are:

* **Instantaneous Vehicle Lookups:** Enable fleet managers to locate any registered vehicle and its complete specification in constant $O(1)$ time.
* **Chronological Service Audit Logging:** Automatically record maintenance events in an ordered sequence that preserves the exact historical timeline of repairs.
* **Automated Service Milestone Calculation:** Automatically recalculate the next due service target when an odometer reading advances and flag vehicles requiring immediate attention.
* **Exact Financial Precision:** Implement monetary computation with zero floating-point loss across labor, spare parts, and grand total fleet expenditures.
* **Defensive Domain Exception Handling:** Prevent invalid actions (such as registering duplicate license plates, logging odometer readings lower than current mileage, or querying nonexistent vehicles).
* **Interactive GUI Dashboard:** Provide a clean, user-friendly desktop interface allowing non-technical workshop technicians and fleet supervisors to operate the system smoothly.

---

## 4. System Architecture & Design Patterns

The software is structured according to a **layered separation of concerns (SoC)**, separating UI presentation, domain coordination, data storage, and entity models:

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│                     (MainFrame.java)                        │
│          Java Swing GUI: 4 Tabs, JTable Models,             │
│            Input Forms & Live Statistic Cards               │
└─────────────────────────────┬───────────────────────────────┘
                              │ Event-Driven Actions
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      SERVICE / FACADE                       │
│                   (ReportService.java)                      │
│            Input Normalization & Query Routing              │
└─────────────────────────────┬───────────────────────────────┘
                              │ Delegated Queries & Mutations
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                 CENTRAL BUSINESS LOGIC ENGINE               │
│                (VehicleServiceManager.java)                 │
│   • Registration Validation    • Milestone Recalculation   │
│   • BigDecimal Financial Math  • Fleet Report Generation    │
└──────────────┬───────────────────────────────┬──────────────┘
               │                               │
       Stores & Retrieves              Stores & Orders
               ▼                               ▼
┌───────────────────────────────┐ ┌───────────────────────────┐
│     HashMap<String, Vehicle>  │ │LinkedList<ServiceRecord>  │
│  Fast O(1) Key-Based Storage  │ │ Chronological Fleet Log   │
└──────────────┬────────────────┘ └───────────────────────────┘
               │
               │ (1-to-Many Composition)
               ▼
┌───────────────────────────────┐
│         Vehicle.java          │
│   LinkedList<ServiceRecord>   │
│  (Vehicle's Private Ledger)   │
└───────────────────────────────┘
```

### Applied Design Patterns:
1. **Facade Pattern (`ReportService.java`):** Provides a simplified interface for report generation, shielding the UI from complex underlying iteration and formatting routines.
2. **Composition Pattern (`Vehicle.java`):** Implements a "Has-A" relationship where a vehicle instance directly owns its chronological `LinkedList<ServiceRecord>`.
3. **Data Transfer Object / Entity Pattern:** `Vehicle`, `ServiceRecord`, and `Part` encapsulate state without mixing presentation logic.

---

## 5. In-Depth Analysis of Core Java Concepts

### 5.1 `HashMap`: Fast $O(1)$ Asset Lookup
* **Implementation:** `public HashMap<String, Vehicle> vehicleMap = new HashMap<>();`
* **Technical Justification:** Fleet management operations are predominantly read-heavy and key-indexed (searching by vehicle registration number). An `ArrayList` requires linear scanning ($O(N)$ comparisons) to verify existence or detect duplicates. The `HashMap` computes the hash code of the registration string, resolving buckets in average $O(1)$ time regardless of whether the fleet contains 10 or 100,000 vehicles.

### 5.2 `LinkedList`: Chronological Service History Preservation
* **Implementation:** `public LinkedList<ServiceRecord> allServiceRecords = new LinkedList<>();` & `vehicle.serviceHistory`
* **Technical Justification:** Maintenance histories are strictly sequential. New service records are continuously appended to the tail of the log (`addLast()`). In a `LinkedList`, appending is an $O(1)$ pointer update that never triggers contiguous memory reallocations or array copy operations, unlike dynamic arrays (`ArrayList`).

### 5.3 `BigDecimal`: Arbitrary-Precision Financial Math
* **Implementation:** `BigDecimal laborCost`, `BigDecimal partsCost`, `BigDecimal totalCost`
* **Technical Justification:** In standard Java primitives:
  $$\text{double } a = 0.10; \quad \text{double } b = 0.20; \quad a + b \rightarrow 0.30000000000000004$$
  Such IEEE-754 binary floating-point inaccuracies are unacceptable in commercial financial software. `BigDecimal` performs exact arbitrary-precision base-10 arithmetic. All calculations explicitly set the scale to 2 decimal places with `RoundingMode.HALF_UP`:
  ```java
  public BigDecimal calculateRecordTotal(ServiceRecord record) {
      if (record == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
      BigDecimal labor = (record.laborCost != null) ? record.laborCost : BigDecimal.ZERO;
      BigDecimal parts = (record.partsCost != null) ? record.partsCost : BigDecimal.ZERO;
      return labor.add(parts).setScale(2, RoundingMode.HALF_UP);
  }
  ```

### 5.4 Custom Exception Handling: Robust Domain Validation
Standard unchecked exceptions (`NullPointerException`, `IllegalArgumentException`) are generic. This system implements domain-specific checked exceptions inheriting from `Exception`:
* **`DuplicateVehicleException`:** Thrown when attempting to register a license plate already indexed in the `HashMap`.
* **`InvalidMileageException`:** Thrown if a logged service mileage is lower than the vehicle's current odometer reading (odometer rollback prevention).
* **`VehicleNotFoundException`:** Thrown when attempting to log service on an unregistered vehicle.

### 5.5 Object-Oriented Composition
Rather than maintaining disconnected tables, the system uses OOP Composition:
* A `Vehicle` **has-a** `LinkedList<ServiceRecord>` representing its private lifetime history.
* This ensures that deleting or inspecting a vehicle immediately provides its linked historical records without requiring external relational join operations.

### 5.6 Graphical User Interface (Java Swing)
* **Thread Safety:** The application launches safely on the **Event Dispatch Thread (EDT)** using `SwingUtilities.invokeLater()`.
* **Look and Feel:** Automatically applies the modern `Nimbus` theme with fallback to system defaults.
* **Component Architecture:** Uses `JTabbedPane` for modular navigation, non-editable custom `DefaultTableModel` instances for `JTable` rendering, and reactive `ActionListener` triggers.

---

## 6. Mathematical Formulations & Business Rules

### 1. Total Service Cost Formula
For any completed maintenance record $R$:
$$\text{Cost}_{\text{total}}(R) = \text{Cost}_{\text{labor}}(R) + \text{Cost}_{\text{parts}}(R)$$
Both operands are normalized to zero if null and evaluated using `BigDecimal.add()`.

### 2. Fleet-Wide Financial Expenditure
$$\text{Fleet Total} = \sum_{i=1}^{N} \text{Cost}_{\text{labor}}(R_i) + \sum_{i=1}^{N} \text{Cost}_{\text{parts}}(R_i)$$
Where $N$ is the total count of service records across all vehicles.

### 3. Maintenance Milestone Progression
When a vehicle $V$ completes a service at odometer reading $M_{\text{service}}$ with a manufacturer service interval $I$:
$$M_{\text{current}} = M_{\text{service}}$$
$$M_{\text{next}} = M_{\text{current}} + I$$

### 4. Service Due Condition
The maintenance status $S(V)$ is dynamically evaluated as:
$$S(V) = \begin{cases} \text{"Service Due"}, & \text{if } M_{\text{current}} \ge M_{\text{next}} \\ \text{"Active"}, & \text{if } M_{\text{current}} < M_{\text{next}} \end{cases}$$
If overdue, the overdue distance is quantified as:
$$\Delta_{\text{overdue}} = M_{\text{current}} - M_{\text{next}} \quad (\text{km})$$

---

## 7. Module-by-Module Technical Breakdown

| Class Name | Source File | Category | Responsibility |
| :--- | :--- | :--- | :--- |
| **`Main`** | `Main.java` | Launcher | Configures UI look-and-feel, instantiates `VehicleServiceManager`, invokes `DataGenerator`, and dispatches `MainFrame` on EDT. |
| **`MainFrame`** | `MainFrame.java` | Presentation | Builds and displays the 4-tab Swing GUI; handles user actions, table rendering, and dashboard stats updates. |
| **`VehicleServiceManager`** | `VehicleServiceManager.java` | Core Engine | Central controller containing all 6 functional sections: Vehicle registration, service event recording, milestone tracking, financial computations, reporting, and parts inventory. |
| **`Vehicle`** | `Vehicle.java` | Model Entity | Encapsulates vehicle specifications, odometer milestones, status logic, and an embedded `LinkedList<ServiceRecord>`. |
| **`ServiceRecord`** | `ServiceRecord.java` | Model Entity | Holds maintenance event data, service type, odometer reading, technician notes, and `BigDecimal` costs. |
| **`Part`** | `Part.java` | Model Entity | Models spare parts catalog items with SKU part numbers, category names, unit prices, and stock quantities. |
| **`ReportService`** | `ReportService.java` | Service Facade | Cleanses inputs (`.trim().toUpperCase()`) and delegates report generation from the UI to the backend manager. |
| **`DataGenerator`** | `DataGenerator.java` | Utility | Seeds initial sample parts, fleet vehicles, and realistic completed service logs for immediate evaluation. |
| **`DuplicateVehicleException`** | `DuplicateVehicleException.java` | Domain Exception | Thrown when a duplicate vehicle registration key is detected in the `HashMap`. |
| **`InvalidMileageException`** | `InvalidMileageException.java` | Domain Exception | Thrown when a maintenance odometer reading rolls backward. |
| **`VehicleNotFoundException`** | `VehicleNotFoundException.java` | Domain Exception | Thrown when a referenced vehicle registration cannot be located in `vehicleMap`. |

---

## 8. Testing, Edge Cases & Verification

The system was subjected to rigorous test cases covering normal workflows and deliberate edge-case violations:

| Test Case ID | Test Scenario | Input Data | Expected Behavior | Actual System Output | Verdict |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-01** | Standard Vehicle Registration | `TRK-900`, `Volvo`, `FMX`, `2023`, `Truck`, `10000 km`, `15000 km` | Vehicle added to `vehicleMap`; auto-logs baseline intake service record. | Added successfully; tables updated. | **PASS** |
| **TC-02** | Duplicate Registration Detection | Attempt to re-register existing `TRK-101` | System intercepts duplicate key in `HashMap` and throws `DuplicateVehicleException`. | Error dialog displayed: `"Vehicle TRK-101 already registered!"` | **PASS** |
| **TC-03** | Odometer Rollback Prevention | Vehicle odometer at `82000 km`; service logged at `80000 km` | System rejects entry; throws `InvalidMileageException`. | Warning dialog displayed: `"Service mileage cannot be lower than current vehicle odometer!"` | **PASS** |
| **TC-04** | Service on Nonexistent Vehicle | Registration `XYZ-999` (unregistered) | System throws `VehicleNotFoundException`. | Error dialog displayed: `"Vehicle not found with Registration Number: XYZ-999"`. | **PASS** |
| **TC-05** | Case & Whitespace Normalization | Search for `"  trk-101  "` in Fleet Reports | Input trimmed and uppercased to `"TRK-101"`; history dossier returned. | Correct history dossier displayed with full maintenance records. | **PASS** |
| **TC-06** | Financial Precision Summation | Labor `$150.00` + Parts `$80.00` | Exact `$230.00` computed via `BigDecimal`. | Exactly `$230.00` rendered with no rounding leakage. | **PASS** |

---

## 9. Conclusion & Future Enhancements

### Conclusion
The **Vehicle Service & Fleet Maintenance System** successfully satisfies all case study requirements for **B.Tech CSE Semester III**. By centralizing business logic in `VehicleServiceManager.java`, pairing $O(1)$ `HashMap` lookups with chronological `LinkedList` logs, guaranteeing financial accuracy with `BigDecimal`, and shielding the core with Custom Exceptions, the application demonstrates robust, maintainable, and industry-standard Java software design.

### Future Scope & Enhancements
1. **Relational Database Connectivity (JDBC):** Replace in-memory collections with persistent storage using PostgreSQL or MySQL.
2. **Automated PDF Invoice Export:** Integrate Apache PDFBox or iText to export printable maintenance invoices and work orders.
3. **Telematics & IoT Integration:** Connect real-time GPS and OBD-II vehicle telematics to automatically update odometer readings without manual driver entry.
4. **Role-Based Access Control (RBAC):** Provide differentiated login portals for Workshop Mechanics (service logging only) vs Fleet Managers (financial and fleet reports).

---

## 10. References

1. Oracle Corporation. *Java™ Platform, Standard Edition 17 API Specification*. `java.util.HashMap`, `java.util.LinkedList`, `java.math.BigDecimal`.
2. Schildt, Herbert. *Java: The Complete Reference*, 12th Edition. McGraw-Hill Education.
3. Bloch, Joshua. *Effective Java*, 3rd Edition. Addison-Wesley Professional.
4. Eckel, Bruce. *Thinking in Java*, 4th Edition. Prentice Hall.
5. Gamma, Helm, Johnson, Vlissides. *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley.
