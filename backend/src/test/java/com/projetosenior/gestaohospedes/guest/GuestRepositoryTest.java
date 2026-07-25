package com.projetosenior.gestaohospedes.guest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
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

    @Test
    void searchByFilterMatchesPartialAndCaseInsensitiveName() {
        guestRepository.save(new Guest(null, "Maria Silva", "12345678900", "11999998888"));
        guestRepository.save(new Guest(null, "Joao Souza", "98765432100", "11988887777"));

        List<Guest> result = guestRepository.findAll(GuestSpecifications.nameContains("maria"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Maria Silva");
    }

    @Test
    void searchByFilterCombinesNameAndDocumentWithAnd() {
        guestRepository.save(new Guest(null, "Maria Silva", "12345678900", "11999998888"));
        guestRepository.save(new Guest(null, "Mariana Costa", "99988877766", "11955554444"));

        Specification<Guest> spec = GuestSpecifications.nameContains("maria")
                .and(GuestSpecifications.documentContains("123"));
        List<Guest> result = guestRepository.findAll(spec);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Maria Silva");
    }

    @Test
    void searchByFilterReturnsEmptyWhenNoGuestMatches() {
        guestRepository.save(new Guest(null, "Maria Silva", "12345678900", "11999998888"));

        List<Guest> result = guestRepository.findAll(GuestSpecifications.phoneContains("00000"));

        assertThat(result).isEmpty();
    }
}
