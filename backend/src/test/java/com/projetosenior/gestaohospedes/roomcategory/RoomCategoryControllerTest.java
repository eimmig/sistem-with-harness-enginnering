package com.projetosenior.gestaohospedes.roomcategory;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    void updatePrices() throws Exception {
        RoomCategory existing = new RoomCategory(1L, "Standard");
        Map<DayOfWeek, BigDecimal> prices = allDaysPrices();
        RoomCategory saved = new RoomCategory(1L, "Standard");
        saved.setPrices(prices);
        when(roomCategoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(roomCategoryRepository.save(any(RoomCategory.class))).thenReturn(saved);

        mockMvc.perform(put("/api/room-categories/1/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoomCategoryPricesRequest(prices))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prices.MONDAY").value(120.0))
                .andExpect(jsonPath("$.prices.SATURDAY").value(150.0));
    }

    @Test
    void rejectsIncompleteWeekWhenUpdatingPrices() throws Exception {
        Map<DayOfWeek, BigDecimal> prices = allDaysPrices();
        prices.remove(DayOfWeek.SUNDAY);

        mockMvc.perform(put("/api/room-categories/1/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoomCategoryPricesRequest(prices))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsNonPositivePriceWhenUpdatingPrices() throws Exception {
        Map<DayOfWeek, BigDecimal> prices = allDaysPrices();
        prices.put(DayOfWeek.MONDAY, BigDecimal.ZERO);

        mockMvc.perform(put("/api/room-categories/1/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoomCategoryPricesRequest(prices))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsNotFoundWhenUpdatingPricesOfUnknownCategory() throws Exception {
        when(roomCategoryRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/room-categories/99/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoomCategoryPricesRequest(allDaysPrices()))))
                .andExpect(status().isNotFound());
    }

    @Test
    void listsAllRoomCategories() throws Exception {
        RoomCategory standard = new RoomCategory(1L, "Standard");
        RoomCategory luxo = new RoomCategory(2L, "Luxo");
        when(roomCategoryRepository.findAll()).thenReturn(List.of(standard, luxo));

        mockMvc.perform(get("/api/room-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Standard"))
                .andExpect(jsonPath("$[1].name").value("Luxo"));
    }

    private Map<DayOfWeek, BigDecimal> allDaysPrices() {
        Map<DayOfWeek, BigDecimal> prices = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            boolean weekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
            prices.put(day, weekend ? new BigDecimal("150.00") : new BigDecimal("120.00"));
        }
        return prices;
    }
}
