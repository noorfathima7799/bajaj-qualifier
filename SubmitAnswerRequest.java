package com.example.bajajqualifier.dto;

public class SubmitAnswerRequest {
    private String finalQuery;

    public SubmitAnswerRequest() {}

    public SubmitAnswerRequest(String finalQuery) {
        this.finalQuery = finalQuery;
    }

    public String getFinalQuery() { return finalQuery; }
    public void setFinalQuery(String finalQuery) { this.finalQuery = finalQuery; }
}
