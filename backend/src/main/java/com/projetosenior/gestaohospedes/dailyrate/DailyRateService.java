package com.projetosenior.gestaohospedes.dailyrate;

import com.projetosenior.gestaohospedes.roomcategory.RoomCategory;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;

@Service
public class DailyRateService {

    public BigDecimal calculate(RoomCategory roomCategory, LocalDateTime checkIn, LocalDateTime checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }

        LocalDate checkInDate = checkIn.toLocalDate();
        LocalDate checkOutDate = checkOut.toLocalDate();
        // Check-out no mesmo dia do check-in (ex.: day-use, ou check-in/check-out reais feitos em
        // sequencia rapida) ainda cobra a diaria minima do dia do check-in, em vez de rejeitar.
        long nights = Math.max(1, ChronoUnit.DAYS.between(checkInDate, checkOutDate));

        BigDecimal total = BigDecimal.ZERO;
        for (long i = 0; i < nights; i++) {
            DayOfWeek dayOfWeek = checkInDate.plusDays(i).getDayOfWeek();
            BigDecimal price = roomCategory.getPrices().get(dayOfWeek);
            if (price == null) {
                throw new IllegalStateException(
                        "No price configured for " + dayOfWeek + " in room category " + roomCategory.getName());
            }
            total = total.add(price);
        }
        return total;
    }
}
