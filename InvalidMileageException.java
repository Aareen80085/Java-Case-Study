/**
 * Thrown when entered mileage is invalid or lower than current odometer reading.
 */
public class InvalidMileageException extends Exception {
    public InvalidMileageException(String message) {
        super(message);
    }
}
