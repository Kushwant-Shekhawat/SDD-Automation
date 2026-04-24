package org.example.utils;

public class CustomExceptions {

    public static class FrameworkException extends RuntimeException {
        public FrameworkException(String message) { super(message); }
        public FrameworkException(String message, Throwable cause) { super(message, cause); }
    }

    public static class ElementNotFoundException extends FrameworkException {
        public ElementNotFoundException(String locator) {
            super("Element not found: " + locator);
        }
        public ElementNotFoundException(String locator, Throwable cause) {
            super("Element not found: " + locator, cause);
        }
    }

    public static class ConfigurationException extends FrameworkException {
        public ConfigurationException(String message) { super(message); }
    }
}
