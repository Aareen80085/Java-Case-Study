/**
 * Thrown when trying to register a vehicle with an existing registration number.
 */
public class DuplicateVehicleException extends Exception {
    public DuplicateVehicleException(String registrationNumber) {
        super("Vehicle with Registration Number '" + registrationNumber + "' already exists!");
    }
}
