package com.cmms.production.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExistenceResponseDto {
    @JsonProperty("exists")
    private boolean exists;
}

