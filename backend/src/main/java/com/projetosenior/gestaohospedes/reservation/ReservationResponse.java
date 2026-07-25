package com.projetosenior.gestaohospedes.reservation;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        GuestSummary guest,
        RoomSummary room,
        LocalDateTime expectedCheckIn,
        LocalDateTime expectedCheckOut,
        boolean parkingRequested,
        LocalDateTime actualCheckIn) {

    public record GuestSummary(Long id, String name) {
    }

    public record RoomSummary(Long id, String number) {
    }

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                new GuestSummary(reservation.getGuest().getId(), reservation.getGuest().getName()),
                new RoomSummary(reservation.getRoom().getId(), reservation.getRoom().getNumber()),
                reservation.getExpectedCheckIn(),
                reservation.getExpectedCheckOut(),
                reservation.isParkingRequested(),
                reservation.getActualCheckIn());
    }
}
