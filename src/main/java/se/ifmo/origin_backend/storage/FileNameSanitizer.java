package se.ifmo.origin_backend.storage;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileNameSanitizer {
    private static final Pattern UNSAFE = Pattern.compile("[^a-zA-Z0-9._-]");

    public static String sanitize(String original) {
        if (original == null || original.isBlank()) {
            return "import.json";
        }
        String sanitized = UNSAFE.matcher(original).replaceAll("_");
        if (sanitized.isBlank()) {
            return "import.json";
        }
        return sanitized.length() > 120 ? sanitized.substring(0, 120) : sanitized;
    }
}
