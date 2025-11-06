package xyz.toway.notes.service;

public class UtilsService {

    /**
     * Sanitizes a string to be a valid file name.
     * Removes or replaces invalid characters: \ / : * ? " < > |
     * Trims spaces and dots from the end.
     */
    public String toValidFileName(String input) {
        if (input == null || input.isBlank()) {
            return "unnamed";
        }

        // Replace invalid characters with underscore
        String sanitized = input.replaceAll("[\\\\/:*?\"<>|]", "_");

        // Trim spaces and dots from end (Windows restriction)
        sanitized = sanitized.replaceAll("[\\s.]+$", "");

        // Prevent empty result
        if (sanitized.isEmpty()) {
            sanitized = "unnamed";
        }

        return sanitized;
    }

}
