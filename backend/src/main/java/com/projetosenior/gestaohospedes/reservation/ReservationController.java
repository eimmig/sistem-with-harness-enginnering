package com.projetosenior.gestaohospedes.reservation;

import com.projetosenior.gestaohospedes.guest.Guest;
import com.projetosenior.gestaohospedes.guest.GuestRepository;
import com.projetosenior.gestaohospedes.room.Room;
import com.projetosenior.gestaohospedes.room.RoomRepository;
import com.projetosenior.gestaohospedes.room.RoomStatus;
import jakarta.validation.Valid;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private static final LocalTime STANDARD_CHECK_IN_TIME = LocalTime.of(14, 0);

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final Clock clock;

    public ReservationController(
            ReservationRepository reservationRepository,
            GuestRepository guestRepository,
            RoomRepository roomRepository,
            Clock clock) {
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
        this.clock = clock;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody ReservationRequest request) {
        if (!request.expectedCheckOut().isAfter(request.expectedCheckIn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expected check-out must be after expected check-in");
        }
        Guest guest = guestRepository.findById(request.guestId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guest not found"));
        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        Reservation reservation = new Reservation(
                null, guest, room, request.expectedCheckIn(), request.expectedCheckOut(), request.parkingRequested());
        Reservation saved = reservationRepository.save(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(ReservationResponse.from(saved));
    }

    @PostMapping("/{id}/check-in")
    public ResponseEntity<ReservationResponse> checkIn(
            @PathVariable Long id, @RequestBody(required = false) CheckInRequest request) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        if (reservation.getActualCheckIn() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservation has already checked in");
        }

        Room room = reservation.getRoom();
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is not available for check-in");
        }

        LocalDateTime now = LocalDateTime.now(clock);
        boolean confirmedByAttendant = request != null && request.confirmedByAttendant();
        if (now.toLocalTime().isBefore(STANDARD_CHECK_IN_TIME) && !confirmedByAttendant) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Check-in before 2pm requires attendant confirmation");
        }

        reservation.setActualCheckIn(now);
        room.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);
        Reservation saved = reservationRepository.save(reservation);
        return ResponseEntity.ok(ReservationResponse.from(saved));
    }
}
