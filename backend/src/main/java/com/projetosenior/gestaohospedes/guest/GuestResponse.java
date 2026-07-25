package com.projetosenior.gestaohospedes.guest;

public record GuestResponse(Long id, String name, String document, String phone) {

    public static GuestResponse from(Guest guest) {
        return new GuestResponse(guest.getId(), guest.getName(), guest.getDocument(), guest.getPhone());
    }
}
