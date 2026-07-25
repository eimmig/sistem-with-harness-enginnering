package com.projetosenior.gestaohospedes.dailyrate;

import com.projetosenior.gestaohospedes.roomcategory.RoomCategory;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DailyRateServiceTest {

    private static final BigDecimal WEEKDAY_PRICE = new BigDecimal("120.00");
    private static final BigDecimal WEEKEND_PRICE = new BigDecimal("180.00");

    private final DailyRateService dailyRateService = new DailyRateService();

    private RoomCategory categoryWithPrices() {
        RoomCategory category = new RoomCategory(1L, "Standard");
        Map<DayOfWeek, BigDecimal> prices = new EnumMap<>(DayOfWeek.class);
        prices.put(DayOfWeek.MONDAY, WEEKDAY_PRICE);
        prices.put(DayOfWeek.TUESDAY, WEEKDAY_PRICE);
        prices.put(DayOfWeek.WEDNESDAY, WEEKDAY_PRICE);
        prices.put(DayOfWeek.THURSDAY, WEEKDAY_PRICE);
        prices.put(DayOfWeek.FRIDAY, WEEKEND_PRICE);
        prices.put(DayOfWeek.SATURDAY, WEEKEND_PRICE);
        prices.put(DayOfWeek.SUNDAY, WEEKEND_PRICE);
        category.setPrices(prices);
        return category;
    }

    @Test
    void calculatesOnlyWeekdayStay() {
        // Monday 2026-08-03 14:00 -> Wednesday 2026-08-05 12:00: 2 nights, Monday + Tuesday
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 5, 12, 0);

        BigDecimal total = dailyRateService.calculate(categoryWithPrices(), checkIn, checkOut);

        assertThat(total).isEqualByComparingTo(WEEKDAY_PRICE.multiply(BigDecimal.valueOf(2)));
    }

    @Test
    void calculatesOnlyWeekendStay() {
        // Friday 2026-08-07 14:00 -> Monday 2026-08-10 12:00: 3 nights, Friday + Saturday + Sunday (regra #3)
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 7, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 10, 12, 0);

        BigDecimal total = dailyRateService.calculate(categoryWithPrices(), checkIn, checkOut);

        assertThat(total).isEqualByComparingTo(WEEKEND_PRICE.multiply(BigDecimal.valueOf(3)));
    }

    @Test
    void calculatesStayCrossingWeekdayAndWeekend() {
        // Thursday 2026-08-06 14:00 -> Saturday 2026-08-08 12:00: 2 nights, Thursday (util) + Friday (fim de semana)
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 6, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 8, 12, 0);

        BigDecimal total = dailyRateService.calculate(categoryWithPrices(), checkIn, checkOut);

        assertThat(total).isEqualByComparingTo(WEEKDAY_PRICE.add(WEEKEND_PRICE));
    }

    @Test
    void rejectsCheckOutNotAfterCheckInDate() {
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 3, 18, 0);

        assertThatThrownBy(() -> dailyRateService.calculate(categoryWithPrices(), checkIn, checkOut))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void failsWhenRoomCategoryHasNoPriceForADay() {
        RoomCategory category = new RoomCategory(1L, "Standard");
        category.setPrices(new EnumMap<>(DayOfWeek.class));
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 4, 12, 0);

        assertThatThrownBy(() -> dailyRateService.calculate(category, checkIn, checkOut))
                .isInstanceOf(IllegalStateException.class);
    }
}
