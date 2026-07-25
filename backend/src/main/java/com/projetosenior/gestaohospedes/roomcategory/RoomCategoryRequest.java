package com.projetosenior.gestaohospedes.roomcategory;

import jakarta.validation.constraints.NotBlank;

public record RoomCategoryRequest(@NotBlank(message = "Name is required") String name) {
}
