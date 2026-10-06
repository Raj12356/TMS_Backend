package com.tms.dto;

public class UserResponseWrapper {
    private UserDto user;

    public UserResponseWrapper() {
    }

    public UserResponseWrapper(UserDto user) {
        this.user = user;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}

