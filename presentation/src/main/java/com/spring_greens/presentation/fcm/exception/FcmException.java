package com.spring_greens.presentation.fcm.exception;

public abstract class FcmException extends RuntimeException{
    public FcmException (String message) {
        super(message);
    }

    public static class FcmFileNotFoundException extends FcmException{
        public FcmFileNotFoundException(String message) {
            super(message);
        }
    }

    public static class FcmIOException extends FcmException {
        public FcmIOException(String message) {
            super(message);
        }
    }

    public static class FcmCreatingTopicException extends FcmException{

        public FcmCreatingTopicException(String message) {
            super(message);
        }
    }

    public static class FcmIllegalArgumentException extends FcmException {

        public FcmIllegalArgumentException(String message) {
            super(message);
        }
    }

    public static class FcmSubscriptionException extends FcmException {
        public FcmSubscriptionException(String message) {
            super(message);
        }
    }
}
