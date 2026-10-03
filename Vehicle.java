import java.util.LinkedList;

/**
 * Vehicle Entity.
 * Holds vehicle specifications and an ordered LinkedList of its service history.
 * All core business calculations are centralized in VehicleServiceManager.java.
 */
public class Vehicle {

    // Vehicle details
    public String registrationNumber;
    public String brand;
    public String model;
    public int year;
    public String vehicleType; // "Car", "Truck", "Van", "Bus"

    // Mileage & service milestone tracking
    public int currentMileage;
    public int serviceInterval;    // e.g. service needed every 10,000 km
    public int nextServiceMileage; // e.g. due at 20,000 km
    public String status;          // "Active" or "Service Due"

    // LinkedList maintains ordered chronological history of services
    public LinkedList<ServiceRecord> serviceHistory = new LinkedList<>();

    /**
     * Checks if current mileage has reached or passed the next service milestone.
     */
    public boolean isServiceDue() {
        return currentMileage >= nextServiceMileage;
    }

    /**
     * Updates status text based on mileage milestone.
     */
    public void updateStatus() {
        if (isServiceDue()) {
            this.status = "Service Due";
        } else {
            this.status = "Active";
        }
    }
}
