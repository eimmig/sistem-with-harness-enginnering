package com.projetosenior.gestaohospedes.reservation;

import com.projetosenior.gestaohospedes.guest.Guest;
import com.projetosenior.gestaohospedes.room.Room;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    private LocalDateTime expectedCheckIn;

    private LocalDateTime expectedCheckOut;

    private boolean parkingRequested;

    private LocalDateTime actualCheckIn;

    public Reservation(
            Long id,
            Guest guest,
            Room room,
            LocalDateTime expectedCheckIn,
            LocalDateTime expectedCheckOut,
            boolean parkingRequested) {
        this.id = id;
        this.guest = guest;
        this.room = room;
        this.expectedCheckIn = expectedCheckIn;
        this.expectedCheckOut = expectedCheckOut;
        this.parkingRequested = parkingRequested;
    }
}
