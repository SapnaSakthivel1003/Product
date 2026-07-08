package com.cmms.production.feignClients;

import com.cmms.production.dto.NotifyChangeEventDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "logistics-service", url = "http://localhost:8084/api/v1")
public interface NotifyFeignClient {
    @PostMapping("/logistics/notifications")
    void createNotifications(@RequestBody NotifyChangeEventDto notifyChangeEventDto);
}
