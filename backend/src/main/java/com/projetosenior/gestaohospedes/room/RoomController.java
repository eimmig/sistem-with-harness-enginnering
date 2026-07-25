package com.projetosenior.gestaohospedes.room;

import com.projetosenior.gestaohospedes.roomcategory.RoomCategory;
import com.projetosenior.gestaohospedes.roomcategory.RoomCategoryRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomRepository roomRepository;
    private final RoomCategoryRepository roomCategoryRepository;

    public RoomController(RoomRepository roomRepository, RoomCategoryRepository roomCategoryRepository) {
        this.roomRepository = roomRepository;
        this.roomCategoryRepository = roomCategoryRepository;
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> list() {
        List<RoomResponse> rooms = roomRepository.findAll().stream().map(RoomResponse::from).toList();
        return ResponseEntity.ok(rooms);
    }

    @PostMapping
    public ResponseEntity<RoomResponse> create(@Valid @RequestBody RoomRequest request) {
        RoomCategory roomCategory = roomCategoryRepository.findById(request.roomCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room category not found"));
        Room room = new Room(null, request.number(), roomCategory, RoomStatus.AVAILABLE);
        Room saved = roomRepository.save(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(RoomResponse.from(saved));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RoomResponse> updateStatus(
            @PathVariable Long id, @Valid @RequestBody RoomStatusRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        room.setStatus(request.status());
        Room saved = roomRepository.save(room);
        return ResponseEntity.ok(RoomResponse.from(saved));
    }
}
