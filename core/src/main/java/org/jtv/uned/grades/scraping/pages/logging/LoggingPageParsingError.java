package org.jtv.uned.grades.scraping.pages.logging;

class LoggingPageParsingError extends RuntimeException {
    LoggingPageParsingError(final Throwable cause) {
        super(cause);
    }
}
