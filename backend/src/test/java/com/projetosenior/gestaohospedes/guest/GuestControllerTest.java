package com.projetosenior.gestaohospedes.guest;

import com.fasterxml.jackson.databind.ObjectMapper;
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
}
