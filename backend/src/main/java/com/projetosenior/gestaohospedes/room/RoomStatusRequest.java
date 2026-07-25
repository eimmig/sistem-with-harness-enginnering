package com.projetosenior.gestaohospedes.room;

import jakarta.validation.constraints.NotNull;

public record RoomStatusRequest(@NotNull(message = "Status is required") RoomStatus status) {
}
