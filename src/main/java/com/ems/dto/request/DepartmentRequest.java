package com.ems.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentRequest {

    @NotBlank(message = "Department name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Department code is required")
    @Size(max = 20)
    @Pattern(regexp = "^[A-Z]{2,10}$", message = "Code must be uppercase letters only (2-10 chars)")
    private String code;

    private String description;
}
