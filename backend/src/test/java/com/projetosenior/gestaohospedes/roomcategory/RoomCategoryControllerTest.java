package com.projetosenior.gestaohospedes.roomcategory;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@WebMvcTest(RoomCategoryController.class)
class RoomCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RoomCategoryRepository roomCategoryRepository;

    @Test
    void createsRoomCategoryAndReturnsCreated() throws Exception {
        RoomCategory saved = new RoomCategory(1L, "Standard");
        when(roomCategoryRepository.save(any(RoomCategory.class))).thenReturn(saved);

        mockMvc.perform(post("/api/room-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoomCategoryRequest("Standard"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Standard"));
    }

    @Test
    void rejectsBlankName() throws Exception {
        mockMvc.perform(post("/api/room-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoomCategoryRequest(""))))
                .andExpect(status().isBadRequest());
    }
}
