package com.projetosenior.gestaohospedes.reservation;

import com.projetosenior.gestaohospedes.dailyrate.DailyRateService;
import com.projetosenior.gestaohospedes.guest.Guest;
import com.projetosenior.gestaohospedes.guest.GuestRepository;
import com.projetosenior.gestaohospedes.parkingfee.ParkingFeeService;
import com.projetosenior.gestaohospedes.room.Room;
import com.projetosenior.gestaohospedes.room.RoomRepository;
import com.projetosenior.gestaohospedes.room.RoomStatus;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategory;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.DayOfWeek;
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
    private static final LocalTime STANDARD_CHECK_OUT_TIME = LocalTime.of(12, 0);
    private static final BigDecimal LATE_CHECK_OUT_FEE_RATE = new BigDecimal("0.5");

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final Clock clock;
    private final DailyRateService dailyRateService;
    private final ParkingFeeService parkingFeeService;

    public ReservationController(
            ReservationRepository reservationRepository,
            GuestRepository guestRepository,
            RoomRepository roomRepository,
            Clock clock,
            DailyRateService dailyRateService,
            ParkingFeeService parkingFeeService) {
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
        this.clock = clock;
        this.dailyRateService = dailyRateService;
        this.parkingFeeService = parkingFeeService;
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

    @PostMapping("/{id}/check-out")
    public ResponseEntity<CheckOutResponse> checkOut(@PathVariable Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        if (reservation.getActualCheckIn() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservation has not checked in yet");
        }
        if (reservation.getActualCheckOut() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservation has already checked out");
        }

        LocalDateTime now = LocalDateTime.now(clock);
        Room room = reservation.getRoom();
        RoomCategory roomCategory = room.getRoomCategory();

        BigDecimal dailyRateTotal = dailyRateService.calculate(roomCategory, reservation.getActualCheckIn(), now);
        BigDecimal parkingFeeTotal =
                parkingFeeService.calculate(reservation.isParkingRequested(), reservation.getActualCheckIn(), now);

        BigDecimal lateCheckOutFee = BigDecimal.ZERO;
        if (now.toLocalTime().isAfter(STANDARD_CHECK_OUT_TIME)) {
            DayOfWeek lastNight = now.toLocalDate().minusDays(1).getDayOfWeek();
            BigDecimal lastNightPrice = roomCategory.getPrices().get(lastNight);
            lateCheckOutFee = lastNightPrice.multiply(LATE_CHECK_OUT_FEE_RATE);
        }

        BigDecimal total = dailyRateTotal.add(parkingFeeTotal).add(lateCheckOutFee);

        reservation.setActualCheckOut(now);
        room.setStatus(RoomStatus.DIRTY);
        roomRepository.save(room);
        reservationRepository.save(reservation);

        return ResponseEntity.ok(
                new CheckOutResponse(reservation.getId(), dailyRateTotal, parkingFeeTotal, lateCheckOutFee, total, now));
    }
}
