package com.CMMS.Production.UserContext;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class UserContext {
    private Long userId;
    private String username;
    private List<String> role;
    public UserContext(Long userId, String username, List<String> roles) {
        this.userId = userId;
        this.username = username;
        this.role = roles;
    }

}
