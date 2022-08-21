package com.example.backend.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class OtpResponse {
    @JsonProperty("Success")
    public boolean success;
    @JsonProperty("Errors")
    public ArrayList<Object> errors;
    @JsonProperty("Results")
    public String results;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ArrayList<Object> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<Object> errors) {
        this.errors = errors;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }
}
