package com.spring_greens.presentation.global.exception;

public abstract class CommonException extends RuntimeException{
    public CommonException(String message) {
        super(message);
    }


    public static class CustomNullPointerException extends CommonException{
        public CustomNullPointerException (String message) {
            super(message);
        }

    }

    public static class CustomRegexPatternException extends CommonException {
        public CustomRegexPatternException(String message) {
            super(message);
        }
    }

    public static class CustomIllegalArgumentException extends CommonException {
        public CustomIllegalArgumentException(String message) {
            super(message);
        }
    }
}
