/**
 * CustomExceptionInterface defines methods for handling custom exceptions.
 */
public interface CustomExceptionInterface {

    /**
     * Gets the error message associated with the exception.
     *
     * @return The error message as a String.
     */
    String getErrorMessage();

    /**
     * Displays or logs the exception message.
     *
     * @param message The exception message to be displayed or logged.
     */
    void showException(String message);
}
