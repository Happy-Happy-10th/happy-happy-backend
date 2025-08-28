package com.happyhappy.backend.calendar.exception;

public class EventException extends RuntimeException {

    public EventException(String message) {
        super(message);
    }

    public static class RepeatPeriodException extends RuntimeException {

        public RepeatPeriodException(String message) {
            super(message);
        }
    }
}