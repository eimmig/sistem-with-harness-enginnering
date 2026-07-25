package com.projetosenior.gestaohospedes.reservation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByActualCheckInIsNotNullAndActualCheckOutIsNull();

    List<Reservation> findByActualCheckInIsNull();
}
