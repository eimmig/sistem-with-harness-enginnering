package com.projetosenior.gestaohospedes.roomcategory;

public record RoomCategoryResponse(Long id, String name) {

    public static RoomCategoryResponse from(RoomCategory roomCategory) {
        return new RoomCategoryResponse(roomCategory.getId(), roomCategory.getName());
    }
}
