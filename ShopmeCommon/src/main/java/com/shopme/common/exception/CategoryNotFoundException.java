package com.shopme.common.exception;

public class CategoryNotFoundException extends Exception {

    // Thêm serialVersionUID
    private static final long serialVersionUID = 1L;

    public CategoryNotFoundException(String message) {
        super(message);
    }

}
