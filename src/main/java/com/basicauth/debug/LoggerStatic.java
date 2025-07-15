package com.basicauth.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerStatic {
    public static final Logger LOGGER = LoggerFactory.getLogger(LoggerStatic.class);

    public static void error(String error) {
        LOGGER.error(error);
    }

    public static void info(String info) {
        LOGGER.info(info);
    }
}
