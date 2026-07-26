package com.projetosenior.gestaohospedes.e2e;

import com.projetosenior.gestaohospedes.roomcategory.RoomCategoryPricesRequest;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategoryRequest;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategoryResponse;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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
 * End-to-end tests for the RoomCategory domain: real Spring Boot server (RANDOM_PORT) over real
 * HTTP (TestRestTemplate) against a real Postgres instance (Testcontainers) -- no MockMvc, no H2,
 * no mocked repositories. Covers restriction #4 (preco configuravel por categoria/dia da semana).
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class RoomCategoryE2ETest {

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

    private Map<DayOfWeek, BigDecimal> weekPrices(String weekdayPrice, String weekendPrice) {
        Map<DayOfWeek, BigDecimal> prices = new EnumMap<>(DayOfWeek.class);
        prices.put(DayOfWeek.MONDAY, new BigDecimal(weekdayPrice));
        prices.put(DayOfWeek.TUESDAY, new BigDecimal(weekdayPrice));
        prices.put(DayOfWeek.WEDNESDAY, new BigDecimal(weekdayPrice));
        prices.put(DayOfWeek.THURSDAY, new BigDecimal(weekdayPrice));
        prices.put(DayOfWeek.FRIDAY, new BigDecimal(weekdayPrice));
        prices.put(DayOfWeek.SATURDAY, new BigDecimal(weekendPrice));
        prices.put(DayOfWeek.SUNDAY, new BigDecimal(weekendPrice));
        return prices;
    }

    @Test
    void createRoomCategory_persistsInRealDatabaseAndReturnsCreated() {
        RoomCategoryResponse created = createRoomCategory("Standard E2E " + System.nanoTime());

        assertThat(created.id()).isNotNull();
        assertThat(created.name()).startsWith("Standard E2E");
    }

    @Test
    void listRoomCategories_includesCreatedCategory() {
        RoomCategoryResponse created = createRoomCategory("Luxo E2E " + System.nanoTime());

        ResponseEntity<RoomCategoryResponse[]> response =
                restTemplate.getForEntity("/api/room-categories", RoomCategoryResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(List.of(response.getBody())).extracting(RoomCategoryResponse::id).contains(created.id());
    }

    @Test
    void updatePrices_persistsAllSevenDaysAndIsReflectedOnRefetch() {
        RoomCategoryResponse category = createRoomCategory("Standard Precos E2E " + System.nanoTime());
        Map<DayOfWeek, BigDecimal> prices = weekPrices("120.00", "150.00");

        RequestEntity<RoomCategoryPricesRequest> request = RequestEntity.put(
                        "/api/room-categories/" + category.id() + "/prices")
                .body(new RoomCategoryPricesRequest(prices));
        ResponseEntity<RoomCategoryResponse> response =
                restTemplate.exchange(request, RoomCategoryResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().prices()).containsEntry(DayOfWeek.MONDAY, new BigDecimal("120.00"));
        assertThat(response.getBody().prices()).containsEntry(DayOfWeek.SATURDAY, new BigDecimal("150.00"));

        ResponseEntity<RoomCategoryResponse[]> listAfterUpdate =
                restTemplate.getForEntity("/api/room-categories", RoomCategoryResponse[].class);
        RoomCategoryResponse persisted = List.of(listAfterUpdate.getBody()).stream()
                .filter(c -> c.id().equals(category.id()))
                .findFirst()
                .orElseThrow();
        assertThat(persisted.prices()).containsEntry(DayOfWeek.SUNDAY, new BigDecimal("150.00"));
    }

    @Test
    void updatePrices_rejectsIncompleteWeek() {
        RoomCategoryResponse category = createRoomCategory("Standard Incompleto E2E " + System.nanoTime());
        Map<DayOfWeek, BigDecimal> incompletePrices = new EnumMap<>(DayOfWeek.class);
        incompletePrices.put(DayOfWeek.MONDAY, new BigDecimal("120.00"));

        RequestEntity<RoomCategoryPricesRequest> request = RequestEntity.put(
                        "/api/room-categories/" + category.id() + "/prices")
                .body(new RoomCategoryPricesRequest(incompletePrices));
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updatePrices_returnsNotFoundForUnknownCategory() {
        RequestEntity<RoomCategoryPricesRequest> request = RequestEntity.put("/api/room-categories/999999/prices")
                .body(new RoomCategoryPricesRequest(weekPrices("120.00", "150.00")));

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
