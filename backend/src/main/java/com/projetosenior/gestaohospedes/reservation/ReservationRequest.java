package com.projetosenior.gestaohospedes.reservation;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ReservationRequest(
        @NotNull(message = "Guest is required") Long guestId,
        @NotNull(message = "Room is required") Long roomId,
        @NotNull(message = "Expected check-in is required") LocalDateTime expectedCheckIn,
        @NotNull(message = "Expected check-out is required") LocalDateTime expectedCheckOut,
        boolean parkingRequested) {
}
