package com.cmms.production.feignClients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", url = "http://localhost:8080/api/v1",configuration =FeignClientSecurityConfig.class)
public interface UserClient {
    @GetMapping("/api/v1/auth/by-role")
    List<String> getPlantManagerEmails(@RequestParam("roleName") String roleName);
}
