package com.projetosenior.gestaohospedes.reservation;

import com.projetosenior.gestaohospedes.dailyrate.DailyRateService;
import com.projetosenior.gestaohospedes.guest.Guest;
import com.projetosenior.gestaohospedes.guest.GuestRepository;
import com.projetosenior.gestaohospedes.parkingfee.ParkingFeeService;
import com.projetosenior.gestaohospedes.room.Room;
import com.projetosenior.gestaohospedes.room.RoomRepository;
import com.projetosenior.gestaohospedes.room.RoomStatus;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategory;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReservationService {

    private static final LocalTime STANDARD_CHECK_IN_TIME = LocalTime.of(14, 0);
    private static final LocalTime STANDARD_CHECK_OUT_TIME = LocalTime.of(12, 0);
    private static final BigDecimal LATE_CHECK_OUT_FEE_RATE = new BigDecimal("0.5");

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final Clock clock;
    private final DailyRateService dailyRateService;
    private final ParkingFeeService parkingFeeService;

    public ReservationService(
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

    public List<ReservationResponse> pendingCheckIn() {
        return reservationRepository.findByActualCheckInIsNull().stream().map(ReservationResponse::from).toList();
    }

    public List<ReservationResponse> pendingCheckOut() {
        return reservationRepository
                .findByActualCheckInIsNotNullAndActualCheckOutIsNull()
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public ReservationResponse create(ReservationRequest request) {
        if (!request.expectedCheckOut().isAfter(request.expectedCheckIn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expected check-out must be after expected check-in");
        }
        Guest guest = guestRepository.findById(request.guestId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guest not found"));
        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        boolean roomTakenForPeriod = !reservationRepository
                .findOverlappingActiveReservations(room.getId(), request.expectedCheckIn(), request.expectedCheckOut())
                .isEmpty();
        if (roomTakenForPeriod) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is not available for the requested dates");
        }

        Reservation reservation = new Reservation(
                null, guest, room, request.expectedCheckIn(), request.expectedCheckOut(), request.parkingRequested());
        Reservation saved = reservationRepository.save(reservation);
        return ReservationResponse.from(saved);
    }

    public ReservationResponse checkIn(Long id, CheckInRequest request) {
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
        return ReservationResponse.from(saved);
    }

    public CheckOutResponse checkOut(Long id) {
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

        return new CheckOutResponse(reservation.getId(), dailyRateTotal, parkingFeeTotal, lateCheckOutFee, total, now);
    }
}
