/**
 * Service to generate clean, human-readable text reports for the fleet.
 * All formatting and calculation logic is cleanly centralized in VehicleServiceManager.java.
 */
public class ReportService {

    /**
     * Generates an executive summary of the entire fleet.
     */
    public String generateFleetSummary(VehicleServiceManager manager) {
        if (manager == null) return "No manager provided.";
        return manager.generateFleetSummaryReport();
    }

    /**
     * Generates a detailed service history log for a specific vehicle.
     */
    public String generateVehicleHistoryReport(VehicleServiceManager manager, String targetRegistrationNumber) {
        if (manager == null) return "No manager provided.";
        if (targetRegistrationNumber == null) return "Vehicle registration number cannot be null.";
        return manager.generateVehicleHistoryReport(targetRegistrationNumber.trim().toUpperCase());
    }
}
