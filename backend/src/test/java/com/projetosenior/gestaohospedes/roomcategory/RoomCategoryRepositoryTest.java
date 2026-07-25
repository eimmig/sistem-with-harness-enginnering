package com.projetosenior.gestaohospedes.roomcategory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoomCategoryRepositoryTest {

    @Autowired
    private RoomCategoryRepository roomCategoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void persistsRoomCategoryAndGeneratesId() {
        RoomCategory roomCategory = new RoomCategory(null, "Luxo");

        RoomCategory saved = roomCategoryRepository.save(roomCategory);

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void findsRoomCategoryById() {
        RoomCategory saved = roomCategoryRepository.save(new RoomCategory(null, "Standard"));

        Optional<RoomCategory> found = roomCategoryRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Standard");
    }

    @Test
    void persistsPricesPerDayOfWeekAcrossReload() {
        RoomCategory roomCategory = new RoomCategory(null, "Standard");
        Map<DayOfWeek, BigDecimal> prices = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            prices.put(day, new BigDecimal("120.00"));
        }
        roomCategory.setPrices(prices);
        Long id = roomCategoryRepository.save(roomCategory).getId();

        entityManager.flush();
        entityManager.clear();

        Optional<RoomCategory> found = roomCategoryRepository.findById(id);

        assertThat(found).isPresent();
        assertThat(found.get().getPrices()).hasSize(7);
        assertThat(found.get().getPrices().get(DayOfWeek.MONDAY)).isEqualByComparingTo("120.00");
        assertThat(found.get().getPrices().get(DayOfWeek.SUNDAY)).isEqualByComparingTo("120.00");
    }
}
