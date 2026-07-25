package com.projetosenior.gestaohospedes.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projetosenior.gestaohospedes.guest.Guest;
import com.projetosenior.gestaohospedes.guest.GuestRepository;
import com.projetosenior.gestaohospedes.room.Room;
import com.projetosenior.gestaohospedes.room.RoomRepository;
import com.projetosenior.gestaohospedes.room.RoomStatus;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategory;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private ReservationRepository reservationRepository;

    @MockitoBean
    private GuestRepository guestRepository;

    @MockitoBean
    private RoomRepository roomRepository;

    private Guest guest() {
        return new Guest(1L, "Maria Silva", "12345678900", "11999998888");
    }

    private Room room() {
        return new Room(1L, "101", new RoomCategory(1L, "Standard"), RoomStatus.AVAILABLE);
    }

    @Test
    void createReservation() throws Exception {
        Guest guest = guest();
        Room room = room();
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        Reservation saved = new Reservation(1L, guest, room, checkIn, checkOut, true);
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(saved);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReservationRequest(1L, 1L, checkIn, checkOut, true))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.guest.id").value(1))
                .andExpect(jsonPath("$.guest.name").value("Maria Silva"))
                .andExpect(jsonPath("$.room.id").value(1))
                .andExpect(jsonPath("$.room.number").value("101"))
                .andExpect(jsonPath("$.parkingRequested").value(true));
    }

    @Test
    void rejectsCheckOutNotAfterCheckIn() throws Exception {
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 3, 12, 0);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReservationRequest(1L, 1L, checkIn, checkOut, false))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsNotFoundWhenGuestDoesNotExist() throws Exception {
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        when(guestRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReservationRequest(99L, 1L, checkIn, checkOut, false))))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsNotFoundWhenRoomDoesNotExist() throws Exception {
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest()));
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReservationRequest(1L, 99L, checkIn, checkOut, false))))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsMissingRequiredFields() throws Exception {
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReservationRequest(null, null, null, null, false))))
                .andExpect(status().isBadRequest());
    }
}
