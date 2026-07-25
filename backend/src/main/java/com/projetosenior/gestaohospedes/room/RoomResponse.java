package com.projetosenior.gestaohospedes.room;

public record RoomResponse(Long id, String number, RoomStatus status, CategorySummary category) {

    public record CategorySummary(Long id, String name) {
    }

    public static RoomResponse from(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getNumber(),
                room.getStatus(),
                new CategorySummary(room.getRoomCategory().getId(), room.getRoomCategory().getName()));
    }
}
