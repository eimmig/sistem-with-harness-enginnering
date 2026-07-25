package com.projetosenior.gestaohospedes.guest;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping
    public ResponseEntity<List<GuestResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String document,
            @RequestParam(required = false) String phone) {
        Specification<Guest> spec = Specification.unrestricted();
        if (StringUtils.hasText(name)) {
            spec = spec.and(GuestSpecifications.nameContains(name));
        }
        if (StringUtils.hasText(document)) {
            spec = spec.and(GuestSpecifications.documentContains(document));
        }
        if (StringUtils.hasText(phone)) {
            spec = spec.and(GuestSpecifications.phoneContains(phone));
        }
        List<GuestResponse> guests = guestRepository.findAll(spec).stream().map(GuestResponse::from).toList();
        return ResponseEntity.ok(guests);
    }
}
