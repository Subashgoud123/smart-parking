package com.smartparking.dto;

import java.util.Set;

public class UserDto {
    public Long id;
    public String email;
    public String fullName;
    public String phone;
    public boolean active;
    public Set<String> roles;
}
