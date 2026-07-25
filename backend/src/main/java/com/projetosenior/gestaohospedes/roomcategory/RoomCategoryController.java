package com.projetosenior.gestaohospedes.roomcategory;

import jakarta.validation.Valid;
import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.EnumSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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

    @PutMapping("/{id}/prices")
    public ResponseEntity<RoomCategoryResponse> updatePrices(
            @PathVariable Long id, @Valid @RequestBody RoomCategoryPricesRequest request) {
        if (!request.prices().keySet().equals(EnumSet.allOf(DayOfWeek.class))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be set for all 7 days of the week");
        }
        RoomCategory roomCategory = roomCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room category not found"));
        roomCategory.setPrices(new EnumMap<>(request.prices()));
        RoomCategory saved = roomCategoryRepository.save(roomCategory);
        return ResponseEntity.ok(RoomCategoryResponse.from(saved));
    }
}
