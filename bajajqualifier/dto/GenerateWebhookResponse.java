package com.example.bajajqualifier.dto;

import lombok.Data;

/**
 * Expected response: webhook URL + accessToken (JWT).
 */
@Data
public class GenerateWebhookResponse {
    private String webhook;
    private String accessToken;
}
 
