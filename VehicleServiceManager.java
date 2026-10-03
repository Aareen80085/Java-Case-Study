import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * =========================================================================
 * VEHICLE SERVICE RECORD SYSTEM - MAIN LOGIC ENGINE
 * =========================================================================
 * 
 * ALL system logic and calculations are centralized here in 6 clear sections:
 * Section 1: Vehicle Registration (HashMap O(1) Fast Lookup)
 * Section 2: Record Service Events (LinkedList Ordered History)
 * Section 3: Track Next Service Requirements (Milestone Tracking)
 * Section 4: Financial Calculations (BigDecimal Exact Currency Math)
 * Section 5: Report Generation (Fleet Summary & Vehicle History)
 * Section 6: Spare Parts Inventory
 */
public class VehicleServiceManager {

    // 1. HashMap: Fast O(1) key-based lookup using Registration Number as key
    public HashMap<String, Vehicle> vehicleMap = new HashMap<>();

    // 2. LinkedList: Ordered chronological audit history of all completed services
    public LinkedList<ServiceRecord> allServiceRecords = new LinkedList<>();

    // 3. LinkedList: Spare parts inventory list
    public LinkedList<Part> partList = new LinkedList<>();

    // Counter for auto-generating unique service record IDs
    public int nextRecordId = 1001;

    /*
     * =========================================================================
     * SECTION 1: VEHICLE REGISTRATION (Uses HashMap for O(1) Fast Lookup)
     * =========================================================================
     */

    /**
     * Registers a new vehicle into the fleet.
     * Prevents duplicate registration numbers using HashMap in O(1) time.
     */
    public void addVehicle(Vehicle newVehicle) throws DuplicateVehicleException {
        if (newVehicle == null || newVehicle.registrationNumber == null) {
            throw new IllegalArgumentException("Vehicle registration cannot be null.");
        }

        String key = newVehicle.registrationNumber.trim().toUpperCase();

        // HashMap O(1) check: Is this vehicle already registered?
        if (vehicleMap.containsKey(key)) {
            throw new DuplicateVehicleException(key);
        }

        newVehicle.registrationNumber = key;
        updateVehicleServiceStatus(newVehicle); // Calculate initial status
        vehicleMap.put(key, newVehicle);

        // Automatically record an initial service inspection event when a vehicle joins
        // the fleet
        ServiceRecord initialRecord = new ServiceRecord();
        initialRecord.recordId = nextRecordId++;
        initialRecord.vehicleRegistrationNumber = key;
        initialRecord.serviceType = "Initial Fleet Intake & Safety Inspection";
        initialRecord.serviceDate = java.time.LocalDate.now().toString();
        initialRecord.mileageAtService = newVehicle.currentMileage;
        initialRecord.laborCost = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        initialRecord.partsCost = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        initialRecord.totalCost = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        initialRecord.notes = "Initial baseline safety check and intake registration.";

        newVehicle.serviceHistory.addLast(initialRecord);
        allServiceRecords.addLast(initialRecord);
    }

    /**
     * Searches for a vehicle by registration number in O(1) time using HashMap.
     */
    public Vehicle findVehicle(String registrationNumber) {
        if (registrationNumber == null) {
            return null;
        }
        return vehicleMap.get(registrationNumber.trim().toUpperCase());
    }

    public Collection<Vehicle> getAllVehicles() {
        return vehicleMap.values();
    }

    public int getTotalVehicles() {
        return vehicleMap.size();
    }

    /*
     * =========================================================================
     * SECTION 2: RECORD SERVICE EVENTS (Uses LinkedList for Ordered History)
     * =========================================================================
     */

    /**
     * Records a new service/repair event for a vehicle.
     * 1. Validates vehicle existence (VehicleNotFoundException).
     * 2. Validates that mileage didn't roll backwards (InvalidMileageException).
     * 3. Calculates the record total cost using BigDecimal.
     * 4. Appends to the vehicle's LinkedList and the fleet's LinkedList.
     * 5. Advances vehicle mileage and calculates next service requirement.
     */
    public void addServiceRecord(ServiceRecord newRecord)
            throws VehicleNotFoundException, InvalidMileageException {

        if (newRecord == null || newRecord.vehicleRegistrationNumber == null) {
            throw new IllegalArgumentException("Service record or vehicle registration cannot be null.");
        }

        String key = newRecord.vehicleRegistrationNumber.trim().toUpperCase();
        Vehicle vehicle = vehicleMap.get(key);

        if (vehicle == null) {
            throw new VehicleNotFoundException(key);
        }

        // Business Rule: Service mileage cannot be lower than current vehicle odometer
        if (newRecord.mileageAtService < vehicle.currentMileage) {
            throw new InvalidMileageException(
                    "Service mileage (" + newRecord.mileageAtService
                            + " km) cannot be lower than current vehicle odometer ("
                            + vehicle.currentMileage + " km)!");
        }

        // Set unique ID and calculate total using BigDecimal
        newRecord.recordId = nextRecordId++;
        newRecord.vehicleRegistrationNumber = key;
        newRecord.totalCost = calculateRecordTotal(newRecord);

        // LinkedList Operation: Append to vehicle's private ordered history
        vehicle.serviceHistory.addLast(newRecord);

        // LinkedList Operation: Append to fleet-wide chronological history
        allServiceRecords.addLast(newRecord);

        // Update vehicle's mileage and recalculate next service milestone
        vehicle.currentMileage = newRecord.mileageAtService;
        vehicle.nextServiceMileage = vehicle.currentMileage + vehicle.serviceInterval;
        updateVehicleServiceStatus(vehicle);
    }

    public List<ServiceRecord> getAllServiceRecords() {
        return allServiceRecords;
    }

    public int getTotalServiceRecords() {
        return allServiceRecords.size();
    }

    /*
     * =========================================================================
     * SECTION 3: TRACK NEXT SERVICE REQUIREMENTS
     * =========================================================================
     */

    /**
     * Checks if a vehicle has reached or passed its next service milestone.
     */
    public boolean isVehicleServiceDue(Vehicle vehicle) {
        if (vehicle == null)
            return false;
        return vehicle.currentMileage >= vehicle.nextServiceMileage;
    }

    /**
     * Updates the status text of a vehicle based on its mileage.
     */
    public void updateVehicleServiceStatus(Vehicle vehicle) {
        if (vehicle == null)
            return;
        if (isVehicleServiceDue(vehicle)) {
            vehicle.status = "Service Due";
        } else {
            vehicle.status = "Active";
        }
    }

    /**
     * Counts how many vehicles currently require service across the fleet.
     */
    public int countVehiclesDueForService() {
        int dueCount = 0;
        for (Vehicle v : vehicleMap.values()) {
            if (isVehicleServiceDue(v)) {
                dueCount++;
            }
        }
        return dueCount;
    }

    /*
     * =========================================================================
     * SECTION 4: FINANCIAL CALCULATIONS (BigDecimal Exact Currency Precision)
     * =========================================================================
     */

    /**
     * CALCULATION 1: Computes Total Cost for a single service record.
     * Formula: Total = Labor Cost + Parts Cost
     */
    public BigDecimal calculateRecordTotal(ServiceRecord record) {
        if (record == null)
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal labor = (record.laborCost != null) ? record.laborCost : BigDecimal.ZERO;
        BigDecimal parts = (record.partsCost != null) ? record.partsCost : BigDecimal.ZERO;
        return labor.add(parts).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * CALCULATION 2: Computes Lifetime Maintenance Spend for a specific vehicle.
     * Sums all service records in that vehicle's LinkedList history.
     */
    public BigDecimal calculateVehicleLifetimeCost(Vehicle vehicle) {
        if (vehicle == null)
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = BigDecimal.ZERO;
        for (ServiceRecord record : vehicle.serviceHistory) {
            if (record.totalCost != null) {
                total = total.add(record.totalCost);
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * CALCULATION 3: Computes Total Labor Spend across the ENTIRE fleet.
     */
    public BigDecimal calculateFleetLaborCost() {
        BigDecimal totalLabor = BigDecimal.ZERO;
        for (ServiceRecord record : allServiceRecords) {
            if (record.laborCost != null && record.laborCost.compareTo(BigDecimal.ZERO) >= 0) {
                totalLabor = totalLabor.add(record.laborCost);
            }
        }
        return totalLabor.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * CALCULATION 4: Computes Total Parts Spend across the ENTIRE fleet.
     */
    public BigDecimal calculateFleetPartsCost() {
        BigDecimal totalParts = BigDecimal.ZERO;
        for (ServiceRecord record : allServiceRecords) {
            if (record.partsCost != null) {
                totalParts = totalParts.add(record.partsCost);
            }
        }
        return totalParts.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * CALCULATION 5: Computes Grand Total Spend across the ENTIRE fleet.
     * Formula: Grand Total = Total Fleet Labor + Total Fleet Parts
     */
    public BigDecimal calculateFleetGrandTotal() {
        return calculateFleetLaborCost().add(calculateFleetPartsCost()).setScale(2, RoundingMode.HALF_UP);
    }

    // Convenient aliases so methods can be called with either naming convention:
    public BigDecimal calculateGrandTotalSpend() {
        return calculateFleetGrandTotal();
    }

    public BigDecimal calculateTotalLaborCost() {
        return calculateFleetLaborCost();
    }

    public BigDecimal calculateTotalPartsCost() {
        return calculateFleetPartsCost();
    }

    public BigDecimal calculateTotalCost(ServiceRecord r) {
        return calculateRecordTotal(r);
    }

    public BigDecimal calculateTotalMaintenanceCost(Vehicle v) {
        return calculateVehicleLifetimeCost(v);
    }

    /*
     * =========================================================================
     * SECTION 5: FLEET & VEHICLE REPORTS
     * =========================================================================
     */

    /**
     * Generates an executive summary report for the entire fleet.
     */
    public String generateFleetSummaryReport() {
        StringBuilder report = new StringBuilder();

        report.append("======================================================================\n");
        report.append("                   FLEET VEHICLE SERVICE SUMMARY REPORT               \n");
        report.append("======================================================================\n\n");

        report.append("Total Registered Vehicles  : ").append(getTotalVehicles()).append("\n");
        report.append("Total Completed Services   : ").append(getTotalServiceRecords()).append("\n");
        report.append("Vehicles Requiring Service : ").append(countVehiclesDueForService()).append("\n\n");

        report.append("----------------------------------------------------------------------\n");
        report.append("FINANCIAL BREAKDOWN (Accurate BigDecimal Calculation)\n");
        report.append("----------------------------------------------------------------------\n");
        report.append(String.format("Total Labor Spend:  $%s\n", calculateFleetLaborCost().toPlainString()));
        report.append(String.format("Total Parts Spend:  $%s\n", calculateFleetPartsCost().toPlainString()));
        report.append(String.format("Grand Total Spend:  $%s\n\n", calculateFleetGrandTotal().toPlainString()));

        report.append("----------------------------------------------------------------------\n");
        report.append("VEHICLES REQUIRING ATTENTION (SERVICE DUE)\n");
        report.append("----------------------------------------------------------------------\n");

        int dueCount = 0;
        for (Vehicle v : getAllVehicles()) {
            if (isVehicleServiceDue(v)) {
                dueCount++;
                int kmOverdue = v.currentMileage - v.nextServiceMileage;
                report.append(String.format("[%d] %s (%s %s) - Current: %d km | Next Service: %d km [OVERDUE: %d km]\n",
                        dueCount, v.registrationNumber, v.brand, v.model, v.currentMileage, v.nextServiceMileage,
                        kmOverdue));
            }
        }

        if (dueCount == 0) {
            report.append("All fleet vehicles are up-to-date with maintenance!\n");
        }

        report.append("\n======================================================================\n");
        return report.toString();
    }

    /**
     * Generates a detailed chronological service history dossier for one vehicle.
     */
    public String generateVehicleHistoryReport(String targetRegistration) {
        if (targetRegistration == null) {
            return "Vehicle registration number cannot be null.";
        }
        String cleanRegistration = targetRegistration.trim().toUpperCase();
        Vehicle vehicle = findVehicle(cleanRegistration);
        if (vehicle == null) {
            return "Vehicle not found with Registration Number: " + cleanRegistration;
        }

        StringBuilder report = new StringBuilder();
        report.append("======================================================================\n");
        report.append("SERVICE HISTORY FOR VEHICLE: ").append(vehicle.registrationNumber).append("\n");
        report.append("Make & Model    : ").append(vehicle.brand).append(" ").append(vehicle.model).append("\n");
        report.append("Year & Category : ").append(vehicle.year).append(" | ").append(vehicle.vehicleType).append("\n");
        report.append("Current Odometer: ").append(vehicle.currentMileage).append(" km\n");
        report.append("Next Service At : ").append(vehicle.nextServiceMileage).append(" km (Status: ")
                .append(vehicle.status).append(")\n");
        report.append("Lifetime Spend  : $").append(calculateVehicleLifetimeCost(vehicle).toPlainString()).append("\n");
        report.append("======================================================================\n\n");

        if (vehicle.serviceHistory.isEmpty()) {
            report.append("No service events recorded yet for this vehicle.\n");
        } else {
            int count = 1;
            for (ServiceRecord sr : vehicle.serviceHistory) {
                report.append(String.format("Record #%d | Date: %s | Service: %s\n", count++, sr.serviceDate,
                        sr.serviceType));
                report.append(String.format("  Odometer: %d km | Labor: $%s | Parts: $%s | Total: $%s\n",
                        sr.mileageAtService,
                        sr.laborCost != null ? sr.laborCost.toPlainString() : "0.00",
                        sr.partsCost != null ? sr.partsCost.toPlainString() : "0.00",
                        sr.totalCost != null ? sr.totalCost.toPlainString() : "0.00"));
                if (sr.notes != null && !sr.notes.isEmpty()) {
                    report.append("  Notes: ").append(sr.notes).append("\n");
                }
                report.append("----------------------------------------------------------------------\n");
            }
        }

        return report.toString();
    }

    /*
     * =========================================================================
     * SECTION 6: SPARE PARTS INVENTORY
     * =========================================================================
     */

    public void addPart(Part newPart) {
        if (newPart != null) {
            partList.add(newPart);
        }
    }

    public List<Part> getAllParts() {
        return partList;
    }

    public int getTotalParts() {
        return partList.size();
    }
}
