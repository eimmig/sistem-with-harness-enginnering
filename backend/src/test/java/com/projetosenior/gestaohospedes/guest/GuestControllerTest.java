package com.projetosenior.gestaohospedes.guest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetosenior.gestaohospedes.reservation.Reservation;
import com.projetosenior.gestaohospedes.reservation.ReservationRepository;
import com.projetosenior.gestaohospedes.room.Room;
import com.projetosenior.gestaohospedes.room.RoomStatus;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategory;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GuestController.class)
class GuestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private GuestRepository guestRepository;

    @MockitoBean
    private ReservationRepository reservationRepository;

    @Test
    void createsGuestAndReturnsCreated() throws Exception {
        Guest saved = new Guest(1L, "Maria Silva", "12345678900", "11999998888");
        when(guestRepository.save(any(Guest.class))).thenReturn(saved);

        mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new GuestRequest("Maria Silva", "12345678900", "11999998888"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Maria Silva"))
                .andExpect(jsonPath("$.document").value("12345678900"))
                .andExpect(jsonPath("$.phone").value("11999998888"));
    }

    @Test
    void rejectsBlankName() throws Exception {
        mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new GuestRequest("", "12345678900", "11999998888"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsBlankDocument() throws Exception {
        mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new GuestRequest("Maria Silva", "", "11999998888"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsBlankPhone() throws Exception {
        mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new GuestRequest("Maria Silva", "12345678900", ""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchByFilter() throws Exception {
        Guest guest = new Guest(1L, "Maria Silva", "12345678900", "11999998888");
        when(guestRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Guest>>any())).thenReturn(List.of(guest));

        mockMvc.perform(get("/api/guests").param("name", "Maria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Maria Silva"))
                .andExpect(jsonPath("$[0].document").value("12345678900"))
                .andExpect(jsonPath("$[0].phone").value("11999998888"));
    }

    @Test
    void searchWithoutFiltersReturnsAllGuests() throws Exception {
        Guest guest = new Guest(1L, "Maria Silva", "12345678900", "11999998888");
        when(guestRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Guest>>any())).thenReturn(List.of(guest));

        mockMvc.perform(get("/api/guests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void searchWithNoMatchesReturnsEmptyList() throws Exception {
        when(guestRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Guest>>any())).thenReturn(List.of());

        mockMvc.perform(get("/api/guests").param("document", "00000000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private Room room() {
        return new Room(1L, "101", new RoomCategory(1L, "Standard"), RoomStatus.OCCUPIED);
    }

    @Test
    void guestsInHotel() throws Exception {
        Guest guest = new Guest(1L, "Maria Silva", "12345678900", "11999998888");
        Reservation reservation = new Reservation(
                1L, guest, room(), LocalDateTime.of(2026, 8, 3, 14, 0), LocalDateTime.of(2026, 8, 4, 12, 0), false);
        reservation.setActualCheckIn(LocalDateTime.of(2026, 8, 3, 14, 0));
        when(reservationRepository.findByActualCheckInIsNotNullAndActualCheckOutIsNull())
                .thenReturn(List.of(reservation));

        mockMvc.perform(get("/api/guests/in-hotel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Maria Silva"));
    }

    @Test
    void guestsInHotelReturnsEmptyListWhenNoOneIsCheckedIn() throws Exception {
        when(reservationRepository.findByActualCheckInIsNotNullAndActualCheckOutIsNull()).thenReturn(List.of());

        mockMvc.perform(get("/api/guests/in-hotel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
