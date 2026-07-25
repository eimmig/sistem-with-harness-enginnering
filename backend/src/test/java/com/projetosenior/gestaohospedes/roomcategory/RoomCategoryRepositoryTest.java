package com.projetosenior.gestaohospedes.roomcategory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoomCategoryRepositoryTest {

    @Autowired
    private RoomCategoryRepository roomCategoryRepository;

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
}
