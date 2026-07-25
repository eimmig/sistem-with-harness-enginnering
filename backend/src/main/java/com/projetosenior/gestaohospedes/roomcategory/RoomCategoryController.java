package com.projetosenior.gestaohospedes.roomcategory;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room-categories")
public class RoomCategoryController {

    private final RoomCategoryRepository roomCategoryRepository;

    public RoomCategoryController(RoomCategoryRepository roomCategoryRepository) {
        this.roomCategoryRepository = roomCategoryRepository;
    }

    @PostMapping
    public ResponseEntity<RoomCategoryResponse> create(@Valid @RequestBody RoomCategoryRequest request) {
        RoomCategory roomCategory = new RoomCategory(null, request.name());
        RoomCategory saved = roomCategoryRepository.save(roomCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(RoomCategoryResponse.from(saved));
    }
}
