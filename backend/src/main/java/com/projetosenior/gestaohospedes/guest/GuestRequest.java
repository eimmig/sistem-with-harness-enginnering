package com.projetosenior.gestaohospedes.guest;

import jakarta.validation.constraints.NotBlank;

public record GuestRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Document is required") String document,
        @NotBlank(message = "Phone is required") String phone) {
}
