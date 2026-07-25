package com.projetosenior.gestaohospedes.guest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class GuestRepositoryTest {

    @Autowired
    private GuestRepository guestRepository;

    @Test
    void persistsGuestAndGeneratesId() {
        Guest guest = new Guest(null, "Joao Souza", "98765432100", "11988887777");

        Guest saved = guestRepository.save(guest);

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void findsGuestById() {
        Guest saved = guestRepository.save(new Guest(null, "Ana Lima", "11122233344", "11977776666"));

        Optional<Guest> found = guestRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Ana Lima");
        assertThat(found.get().getDocument()).isEqualTo("11122233344");
        assertThat(found.get().getPhone()).isEqualTo("11977776666");
    }
}
