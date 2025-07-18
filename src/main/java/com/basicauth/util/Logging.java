package com.basicauth.util;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logging {
    private final String PATH = "logs/basicauth.log";
    private final Path FILE_PATH = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("basicauth/" + PATH);
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("'['dd/MM/yyyy']' '['HH:mm:ss']' ");

    private String formatWithBraces(String message, Object... args) {
        for (Object arg : args) {
            message = message.replaceFirst("\\{}", java.util.regex.Matcher.quoteReplacement(String.valueOf(arg)));
        }
        return message;
    }

    public Logging() {
        try {
            if (!java.nio.file.Files.exists(FILE_PATH)) {
                java.nio.file.Files.createDirectories(FILE_PATH.getParent());
                java.nio.file.Files.createFile(FILE_PATH);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void log(String message, Object... args) {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(FILE_PATH.toFile(), true))) {
            String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
            String formattedMessage = formatWithBraces(message, args);
            writer.println(timestamp + formattedMessage);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void info(String message, Object... args) {
        log("INFO: " + message, args);
    }

    public void warn(String message, Object... args) {
        log("WARN: " + message, args);
    }

    public void error(String message, Object... args) {
        log("ERROR: " + message, args);
    }

    public void debug(String message, Object... args) {
        log("DEBUG: " + message, args);
    }
}


