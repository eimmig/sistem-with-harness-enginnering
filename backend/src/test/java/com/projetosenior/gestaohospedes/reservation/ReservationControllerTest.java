package com.projetosenior.gestaohospedes.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projetosenior.gestaohospedes.guest.Guest;
import com.projetosenior.gestaohospedes.guest.GuestRepository;
import com.projetosenior.gestaohospedes.room.Room;
import com.projetosenior.gestaohospedes.room.RoomRepository;
import com.projetosenior.gestaohospedes.room.RoomStatus;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategory;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    @MockitoBean
    private Clock clock;

    private Guest guest() {
        return new Guest(1L, "Maria Silva", "12345678900", "11999998888");
    }

    private Room room(RoomStatus status) {
        return new Room(1L, "101", new RoomCategory(1L, "Standard"), status);
    }

    private void fixClockAt(LocalDateTime dateTime) {
        ZoneId zone = ZoneId.systemDefault();
        when(clock.instant()).thenReturn(dateTime.atZone(zone).toInstant());
        when(clock.getZone()).thenReturn(zone);
    }

    @Test
    void createReservation() throws Exception {
        Guest guest = guest();
        Room room = room(RoomStatus.AVAILABLE);
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

    @Test
    void checkInValid() throws Exception {
        Guest guest = guest();
        Room room = room(RoomStatus.AVAILABLE);
        LocalDateTime expectedCheckIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime expectedCheckOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        LocalDateTime now = LocalDateTime.of(2026, 8, 3, 15, 0);
        Reservation existing = new Reservation(1L, guest, room, expectedCheckIn, expectedCheckOut, false);
        Reservation checkedIn = new Reservation(1L, guest, room, expectedCheckIn, expectedCheckOut, false);
        checkedIn.setActualCheckIn(now);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(checkedIn);
        fixClockAt(now);

        mockMvc.perform(post("/api/reservations/1/check-in"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actualCheckIn").value("2026-08-03T15:00:00"));
    }

    @Test
    void checkInBefore2pm() throws Exception {
        Guest guest = guest();
        Room room = room(RoomStatus.AVAILABLE);
        LocalDateTime expectedCheckIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime expectedCheckOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        LocalDateTime now = LocalDateTime.of(2026, 8, 3, 9, 0);
        Reservation existing = new Reservation(1L, guest, room, expectedCheckIn, expectedCheckOut, false);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));
        fixClockAt(now);

        mockMvc.perform(post("/api/reservations/1/check-in"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkInBefore2pmWithConfirmationSucceeds() throws Exception {
        Guest guest = guest();
        Room room = room(RoomStatus.AVAILABLE);
        LocalDateTime expectedCheckIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime expectedCheckOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        LocalDateTime now = LocalDateTime.of(2026, 8, 3, 9, 0);
        Reservation existing = new Reservation(1L, guest, room, expectedCheckIn, expectedCheckOut, false);
        Reservation checkedIn = new Reservation(1L, guest, room, expectedCheckIn, expectedCheckOut, false);
        checkedIn.setActualCheckIn(now);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(checkedIn);
        fixClockAt(now);

        mockMvc.perform(post("/api/reservations/1/check-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CheckInRequest(true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actualCheckIn").value("2026-08-03T09:00:00"));
    }

    @Test
    void checkInRoomUnavailable() throws Exception {
        Guest guest = guest();
        Room room = room(RoomStatus.OCCUPIED);
        LocalDateTime expectedCheckIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime expectedCheckOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        Reservation existing = new Reservation(1L, guest, room, expectedCheckIn, expectedCheckOut, false);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));

        mockMvc.perform(post("/api/reservations/1/check-in"))
                .andExpect(status().isConflict());
    }

    @Test
    void checkInAlreadyDoneIsRejected() throws Exception {
        Guest guest = guest();
        Room room = room(RoomStatus.OCCUPIED);
        LocalDateTime expectedCheckIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime expectedCheckOut = LocalDateTime.of(2026, 8, 4, 12, 0);
        Reservation existing = new Reservation(1L, guest, room, expectedCheckIn, expectedCheckOut, false);
        existing.setActualCheckIn(LocalDateTime.of(2026, 8, 3, 15, 0));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));

        mockMvc.perform(post("/api/reservations/1/check-in"))
                .andExpect(status().isConflict());
    }

    @Test
    void checkInReservationNotFound() throws Exception {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/reservations/99/check-in"))
                .andExpect(status().isNotFound());
    }
}
