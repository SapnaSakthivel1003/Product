package com.cmms.production.feignClients;

import com.cmms.production.dto.DataChangeEventDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "history-service", url = "http://localhost:8085/api/v1")
public interface AuditLogFeignClient {
    @PostMapping("/system/auditLogs")
    void createAuditLogs(@RequestBody DataChangeEventDto eventDto);
}
