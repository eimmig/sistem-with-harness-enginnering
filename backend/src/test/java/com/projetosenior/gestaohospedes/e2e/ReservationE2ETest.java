package com.projetosenior.gestaohospedes.e2e;

import com.projetosenior.gestaohospedes.guest.GuestRequest;
import com.projetosenior.gestaohospedes.guest.GuestResponse;
import com.projetosenior.gestaohospedes.reservation.CheckInRequest;
import com.projetosenior.gestaohospedes.reservation.CheckOutResponse;
import com.projetosenior.gestaohospedes.reservation.ReservationRequest;
import com.projetosenior.gestaohospedes.reservation.ReservationResponse;
import com.projetosenior.gestaohospedes.room.RoomRequest;
import com.projetosenior.gestaohospedes.room.RoomResponse;
import com.projetosenior.gestaohospedes.room.RoomStatus;
import com.projetosenior.gestaohospedes.room.RoomStatusRequest;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategoryPricesRequest;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategoryRequest;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategoryResponse;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end tests for the Reservation / Check-in / Check-out domain: real Spring Boot server
 * (RANDOM_PORT) over real HTTP (TestRestTemplate) against a real Postgres instance
 * (Testcontainers) -- no MockMvc, no H2, no mocked repositories or services. Validates daily-rate
 * and parking-fee calculation end-to-end (highest-risk domain per DECISIONS.md D-02/D-03).
 *
 * <p>The Clock bean (D-21) is swapped for a mutable test double so check-in/check-out "now" is
 * deterministic -- this is the same seam ReservationControllerTest already relies on, just backed
 * by a real bean instead of a Mockito mock.
 */
@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureTestRestTemplate
class ReservationE2ETest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @TestConfiguration
    static class MutableClockConfig {
        @Bean
        @Primary
        Clock clock() {
            return new MutableClock();
        }
    }

    static class MutableClock extends Clock {
        private volatile Instant instant = Instant.now();
        private final ZoneId zone = ZoneId.systemDefault();

        void setNow(LocalDateTime dateTime) {
            this.instant = dateTime.atZone(zone).toInstant();
        }

        @Override
        public ZoneId getZone() {
            return zone;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Clock clock;

    private MutableClock mutableClock() {
        return (MutableClock) clock;
    }

    private GuestResponse createGuest(String name) {
        ResponseEntity<GuestResponse> response = restTemplate.postForEntity(
                "/api/guests", new GuestRequest(name, "E2E-" + System.nanoTime(), "11999990000"), GuestResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody();
    }

    private RoomResponse createRoomWithPrices(String weekdayPrice, String weekendPrice) {
        ResponseEntity<RoomCategoryResponse> categoryResponse = restTemplate.postForEntity(
                "/api/room-categories",
                new RoomCategoryRequest("Standard E2E Reservation " + System.nanoTime()),
                RoomCategoryResponse.class);
        assertThat(categoryResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long categoryId = categoryResponse.getBody().id();

        Map<DayOfWeek, BigDecimal> prices = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            boolean weekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
            prices.put(day, new BigDecimal(weekend ? weekendPrice : weekdayPrice));
        }
        RequestEntity<RoomCategoryPricesRequest> pricesRequest =
                RequestEntity.put("/api/room-categories/" + categoryId + "/prices")
                        .body(new RoomCategoryPricesRequest(prices));
        assertThat(restTemplate.exchange(pricesRequest, RoomCategoryResponse.class).getStatusCode())
                .isEqualTo(HttpStatus.OK);

        ResponseEntity<RoomResponse> roomResponse = restTemplate.postForEntity(
                "/api/rooms", new RoomRequest("E2E-" + System.nanoTime(), categoryId), RoomResponse.class);
        assertThat(roomResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return roomResponse.getBody();
    }

    private ReservationResponse createReservation(Long guestId, Long roomId, boolean parkingRequested) {
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        ResponseEntity<ReservationResponse> response = restTemplate.postForEntity(
                "/api/reservations",
                new ReservationRequest(guestId, roomId, checkIn, checkOut, parkingRequested),
                ReservationResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody();
    }

    @Test
    void createReservation_persistsInRealDatabaseAndReturnsCreated() {
        GuestResponse guest = createGuest("Fabio Mendes E2E");
        RoomResponse room = createRoomWithPrices("120.00", "150.00");

        ReservationResponse reservation = createReservation(guest.id(), room.id(), true);

        assertThat(reservation.id()).isNotNull();
        assertThat(reservation.guest().id()).isEqualTo(guest.id());
        assertThat(reservation.room().id()).isEqualTo(room.id());
        assertThat(reservation.parkingRequested()).isTrue();
        assertThat(reservation.actualCheckIn()).isNull();
    }

    @Test
    void createReservation_rejectsCheckOutNotAfterCheckIn() {
        GuestResponse guest = createGuest("Gabriela Rocha E2E");
        RoomResponse room = createRoomWithPrices("120.00", "150.00");
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 3, 12, 0);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/reservations", new ReservationRequest(guest.id(), room.id(), checkIn, checkOut, false), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createReservation_returnsNotFoundForUnknownGuestOrRoom() {
        RoomResponse room = createRoomWithPrices("120.00", "150.00");
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 4, 12, 0);

        ResponseEntity<String> unknownGuest = restTemplate.postForEntity(
                "/api/reservations", new ReservationRequest(999999L, room.id(), checkIn, checkOut, false), String.class);
        assertThat(unknownGuest.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        GuestResponse guest = createGuest("Heitor Alves E2E");
        ResponseEntity<String> unknownRoom = restTemplate.postForEntity(
                "/api/reservations", new ReservationRequest(guest.id(), 999999L, checkIn, checkOut, false), String.class);
        assertThat(unknownRoom.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void checkIn_beforeStandardTimeRequiresAttendantConfirmation() {
        GuestResponse guest = createGuest("Igor Barbosa E2E");
        RoomResponse room = createRoomWithPrices("120.00", "150.00");
        ReservationResponse reservation = createReservation(guest.id(), room.id(), false);
        mutableClock().setNow(LocalDateTime.of(2026, 8, 3, 9, 0)); // Monday, before 2pm

        ResponseEntity<String> withoutConfirmation = restTemplate.postForEntity(
                "/api/reservations/" + reservation.id() + "/check-in", null, String.class);
        assertThat(withoutConfirmation.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ResponseEntity<ReservationResponse> withConfirmation = restTemplate.postForEntity(
                "/api/reservations/" + reservation.id() + "/check-in",
                new CheckInRequest(true),
                ReservationResponse.class);
        assertThat(withConfirmation.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(withConfirmation.getBody().actualCheckIn()).isEqualTo(LocalDateTime.of(2026, 8, 3, 9, 0));
    }

    @Test
    void checkIn_blockedWhenRoomIsNotAvailable() {
        GuestResponse guest = createGuest("Julia Ramos E2E");
        RoomResponse room = createRoomWithPrices("120.00", "150.00");
        ReservationResponse reservation = createReservation(guest.id(), room.id(), false);
        mutableClock().setNow(LocalDateTime.of(2026, 8, 3, 15, 0));

        RequestEntity<RoomStatusRequest> dirtyRoom = RequestEntity.patch("/api/rooms/" + room.id() + "/status")
                .body(new RoomStatusRequest(RoomStatus.DIRTY));
        assertThat(restTemplate.exchange(dirtyRoom, RoomResponse.class).getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> checkIn = restTemplate.postForEntity(
                "/api/reservations/" + reservation.id() + "/check-in", null, String.class);

        assertThat(checkIn.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void checkOut_onTime_hasNoLateFeeAndReturnsBreakdown() {
        GuestResponse guest = createGuest("Karina Dias E2E");
        RoomResponse room = createRoomWithPrices("120.00", "150.00");
        ReservationResponse reservation = createReservation(guest.id(), room.id(), true);
        mutableClock().setNow(LocalDateTime.of(2026, 8, 3, 14, 0)); // Monday 2pm, check-in
        assertThat(restTemplate
                        .postForEntity(
                                "/api/reservations/" + reservation.id() + "/check-in",
                                null,
                                ReservationResponse.class)
                        .getStatusCode())
                .isEqualTo(HttpStatus.OK);

        mutableClock().setNow(LocalDateTime.of(2026, 8, 4, 11, 0)); // Tuesday 11am, before noon
        ResponseEntity<CheckOutResponse> checkOut = restTemplate.postForEntity(
                "/api/reservations/" + reservation.id() + "/check-out", null, CheckOutResponse.class);

        assertThat(checkOut.getStatusCode()).isEqualTo(HttpStatus.OK);
        CheckOutResponse body = checkOut.getBody();
        assertThat(body.dailyRateTotal()).isEqualByComparingTo("120.00");
        assertThat(body.parkingFeeTotal()).isEqualByComparingTo("15.00");
        assertThat(body.lateCheckOutFee()).isEqualByComparingTo("0");
        assertThat(body.total()).isEqualByComparingTo("135.00");
    }

    @Test
    void checkOut_afterNoon_appliesLateFee() {
        GuestResponse guest = createGuest("Leandro Costa E2E");
        RoomResponse room = createRoomWithPrices("120.00", "150.00");
        ReservationResponse reservation = createReservation(guest.id(), room.id(), true);
        mutableClock().setNow(LocalDateTime.of(2026, 8, 3, 14, 0)); // Monday 2pm, check-in
        assertThat(restTemplate
                        .postForEntity(
                                "/api/reservations/" + reservation.id() + "/check-in",
                                null,
                                ReservationResponse.class)
                        .getStatusCode())
                .isEqualTo(HttpStatus.OK);

        mutableClock().setNow(LocalDateTime.of(2026, 8, 4, 13, 0)); // Tuesday 1pm, after noon -> late
        ResponseEntity<CheckOutResponse> checkOut = restTemplate.postForEntity(
                "/api/reservations/" + reservation.id() + "/check-out", null, CheckOutResponse.class);

        assertThat(checkOut.getStatusCode()).isEqualTo(HttpStatus.OK);
        CheckOutResponse body = checkOut.getBody();
        assertThat(body.dailyRateTotal()).isEqualByComparingTo("120.00");
        assertThat(body.parkingFeeTotal()).isEqualByComparingTo("15.00");
        assertThat(body.lateCheckOutFee()).isEqualByComparingTo("60.00"); // 50% of Monday's price
        assertThat(body.total()).isEqualByComparingTo("195.00");
    }

    @Test
    void checkOut_returnsConflictWhenNotCheckedInYet() {
        GuestResponse guest = createGuest("Mariana Fonte E2E");
        RoomResponse room = createRoomWithPrices("120.00", "150.00");
        ReservationResponse reservation = createReservation(guest.id(), room.id(), false);
        mutableClock().setNow(LocalDateTime.of(2026, 8, 4, 11, 0));

        ResponseEntity<String> checkOut = restTemplate.postForEntity(
                "/api/reservations/" + reservation.id() + "/check-out", null, String.class);

        assertThat(checkOut.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
