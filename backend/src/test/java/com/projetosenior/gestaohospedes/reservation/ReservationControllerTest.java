package com.projetosenior.gestaohospedes.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testa so o mapeamento HTTP da controller (status code, corpo, delegacao para o service) --
 * as regras de negocio (D-40) sao cobertas por ReservationServiceTest.
 */
@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private ReservationService reservationService;

    private ReservationResponse reservationResponse(LocalDateTime actualCheckIn) {
        return new ReservationResponse(
                1L,
                new ReservationResponse.GuestSummary(1L, "Maria Silva"),
                new ReservationResponse.RoomSummary(1L, "101"),
                LocalDateTime.of(2026, 8, 3, 14, 0),
                LocalDateTime.of(2026, 8, 4, 12, 0),
                true,
                actualCheckIn);
    }

    @Test
    void createDelegatesToServiceAndReturnsCreated() throws Exception {
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        when(reservationService.create(any(ReservationRequest.class))).thenReturn(reservationResponse(null));

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReservationRequest(1L, 1L, checkIn, checkOut, true))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.guest.name").value("Maria Silva"))
                .andExpect(jsonPath("$.room.number").value("101"));
    }

    @Test
    void createPropagatesServiceErrorStatus() throws Exception {
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 3, 12, 0);
        when(reservationService.create(any(ReservationRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expected check-out must be after expected check-in"));

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReservationRequest(1L, 1L, checkIn, checkOut, false))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsMissingRequiredFieldsBeforeReachingService() throws Exception {
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReservationRequest(null, null, null, null, false))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listsReservationsPendingCheckIn() throws Exception {
        when(reservationService.pendingCheckIn()).thenReturn(List.of(reservationResponse(null)));

        mockMvc.perform(get("/api/reservations/pending-check-in"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].guest.name").value("Maria Silva"));
    }

    @Test
    void listsReservationsPendingCheckOut() throws Exception {
        when(reservationService.pendingCheckOut()).thenReturn(List.of(reservationResponse(LocalDateTime.of(2026, 8, 3, 14, 0))));

        mockMvc.perform(get("/api/reservations/pending-check-out"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].guest.name").value("Maria Silva"));
    }

    @Test
    void checkInDelegatesToServiceAndReturnsOk() throws Exception {
        LocalDateTime now = LocalDateTime.of(2026, 8, 3, 15, 0);
        when(reservationService.checkIn(eq(1L), any())).thenReturn(reservationResponse(now));

        mockMvc.perform(post("/api/reservations/1/check-in"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actualCheckIn").value("2026-08-03T15:00:00"));
    }

    @Test
    void checkInPropagatesServiceErrorStatus() throws Exception {
        when(reservationService.checkIn(eq(99L), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        mockMvc.perform(post("/api/reservations/99/check-in")).andExpect(status().isNotFound());
    }

    @Test
    void checkOutDelegatesToServiceAndReturnsOk() throws Exception {
        CheckOutResponse response = new CheckOutResponse(
                1L, new BigDecimal("120.00"), new BigDecimal("15.00"), BigDecimal.ZERO, new BigDecimal("135.00"),
                LocalDateTime.of(2026, 8, 4, 11, 0));
        when(reservationService.checkOut(1L)).thenReturn(response);

        mockMvc.perform(post("/api/reservations/1/check-out"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value(1))
                .andExpect(jsonPath("$.dailyRateTotal").value(120.0))
                .andExpect(jsonPath("$.parkingFeeTotal").value(15.0))
                .andExpect(jsonPath("$.lateCheckOutFee").value(0))
                .andExpect(jsonPath("$.total").value(135.0));
    }

    @Test
    void checkOutPropagatesServiceErrorStatus() throws Exception {
        when(reservationService.checkOut(anyLong()))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Reservation has not checked in yet"));

        mockMvc.perform(post("/api/reservations/1/check-out")).andExpect(status().isConflict());
    }
}
