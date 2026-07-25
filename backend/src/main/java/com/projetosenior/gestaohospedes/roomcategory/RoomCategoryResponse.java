package com.projetosenior.gestaohospedes.roomcategory;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.Map;

public record RoomCategoryResponse(Long id, String name, Map<DayOfWeek, BigDecimal> prices) {

    public static RoomCategoryResponse from(RoomCategory roomCategory) {
        return new RoomCategoryResponse(roomCategory.getId(), roomCategory.getName(), roomCategory.getPrices());
    }
}
