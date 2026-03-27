package com.panelsv.backend.controller.dto;

public class LoginResponse {
    private String status;
    private String token; // opcional (deixa nulo por enquanto)

    public LoginResponse() {}

    // usado no seu controller: new LoginResponse("ok") / "registered"
    public LoginResponse(String status) {
        this.status = status;
    }

    public LoginResponse(String status, String token) {
        this.status = status;
        this.token = token;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
