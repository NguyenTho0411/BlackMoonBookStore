package com.shopme.admin.user;

public class UserNotFoundException extends Exception {

    // Thêm serialVersionUID
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String message) {
        super(message);
    }

}
