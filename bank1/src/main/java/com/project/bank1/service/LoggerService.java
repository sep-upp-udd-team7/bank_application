package com.project.bank1.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerService {

    private final Logger logger;

    public LoggerService(Class<?> parentClass) {
        this.logger = LogManager.getLogger(parentClass);
    }

    public void infoLog(String message) {
        logger.info(message);
    }

    public void debugLog(String message) {
        logger.debug(message);
    }

    public void warnLog(String message) {
        logger.warn(message);
    }

    public void successLog(String message) {
        logger.info(message);
    }

    public void errorLog(String message) {
        logger.error(message);
    }
}