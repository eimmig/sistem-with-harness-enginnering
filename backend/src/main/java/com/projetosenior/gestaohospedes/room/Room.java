package com.projetosenior.gestaohospedes.room;

import com.projetosenior.gestaohospedes.roomcategory.RoomCategory;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;

    @ManyToOne
    @JoinColumn(name = "room_category_id", nullable = false)
    private RoomCategory roomCategory;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    public Room(Long id, String number, RoomCategory roomCategory, RoomStatus status) {
        this.id = id;
        this.number = number;
        this.roomCategory = roomCategory;
        this.status = status;
    }
}
