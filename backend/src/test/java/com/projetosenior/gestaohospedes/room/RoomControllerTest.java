package com.projetosenior.gestaohospedes.room;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategory;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategoryRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RoomRepository roomRepository;

    @MockitoBean
    private RoomCategoryRepository roomCategoryRepository;

    @Test
    void createsRoomAndReturnsCreated() throws Exception {
        RoomCategory category = new RoomCategory(1L, "Standard");
        Room saved = new Room(1L, "101", category, RoomStatus.AVAILABLE);
        when(roomCategoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(roomRepository.save(any(Room.class))).thenReturn(saved);

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoomRequest("101", 1L))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.number").value("101"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.category.id").value(1))
                .andExpect(jsonPath("$.category.name").value("Standard"));
    }

    @Test
    void rejectsBlankNumber() throws Exception {
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoomRequest("", 1L))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsNotFoundWhenCreatingRoomWithUnknownCategory() throws Exception {
        when(roomCategoryRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoomRequest("101", 99L))))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatesRoomStatus() throws Exception {
        RoomCategory category = new RoomCategory(1L, "Standard");
        Room existing = new Room(1L, "101", category, RoomStatus.AVAILABLE);
        Room saved = new Room(1L, "101", category, RoomStatus.DIRTY);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(roomRepository.save(any(Room.class))).thenReturn(saved);

        mockMvc.perform(patch("/api/rooms/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoomStatusRequest(RoomStatus.DIRTY))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DIRTY"));
    }

    @Test
    void returnsNotFoundWhenUpdatingStatusOfUnknownRoom() throws Exception {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/rooms/99/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoomStatusRequest(RoomStatus.OCCUPIED))))
                .andExpect(status().isNotFound());
    }
}
