package com.projetosenior.gestaohospedes.reservation;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/pending-check-in")
    public ResponseEntity<List<ReservationResponse>> pendingCheckIn() {
        return ResponseEntity.ok(reservationService.pendingCheckIn());
    }

    @GetMapping("/pending-check-out")
    public ResponseEntity<List<ReservationResponse>> pendingCheckOut() {
        return ResponseEntity.ok(reservationService.pendingCheckOut());
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(request));
    }

    @PostMapping("/{id}/check-in")
    public ResponseEntity<ReservationResponse> checkIn(
            @PathVariable Long id, @RequestBody(required = false) CheckInRequest request) {
        return ResponseEntity.ok(reservationService.checkIn(id, request));
    }

    @PostMapping("/{id}/check-out")
    public ResponseEntity<CheckOutResponse> checkOut(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.checkOut(id));
    }
}
