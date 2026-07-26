package com.projetosenior.gestaohospedes.e2e;

import com.projetosenior.gestaohospedes.guest.GuestRequest;
import com.projetosenior.gestaohospedes.guest.GuestResponse;
import com.projetosenior.gestaohospedes.reservation.CheckInRequest;
import com.projetosenior.gestaohospedes.reservation.ReservationRequest;
import com.projetosenior.gestaohospedes.reservation.ReservationResponse;
import com.projetosenior.gestaohospedes.room.RoomRequest;
import com.projetosenior.gestaohospedes.room.RoomResponse;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategoryRequest;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategoryResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end tests for the Guest domain: real Spring Boot server (RANDOM_PORT) over real HTTP
 * (TestRestTemplate) against a real Postgres instance (Testcontainers) -- no MockMvc, no H2,
 * no mocked repositories. Covers restrictions #9 (busca), #10/#11 (listagens) and #12 (cadastro).
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class GuestE2ETest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    private GuestResponse createGuest(String name, String document, String phone) {
        ResponseEntity<GuestResponse> response =
                restTemplate.postForEntity("/api/guests", new GuestRequest(name, document, phone), GuestResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody();
    }

    private RoomCategoryResponse createRoomCategory(String name) {
        ResponseEntity<RoomCategoryResponse> response = restTemplate.postForEntity(
                "/api/room-categories", new RoomCategoryRequest(name), RoomCategoryResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody();
    }

    private RoomResponse createRoom(String number, Long roomCategoryId) {
        ResponseEntity<RoomResponse> response =
                restTemplate.postForEntity("/api/rooms", new RoomRequest(number, roomCategoryId), RoomResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody();
    }

    private ReservationResponse createReservation(Long guestId, Long roomId) {
        LocalDateTime checkIn = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime checkOut = checkIn.plusDays(1).withHour(12).withMinute(0);
        ResponseEntity<ReservationResponse> response = restTemplate.postForEntity(
                "/api/reservations",
                new ReservationRequest(guestId, roomId, checkIn, checkOut, false),
                ReservationResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody();
    }

    @Test
    void createGuest_persistsInRealDatabaseAndReturnsCreated() {
        GuestResponse created = createGuest("Ana Costa E2E", "11122233396", "11955554444");

        assertThat(created.id()).isNotNull();
        assertThat(created.name()).isEqualTo("Ana Costa E2E");
        assertThat(created.document()).isEqualTo("11122233396");
        assertThat(created.phone()).isEqualTo("11955554444");
    }

    @Test
    void searchGuests_byNameDocumentAndPhone() {
        createGuest("Bruno Alves E2E", "22233344497", "21988887777");
        createGuest("Carla Souza E2E", "33344455598", "21977776666");

        ResponseEntity<GuestResponse[]> byName =
                restTemplate.getForEntity("/api/guests?name=Bruno Alves E2E", GuestResponse[].class);
        assertThat(byName.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(byName.getBody()).extracting(GuestResponse::name).contains("Bruno Alves E2E");

        ResponseEntity<GuestResponse[]> byDocument =
                restTemplate.getForEntity("/api/guests?document=33344455598", GuestResponse[].class);
        assertThat(byDocument.getBody()).extracting(GuestResponse::name).containsExactly("Carla Souza E2E");

        ResponseEntity<GuestResponse[]> byPhone =
                restTemplate.getForEntity("/api/guests?phone=21977776666", GuestResponse[].class);
        assertThat(byPhone.getBody()).extracting(GuestResponse::name).containsExactly("Carla Souza E2E");
    }

    @Test
    void guestsInHotel_listsOnlyGuestsWithCheckInAndNoCheckOut() {
        GuestResponse guest = createGuest("Daniela Lima E2E", "44455566699", "31966665555");
        RoomCategoryResponse category = createRoomCategory("Standard E2E Guest " + System.nanoTime());
        RoomResponse room = createRoom("E2E-" + System.nanoTime(), category.id());
        ReservationResponse reservation = createReservation(guest.id(), room.id());

        ResponseEntity<Void> checkIn = restTemplate.postForEntity(
                "/api/reservations/" + reservation.id() + "/check-in",
                new CheckInRequest(true),
                Void.class);
        assertThat(checkIn.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<GuestResponse[]> inHotel = restTemplate.getForEntity("/api/guests/in-hotel", GuestResponse[].class);
        assertThat(inHotel.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(List.of(inHotel.getBody())).extracting(GuestResponse::id).contains(guest.id());
    }

    @Test
    void guestsWithoutCheckIn_listsGuestsWithReservationButNoCheckIn() {
        GuestResponse guest = createGuest("Eduardo Nunes E2E", "55566677790", "41955554444");
        RoomCategoryResponse category = createRoomCategory("Standard E2E Guest2 " + System.nanoTime());
        RoomResponse room = createRoom("E2E-" + System.nanoTime(), category.id());
        createReservation(guest.id(), room.id());

        ResponseEntity<GuestResponse[]> withoutCheckIn =
                restTemplate.getForEntity("/api/guests/without-check-in", GuestResponse[].class);
        assertThat(withoutCheckIn.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(List.of(withoutCheckIn.getBody())).extracting(GuestResponse::id).contains(guest.id());
    }
}
