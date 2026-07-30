// dto/LoginRequest.java
package com.empresa.gestionclientes.dto;

public record LoginRequest(
        String username,
        String password
) {}