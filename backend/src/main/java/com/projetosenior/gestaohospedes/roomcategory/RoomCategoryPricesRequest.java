package com.projetosenior.gestaohospedes.roomcategory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.Map;

public record RoomCategoryPricesRequest(
        @NotNull(message = "Prices are required")
        Map<DayOfWeek, @NotNull(message = "Price is required") @Positive(message = "Price must be positive") BigDecimal> prices) {
}
