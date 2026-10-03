import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service Record Entity.
 * Represents a single maintenance or repair job.
 * Uses BigDecimal for financial fields to guarantee exact currency precision.
 * All core business calculations are centralized in VehicleServiceManager.java.
 */
public class ServiceRecord {

    public int recordId;
    public String vehicleRegistrationNumber;
    public String serviceType; // e.g. "Oil & Filter Change", "Brake Inspection"
    public String serviceDate; // e.g. "2026-09-30"
    public int mileageAtService;

    // Monetary fields using BigDecimal
    public BigDecimal laborCost;
    public BigDecimal partsCost;
    public BigDecimal totalCost;

    public String notes;

    /**
     * Calculates total cost = labor + parts using BigDecimal.
     */
    public void calculateTotalCost() {
        BigDecimal labor = (laborCost != null) ? laborCost : BigDecimal.ZERO;
        BigDecimal parts = (partsCost != null) ? partsCost : BigDecimal.ZERO;
        this.totalCost = labor.add(parts).setScale(2, RoundingMode.HALF_UP);
    }
}
