package com.projetosenior.gestaohospedes.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoomRequest(
        @NotBlank(message = "Number is required") String number,
        @NotNull(message = "Room category is required") Long roomCategoryId) {
}
