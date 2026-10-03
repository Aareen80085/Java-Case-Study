/**
 * Thrown when attempting to find or service a vehicle that does not exist.
 */
public class VehicleNotFoundException extends Exception {
    public VehicleNotFoundException(String registrationNumber) {
        super("Vehicle not found with Registration Number: " + registrationNumber);
    }
}
