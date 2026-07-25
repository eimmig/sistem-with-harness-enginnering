package com.projetosenior.gestaohospedes.room;

import com.projetosenior.gestaohospedes.roomcategory.RoomCategory;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategoryRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomCategoryRepository roomCategoryRepository;

    @Test
    void persistsRoomWithCategoryAndDefaultStatus() {
        RoomCategory category = roomCategoryRepository.save(new RoomCategory(null, "Standard"));
        Room room = new Room(null, "101", category, RoomStatus.AVAILABLE);

        Room saved = roomRepository.save(room);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(RoomStatus.AVAILABLE);
        assertThat(saved.getRoomCategory().getName()).isEqualTo("Standard");
    }

    @Test
    void findsRoomByIdAndReflectsUpdatedStatus() {
        RoomCategory category = roomCategoryRepository.save(new RoomCategory(null, "Luxo"));
        Room saved = roomRepository.save(new Room(null, "205", category, RoomStatus.AVAILABLE));

        saved.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(saved);

        Optional<Room> found = roomRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(RoomStatus.OCCUPIED);
        assertThat(found.get().getNumber()).isEqualTo("205");
    }
}
