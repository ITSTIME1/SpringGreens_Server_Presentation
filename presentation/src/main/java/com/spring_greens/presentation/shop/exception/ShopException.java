package com.spring_greens.presentation.shop.exception;

public abstract class ShopException extends RuntimeException{
    public ShopException(String message) {
        super(message);
    }

    public static class ShopDataNotFoundException extends ShopException {
        public ShopDataNotFoundException (String message) {
            super(message);
        }
    }

}
