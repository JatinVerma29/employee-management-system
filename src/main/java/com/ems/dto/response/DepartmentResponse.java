package com.ems.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DepartmentResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private Boolean isActive;
    private Long activeEmployeeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
