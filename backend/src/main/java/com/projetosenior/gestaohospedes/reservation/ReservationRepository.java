package com.projetosenior.gestaohospedes.reservation;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByActualCheckInIsNotNullAndActualCheckOutIsNull();

    List<Reservation> findByActualCheckInIsNull();

    /** Reservas do mesmo quarto, ainda sem check-out, cujas datas previstas se sobrepõem ao período informado (D-41). */
    @Query("""
            SELECT r FROM Reservation r
            WHERE r.room.id = :roomId
              AND r.actualCheckOut IS NULL
              AND r.expectedCheckIn < :expectedCheckOut
              AND r.expectedCheckOut > :expectedCheckIn
            """)
    List<Reservation> findOverlappingActiveReservations(
            @Param("roomId") Long roomId,
            @Param("expectedCheckIn") LocalDateTime expectedCheckIn,
            @Param("expectedCheckOut") LocalDateTime expectedCheckOut);
}
