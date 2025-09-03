package com.example.bajajqualifier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for submitting the final SQL query.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerRequest {
    private String finalQuery;
}
 
