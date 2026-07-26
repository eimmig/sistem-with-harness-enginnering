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
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private Clock clock;

    @Mock
    private DailyRateService dailyRateService;

    @Mock
    private ParkingFeeService parkingFeeService;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(
                reservationRepository, guestRepository, roomRepository, clock, dailyRateService, parkingFeeService);
    }

    private Guest guest() {
        return new Guest(1L, "Maria Silva", "12345678900", "11999998888");
    }

    private Room room(RoomStatus status) {
        return new Room(1L, "101", new RoomCategory(1L, "Standard"), status);
    }

    private Room roomWithPrices(RoomStatus status) {
        RoomCategory category = new RoomCategory(1L, "Standard");
        Map<DayOfWeek, BigDecimal> prices = new EnumMap<>(DayOfWeek.class);
        prices.put(DayOfWeek.MONDAY, new BigDecimal("120.00"));
        prices.put(DayOfWeek.TUESDAY, new BigDecimal("120.00"));
        prices.put(DayOfWeek.WEDNESDAY, new BigDecimal("120.00"));
        prices.put(DayOfWeek.THURSDAY, new BigDecimal("120.00"));
        prices.put(DayOfWeek.FRIDAY, new BigDecimal("120.00"));
        prices.put(DayOfWeek.SATURDAY, new BigDecimal("150.00"));
        prices.put(DayOfWeek.SUNDAY, new BigDecimal("150.00"));
        category.setPrices(prices);
        return new Room(1L, "101", category, status);
    }

    private void fixClockAt(LocalDateTime dateTime) {
        ZoneId zone = ZoneId.systemDefault();
        when(clock.instant()).thenReturn(dateTime.atZone(zone).toInstant());
        when(clock.getZone()).thenReturn(zone);
    }

    // --- create ---

    @Test
    void createsReservation() {
        Guest guest = guest();
        Room room = room(RoomStatus.AVAILABLE);
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        Reservation saved = new Reservation(1L, guest, room, checkIn, checkOut, true);
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.findOverlappingActiveReservations(1L, checkIn, checkOut)).thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(saved);

        ReservationResponse response = reservationService.create(new ReservationRequest(1L, 1L, checkIn, checkOut, true));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.guest().name()).isEqualTo("Maria Silva");
        assertThat(response.room().number()).isEqualTo("101");
        assertThat(response.parkingRequested()).isTrue();
    }

    @Test
    void rejectsCheckOutNotAfterCheckIn() {
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 3, 12, 0);

        assertThatThrownBy(() -> reservationService.create(new ReservationRequest(1L, 1L, checkIn, checkOut, false)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400");
    }

    @Test
    void rejectsWhenGuestDoesNotExist() {
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        when(guestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.create(new ReservationRequest(99L, 1L, checkIn, checkOut, false)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    @Test
    void rejectsWhenRoomDoesNotExist() {
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest()));
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.create(new ReservationRequest(1L, 99L, checkIn, checkOut, false)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    @Test
    void rejectsWhenRoomHasOverlappingActiveReservation() {
        // D-41: outra reserva no mesmo quarto, sem check-out, com datas sobrepostas -> 409
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 5, 12, 0);
        Reservation conflicting = new Reservation(
                2L, guest(), room(RoomStatus.AVAILABLE), LocalDateTime.of(2026, 8, 4, 14, 0), LocalDateTime.of(2026, 8, 6, 12, 0), false);
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest()));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room(RoomStatus.AVAILABLE)));
        when(reservationRepository.findOverlappingActiveReservations(1L, checkIn, checkOut))
                .thenReturn(List.of(conflicting));

        assertThatThrownBy(() -> reservationService.create(new ReservationRequest(1L, 1L, checkIn, checkOut, false)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409");
    }

    @Test
    void allowsReservationWhenNoOverlappingActiveReservationExists() {
        // Datas nao se sobrepoem a nenhuma reserva ativa do mesmo quarto -> permitido
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        Guest guest = guest();
        Room room = room(RoomStatus.AVAILABLE);
        Reservation saved = new Reservation(1L, guest, room, checkIn, checkOut, false);
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.findOverlappingActiveReservations(1L, checkIn, checkOut)).thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(saved);

        ReservationResponse response = reservationService.create(new ReservationRequest(1L, 1L, checkIn, checkOut, false));

        assertThat(response.id()).isEqualTo(1L);
    }

    // --- pending listings ---

    @Test
    void listsReservationsPendingCheckIn() {
        Reservation reservation = new Reservation(
                1L, guest(), room(RoomStatus.AVAILABLE), LocalDateTime.of(2026, 8, 3, 14, 0), LocalDateTime.of(2026, 8, 4, 12, 0), false);
        when(reservationRepository.findByActualCheckInIsNull()).thenReturn(List.of(reservation));

        List<ReservationResponse> result = reservationService.pendingCheckIn();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).guest().name()).isEqualTo("Maria Silva");
    }

    @Test
    void listsReservationsPendingCheckOut() {
        Reservation reservation = new Reservation(
                1L, guest(), room(RoomStatus.OCCUPIED), LocalDateTime.of(2026, 8, 3, 14, 0), LocalDateTime.of(2026, 8, 4, 12, 0), false);
        reservation.setActualCheckIn(LocalDateTime.of(2026, 8, 3, 14, 0));
        when(reservationRepository.findByActualCheckInIsNotNullAndActualCheckOutIsNull()).thenReturn(List.of(reservation));

        List<ReservationResponse> result = reservationService.pendingCheckOut();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).guest().name()).isEqualTo("Maria Silva");
    }

    // --- check-in ---

    @Test
    void checkInValid() {
        Room room = room(RoomStatus.AVAILABLE);
        LocalDateTime expectedCheckIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime expectedCheckOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        LocalDateTime now = LocalDateTime.of(2026, 8, 3, 15, 0);
        Reservation existing = new Reservation(1L, guest(), room, expectedCheckIn, expectedCheckOut, false);
        Reservation checkedIn = new Reservation(1L, guest(), room, expectedCheckIn, expectedCheckOut, false);
        checkedIn.setActualCheckIn(now);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(checkedIn);
        fixClockAt(now);

        ReservationResponse response = reservationService.checkIn(1L, null);

        assertThat(response.actualCheckIn()).isEqualTo(now);
    }

    @Test
    void checkInBefore2pmRequiresConfirmation() {
        Room room = room(RoomStatus.AVAILABLE);
        LocalDateTime expectedCheckIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime expectedCheckOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        LocalDateTime now = LocalDateTime.of(2026, 8, 3, 9, 0);
        Reservation existing = new Reservation(1L, guest(), room, expectedCheckIn, expectedCheckOut, false);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));
        fixClockAt(now);

        assertThatThrownBy(() -> reservationService.checkIn(1L, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400");
    }

    @Test
    void checkInBefore2pmWithConfirmationSucceeds() {
        Room room = room(RoomStatus.AVAILABLE);
        LocalDateTime expectedCheckIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime expectedCheckOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        LocalDateTime now = LocalDateTime.of(2026, 8, 3, 9, 0);
        Reservation existing = new Reservation(1L, guest(), room, expectedCheckIn, expectedCheckOut, false);
        Reservation checkedIn = new Reservation(1L, guest(), room, expectedCheckIn, expectedCheckOut, false);
        checkedIn.setActualCheckIn(now);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(checkedIn);
        fixClockAt(now);

        ReservationResponse response = reservationService.checkIn(1L, new CheckInRequest(true));

        assertThat(response.actualCheckIn()).isEqualTo(now);
    }

    @Test
    void checkInRejectsWhenRoomUnavailable() {
        Room room = room(RoomStatus.OCCUPIED);
        LocalDateTime expectedCheckIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime expectedCheckOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        Reservation existing = new Reservation(1L, guest(), room, expectedCheckIn, expectedCheckOut, false);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> reservationService.checkIn(1L, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409");
    }

    @Test
    void checkInRejectsWhenAlreadyDone() {
        Room room = room(RoomStatus.OCCUPIED);
        LocalDateTime expectedCheckIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime expectedCheckOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        Reservation existing = new Reservation(1L, guest(), room, expectedCheckIn, expectedCheckOut, false);
        existing.setActualCheckIn(LocalDateTime.of(2026, 8, 3, 15, 0));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> reservationService.checkIn(1L, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409");
    }

    @Test
    void checkInRejectsWhenReservationNotFound() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.checkIn(99L, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    // --- check-out ---

    @Test
    void checkOutAppliesLateFee() {
        Room room = roomWithPrices(RoomStatus.OCCUPIED);
        LocalDateTime actualCheckIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime now = LocalDateTime.of(2026, 8, 4, 13, 0);
        Reservation existing = new Reservation(1L, guest(), room, actualCheckIn, actualCheckIn.plusDays(1), true);
        existing.setActualCheckIn(actualCheckIn);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(dailyRateService.calculate(any(RoomCategory.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("120.00"));
        when(parkingFeeService.calculate(eq(true), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("15.00"));
        fixClockAt(now);

        CheckOutResponse response = reservationService.checkOut(1L);

        assertThat(response.dailyRateTotal()).isEqualByComparingTo("120.00");
        assertThat(response.parkingFeeTotal()).isEqualByComparingTo("15.00");
        assertThat(response.lateCheckOutFee()).isEqualByComparingTo("60.00");
        assertThat(response.total()).isEqualByComparingTo("195.00");
    }

    @Test
    void checkOutOnTimeHasNoLateFee() {
        Room room = roomWithPrices(RoomStatus.OCCUPIED);
        LocalDateTime actualCheckIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime now = LocalDateTime.of(2026, 8, 4, 11, 0);
        Reservation existing = new Reservation(1L, guest(), room, actualCheckIn, actualCheckIn.plusDays(1), true);
        existing.setActualCheckIn(actualCheckIn);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(dailyRateService.calculate(any(RoomCategory.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("120.00"));
        when(parkingFeeService.calculate(eq(true), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("15.00"));
        fixClockAt(now);

        CheckOutResponse response = reservationService.checkOut(1L);

        assertThat(response.reservationId()).isEqualTo(1L);
        assertThat(response.lateCheckOutFee()).isEqualByComparingTo("0");
        assertThat(response.total()).isEqualByComparingTo("135.00");
        assertThat(response.actualCheckOut()).isEqualTo(now);
    }

    @Test
    void checkOutRejectsWhenNotCheckedInYet() {
        Room room = roomWithPrices(RoomStatus.AVAILABLE);
        LocalDateTime expectedCheckIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime expectedCheckOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        Reservation existing = new Reservation(1L, guest(), room, expectedCheckIn, expectedCheckOut, false);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> reservationService.checkOut(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409");
    }

    @Test
    void checkOutRejectsWhenAlreadyDone() {
        Room room = roomWithPrices(RoomStatus.DIRTY);
        LocalDateTime actualCheckIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        Reservation existing = new Reservation(1L, guest(), room, actualCheckIn, actualCheckIn.plusDays(1), false);
        existing.setActualCheckIn(actualCheckIn);
        existing.setActualCheckOut(LocalDateTime.of(2026, 8, 4, 11, 0));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> reservationService.checkOut(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409");
    }

    @Test
    void checkOutRejectsWhenReservationNotFound() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.checkOut(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    @Test
    void checkOutSameCalendarDayAsCheckInChargesMinimumOneNight() {
        // Regressao de D-39: garante que o service continua delegando corretamente para
        // DailyRateService/ParkingFeeService mesmo quando eles cobram o minimo de 1 dia.
        Room room = roomWithPrices(RoomStatus.OCCUPIED);
        LocalDateTime actualCheckIn = LocalDateTime.of(2026, 8, 3, 9, 0);
        LocalDateTime now = LocalDateTime.of(2026, 8, 3, 11, 0);
        Reservation existing = new Reservation(1L, guest(), room, actualCheckIn, actualCheckIn.plusDays(1), true);
        existing.setActualCheckIn(actualCheckIn);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(dailyRateService.calculate(any(RoomCategory.class), eq(actualCheckIn), eq(now)))
                .thenReturn(new BigDecimal("120.00"));
        when(parkingFeeService.calculate(eq(true), eq(actualCheckIn), eq(now)))
                .thenReturn(new BigDecimal("15.00"));
        fixClockAt(now);

        CheckOutResponse response = reservationService.checkOut(1L);

        assertThat(response.total()).isEqualByComparingTo("135.00");
    }
}
