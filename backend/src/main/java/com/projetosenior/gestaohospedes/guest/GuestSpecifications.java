package com.projetosenior.gestaohospedes.guest;

import org.springframework.data.jpa.domain.Specification;

public final class GuestSpecifications {

    private GuestSpecifications() {
    }

    public static Specification<Guest> nameContains(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Guest> documentContains(String document) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("document")), "%" + document.toLowerCase() + "%");
    }

    public static Specification<Guest> phoneContains(String phone) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("phone")), "%" + phone.toLowerCase() + "%");
    }
}
