package service;

public abstract class ServiceException extends Exception {
    public ServiceException(String message) {
        super("Error: " + message);
    }
}
