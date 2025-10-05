package com.ideaspark.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorDTO {
    private String field;
    private Object rejectedValue;
    private String message;
    private String code;
}