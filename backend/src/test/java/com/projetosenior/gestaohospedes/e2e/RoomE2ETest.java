package com.projetosenior.gestaohospedes.e2e;

import com.projetosenior.gestaohospedes.room.RoomRequest;
import com.projetosenior.gestaohospedes.room.RoomResponse;
import com.projetosenior.gestaohospedes.room.RoomStatus;
import com.projetosenior.gestaohospedes.room.RoomStatusRequest;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategoryRequest;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategoryResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end tests for the Room domain: real Spring Boot server (RANDOM_PORT) over real HTTP
 * (TestRestTemplate) against a real Postgres instance (Testcontainers) -- no MockMvc, no H2, no
 * mocked repositories. Covers Room's own cadastro/status lifecycle (D-12, D-17 em DECISIONS.md).
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class RoomE2ETest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

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

    @Test
    void createRoom_persistsInRealDatabaseAsAvailable() {
        RoomCategoryResponse category = createRoomCategory("Standard E2E Room " + System.nanoTime());

        RoomResponse room = createRoom("E2E-" + System.nanoTime(), category.id());

        assertThat(room.id()).isNotNull();
        assertThat(room.status()).isEqualTo(RoomStatus.AVAILABLE);
        assertThat(room.category().id()).isEqualTo(category.id());
    }

    @Test
    void createRoom_returnsNotFoundForUnknownCategory() {
        ResponseEntity<String> response =
                restTemplate.postForEntity("/api/rooms", new RoomRequest("E2E-404", 999999L), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void listRooms_includesCreatedRoom() {
        RoomCategoryResponse category = createRoomCategory("Standard E2E List " + System.nanoTime());
        RoomResponse room = createRoom("E2E-" + System.nanoTime(), category.id());

        ResponseEntity<RoomResponse[]> response = restTemplate.getForEntity("/api/rooms", RoomResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(List.of(response.getBody())).extracting(RoomResponse::id).contains(room.id());
    }

    @Test
    void updateStatus_persistsNewStatusAndIsReflectedOnRefetch() {
        RoomCategoryResponse category = createRoomCategory("Standard E2E Status " + System.nanoTime());
        RoomResponse room = createRoom("E2E-" + System.nanoTime(), category.id());

        RequestEntity<RoomStatusRequest> request = RequestEntity.patch("/api/rooms/" + room.id() + "/status")
                .body(new RoomStatusRequest(RoomStatus.DIRTY));
        ResponseEntity<RoomResponse> response = restTemplate.exchange(request, RoomResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().status()).isEqualTo(RoomStatus.DIRTY);

        ResponseEntity<RoomResponse[]> listAfterUpdate = restTemplate.getForEntity("/api/rooms", RoomResponse[].class);
        RoomResponse persisted = List.of(listAfterUpdate.getBody()).stream()
                .filter(r -> r.id().equals(room.id()))
                .findFirst()
                .orElseThrow();
        assertThat(persisted.status()).isEqualTo(RoomStatus.DIRTY);
    }

    @Test
    void updateStatus_returnsNotFoundForUnknownRoom() {
        RequestEntity<RoomStatusRequest> request = RequestEntity.patch("/api/rooms/999999/status")
                .body(new RoomStatusRequest(RoomStatus.OCCUPIED));

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
