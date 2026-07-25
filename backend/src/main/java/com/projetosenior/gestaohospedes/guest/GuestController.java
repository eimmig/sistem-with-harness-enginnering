package com.projetosenior.gestaohospedes.guest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

    private final GuestRepository guestRepository;

    public GuestController(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @PostMapping
    public ResponseEntity<GuestResponse> create(@Valid @RequestBody GuestRequest request) {
        Guest guest = new Guest(null, request.name(), request.document(), request.phone());
        Guest saved = guestRepository.save(guest);
        return ResponseEntity.status(HttpStatus.CREATED).body(GuestResponse.from(saved));
    }
}
