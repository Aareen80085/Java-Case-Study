# 🚗 Enterprise Vehicle Service & Fleet Maintenance System
> **B.Tech Computer Science & Engineering (Semester III) | Object-Oriented Programming in Java**  
> *A robust, enterprise-grade desktop application for fleet management, maintenance tracking, parts inventory, and financial auditing.*

---

## 📌 Executive Summary

The **Vehicle Service & Fleet Maintenance System** is a standalone Java application designed for transport operators, logistics fleets, and service workshops. It provides end-to-end management of vehicle assets, automated preventive maintenance scheduling, spare parts inventory control, and financial reporting.

The system is engineered specifically to demonstrate core **Object-Oriented Programming (OOP)** principles, advanced **Java Collections Framework** data structures (`HashMap`, `LinkedList`), exact arbitrary-precision arithmetic (`BigDecimal`), and domain-specific **Custom Exception Handling** wrapped in an interactive **Java Swing GUI**.

---

## 🚀 Key Features

* **🚘 Fleet Asset Registration:** Fast registration of transport assets (Cars, Trucks, Vans, Buses) with make, model, manufacturing year, odometer reading, and custom maintenance intervals.
* **🛡️ O(1) Instant Duplicate Detection:** Prevents duplicate vehicle registrations using hashing lookups.
* **🔧 Service Event Logging:** Complete maintenance logging (oil changes, brake overhauls, safety inspections, tune-ups) with labor and replacement parts billing.
* **⏱️ Automated Maintenance Milestone Tracking:** Real-time calculation of upcoming service targets. Flags vehicles as `"Active"` or `"Service Due"` with overdue kilometer tracking.
* **💰 Exact Financial Calculations:** Uses `BigDecimal` with explicit rounding modes (`RoundingMode.HALF_UP`) to guarantee 100% precision for invoices and grand totals.
* **📦 Spare Parts Inventory:** Stock catalog tracking unit costs, categories, and quantities available.
* **📊 Executive Reporting & Vehicle Dossiers:** Generates instantaneous full-fleet financial audits and single-vehicle service histories with search sanitization (auto-trim and uppercase normalization).
* **🖥️ Interactive Multi-Tab Swing GUI:** Clean 4-tab user interface with tabular views, form validation, and real-time dashboard statistic cards.

---

## 🧠 Java Concepts Applied & Technical Justification

This project was built to satisfy academic case study requirements while adhering to real-world software engineering practices:

| Java Concept | File / Location | Academic Justification & Industry Rationale |
| :--- | :--- | :--- |
| **`HashMap<K, V>`** | `VehicleServiceManager.java` (`vehicleMap`) | **$O(1)$ Fast Key-Based Lookup:** Vehicles are indexed by their unique registration number (`String`). Lookups, duplicate checks, and updates execute in constant $O(1)$ time rather than $O(N)$ linear scans in an `ArrayList`. |
| **`LinkedList<E>`** | `Vehicle.java` (`serviceHistory`) & `VehicleServiceManager.java` (`allServiceRecords`) | **Chronological Order Preservation:** A doubly-linked list naturally preserves chronological FIFO history. Appending new maintenance jobs (`addLast()`) is a constant $O(1)$ operation without array resizing overhead. |
| **`BigDecimal`** | `ServiceRecord.java`, `Part.java`, `VehicleServiceManager.java` | **Accurate Currency Arithmetic:** Standard floating-point primitives (`float`, `double`) suffer from IEEE-754 binary rounding inaccuracies (e.g. `0.1 + 0.2 = 0.30000000000000004`). `BigDecimal` ensures exact decimal math. |
| **Custom Exceptions** | `DuplicateVehicleException.java`<br>`InvalidMileageException.java`<br>`VehicleNotFoundException.java` | **Domain-Specific Validation:** Rather than throwing generic runtime exceptions, custom checked exceptions enforce domain business rules and supply clear, contextual error messages. |
| **Composition (OOP)** | `Vehicle.java` $\rightarrow$ `LinkedList<ServiceRecord>` | **"Has-A" Relationship:** Models real-world containment where each vehicle owns its private maintenance ledger. |
| **Service Layer / Facade** | `ReportService.java` | **Separation of Concerns (SoC):** Decouples the presentation layer (`MainFrame`) from the core computation and formatting engine (`VehicleServiceManager`). |
| **Java Swing GUI** | `MainFrame.java` | **Event-Driven Architecture:** Employs action listeners, custom non-editable table models (`DefaultTableModel`), and Swing event dispatching (`SwingUtilities.invokeLater`). |

---

## 📁 Project Architecture & File Map

All project files use a clean, flat structure in the default package for effortless compilation:

```text
Java-Case-Study/
├── src/
│   ├── Main.java                      # Application launcher; initializes manager, data, & GUI
│   ├── MainFrame.java                 # Complete Swing GUI (4 Tabs: Fleet, Service, Parts, Reports)
│   ├── VehicleServiceManager.java     # CENTRAL BUSINESS ENGINE (Calculations, HashMap, LinkedList)
│   ├── Vehicle.java                   # Vehicle entity model with embedded serviceHistory LinkedList
│   ├── ServiceRecord.java             # Service job entity model with BigDecimal costs
│   ├── Part.java                      # Spare part entity model with pricing and inventory count
│   ├── ReportService.java             # Facade helper delegating report queries
│   ├── DataGenerator.java             # Seeds realistic sample fleet and service records
│   ├── DuplicateVehicleException.java # Thrown when a duplicate registration number is added
│   ├── VehicleNotFoundException.java  # Thrown when querying a registration that does not exist
│   ├── InvalidMileageException.java   # Thrown on odometer rollback attempts
│   ├── .gitignore                     # Ignores compiled .class bytecode and temporary files
│   └── README.md                      # Comprehensive project documentation for GitHub
│
├── build.sh                           # Shell script to compile and package standalone executable JAR
├── run.sh                             # Convenience script to launch the application
├── VehicleServiceSystem.jar           # Pre-packaged standalone executable JAR
└── README.md                          # Root project documentation
```

---

## ⚙️ Core Business Rules & Validations

1. **Duplicate Prevention:**
   * Before adding any vehicle, the system checks `vehicleMap.containsKey(key)` in $O(1)$ time. If present, it aborts by throwing `DuplicateVehicleException`.
2. **Odometer Rollback Guard:**
   * When logging a service, `newRecord.mileageAtService` cannot be lower than the vehicle's current odometer reading (`currentMileage`). If a technician enters a lower value, the system throws `InvalidMileageException`.
3. **Automatic Maintenance Milestone Advancement:**
   * Upon completing a service event at mileage $M$ with interval $I$:
     $$\text{currentMileage} = M$$
     $$\text{nextServiceMileage} = M + I$$
     $$\text{status} = \begin{cases} \text{"Service Due"}, & \text{if } \text{currentMileage} \ge \text{nextServiceMileage} \\ \text{"Active"}, & \text{otherwise} \end{cases}$$
4. **Auto-Intake Service Logging:**
   * Registering a new vehicle automatically logs an initial baseline safety inspection record, ensuring every fleet vehicle is indexed in the service ledger.
5. **Input Sanitization:**
   * Registration numbers are automatically stripped of leading/trailing whitespace (`.trim()`) and normalized to uppercase (`.toUpperCase()`), ensuring searches like `" trk-101 "` seamlessly resolve to `"TRK-101"`.

---

## 💻 How to Compile and Run

### Prerequisites
* **Java Development Kit (JDK):** Version 11 or higher (JDK 17 or 21 recommended).
* Terminal / Command Prompt access.

---

### Option 1: Direct Compilation via Terminal (Inside `src/`)

```bash
# 1. Navigate to the source folder
cd src

# 2. Compile Main.java (Java automatically compiles all dependencies)
javac Main.java

# 3. Launch the application
java Main
```

---

### Option 2: Build & Package Standalone JAR (From Project Root)

```bash
# 1. Grant execute permissions (macOS/Linux)
chmod +x build.sh run.sh

# 2. Build the bin directory and package VehicleServiceSystem.jar
./build.sh

# 3. Execute the packaged application
java -jar VehicleServiceSystem.jar
```

---

## 🖥️ Application Interface (4-Tab Layout)

1. **Tab 1 — Fleet Vehicles:**
   * Left panel: Registration form with inputs for Registration ID, Brand, Model, Year, Vehicle Type dropdown, Mileage, and Interval.
   * Right panel: Live `JTable` listing all fleet vehicles, current odometer readings, next service milestones, and real-time maintenance status tags.
2. **Tab 2 — Record Service:**
   * Left panel: Maintenance logging form with Service Type dropdown, Service Date, Mileage, Labor Cost, Parts Cost, and Technician Notes.
   * Right panel: Live audit table of all completed services with unique Service IDs and calculated invoice totals.
3. **Tab 3 — Spare Parts Inventory:**
   * Left panel: Catalog addition form (Part Number, Part Name, Category, Unit Price, Stock Quantity).
   * Right panel: Tabular view of available warehouse stock.
4. **Tab 4 — Fleet Reports & Summary:**
   * Top dashboard: 4 KPI summary cards (*Total Vehicles*, *Completed Services*, *Vehicles Due for Service*, *Total Maintenance Spend*).
   * Action buttons: One-click generation of the complete Fleet Summary report and search input for individual vehicle history dossiers.
   * Console: Scrollable monospaced readout displaying formatted audit reports.

---

## 🎓 Viva & Presentation Study Guide (Top Q&A)

### Q1: Why use `BigDecimal` instead of `double` or `float` for costs?
> **Answer:** Standard primitive floating-point numbers represent numbers using IEEE-754 binary fractions. Certain decimal numbers like `0.1` cannot be represented precisely in binary, introducing rounding errors (e.g. `0.10 + 0.20 = 0.30000000000000004`). In financial and enterprise systems, this causes discrepancies. `BigDecimal` provides arbitrary-precision arithmetic with explicit scale (2 decimal places) and rounding controls (`RoundingMode.HALF_UP`).

### Q2: Why use `HashMap` for vehicles and `LinkedList` for service records?
> **Answer:** 
> * **`HashMap` for Vehicles:** Allows instantaneous $O(1)$ lookups and duplicate checks by vehicle registration number (key). Searching through an `ArrayList` would take linear $O(N)$ time.
> * **`LinkedList` for Service Records:** Maintenance events happen sequentially over time. A `LinkedList` preserves strict chronological insertion order and supports constant-time $O(1)$ appends (`addLast()`) without copying or resizing internal arrays.

### Q3: Why create Custom Exceptions instead of using standard Java exceptions?
> **Answer:** Generic exceptions like `IllegalArgumentException` or `Exception` do not convey domain context. Custom exceptions (`DuplicateVehicleException`, `InvalidMileageException`, `VehicleNotFoundException`) clearly communicate exact business rule violations, simplify unit testing, and allow the GUI to catch specific errors and display tailored dialog messages.

---

## 👤 Author & Academic Details

* **Project:** Vehicle Service & Fleet Maintenance Record System
* **Course:** B.Tech Computer Science & Engineering (Semester III)
* **Subject:** Object-Oriented Programming with Java
* **Repository:** [GitHub: Aareen80085/Java-Case-Study](https://github.com/Aareen80085/Java-Case-Study)
