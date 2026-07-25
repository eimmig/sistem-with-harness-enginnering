package com.projetosenior.gestaohospedes.reservation;

import com.projetosenior.gestaohospedes.guest.Guest;
import com.projetosenior.gestaohospedes.guest.GuestRepository;
import com.projetosenior.gestaohospedes.room.Room;
import com.projetosenior.gestaohospedes.room.RoomRepository;
import com.projetosenior.gestaohospedes.room.RoomStatus;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategory;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategoryRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomCategoryRepository roomCategoryRepository;

    @Test
    void persistsReservationLinkedToGuestAndRoom() {
        Guest guest = guestRepository.save(new Guest(null, "Maria Silva", "12345678900", "11999998888"));
        RoomCategory category = roomCategoryRepository.save(new RoomCategory(null, "Standard"));
        Room room = roomRepository.save(new Room(null, "101", category, RoomStatus.AVAILABLE));
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 4, 12, 0);

        Reservation saved =
                reservationRepository.save(new Reservation(null, guest, room, checkIn, checkOut, true));

        assertThat(saved.getId()).isNotNull();

        Optional<Reservation> found = reservationRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getGuest().getName()).isEqualTo("Maria Silva");
        assertThat(found.get().getRoom().getNumber()).isEqualTo("101");
        assertThat(found.get().getExpectedCheckIn()).isEqualTo(checkIn);
        assertThat(found.get().getExpectedCheckOut()).isEqualTo(checkOut);
        assertThat(found.get().isParkingRequested()).isTrue();
    }
}
