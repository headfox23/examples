package com.example.repomanager.model.general;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@AllArgsConstructor
@Data
public class ErrorMessage {
    private int statusCode;
    private Instant timestamp;
    private String message;
    private String description;
}
