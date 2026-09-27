package com.alocanote.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SendSmsRequestDTO(
        @NotBlank(message = "O número de telefone é obrigatório.")
        String phone
) {}