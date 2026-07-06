package com.cmms.production.user_context;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserContext {
    private Long userId;
    private String username;
    private String roles;
}
