package com.projetosenior.gestaohospedes.parkingfee;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParkingFeeServiceTest {

    private static final BigDecimal WEEKDAY_FEE = new BigDecimal("15.00");
    private static final BigDecimal WEEKEND_FEE = new BigDecimal("20.00");

    private final ParkingFeeService parkingFeeService = new ParkingFeeService();

    @Test
    void calculatesOnlyWeekdayStay() {
        // Monday 2026-08-03 14:00 -> Wednesday 2026-08-05 12:00: 2 nights, Monday + Tuesday
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 5, 12, 0);

        BigDecimal total = parkingFeeService.calculate(true, checkIn, checkOut);

        assertThat(total).isEqualByComparingTo(WEEKDAY_FEE.multiply(BigDecimal.valueOf(2)));
    }

    @Test
    void calculatesOnlyWeekendStay() {
        // Saturday 2026-08-08 14:00 -> Monday 2026-08-10 12:00: 2 nights, Saturday + Sunday
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 8, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 10, 12, 0);

        BigDecimal total = parkingFeeService.calculate(true, checkIn, checkOut);

        assertThat(total).isEqualByComparingTo(WEEKEND_FEE.multiply(BigDecimal.valueOf(2)));
    }

    @Test
    void calculatesStayCrossingWeekdayAndWeekend() {
        // Friday 2026-08-07 14:00 -> Sunday 2026-08-09 12:00: 2 nights, Friday (util) + Saturday (fim de semana)
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 7, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 9, 12, 0);

        BigDecimal total = parkingFeeService.calculate(true, checkIn, checkOut);

        assertThat(total).isEqualByComparingTo(WEEKDAY_FEE.add(WEEKEND_FEE));
    }

    @Test
    void returnsZeroWhenParkingNotRequested() {
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 7, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 9, 12, 0);

        BigDecimal total = parkingFeeService.calculate(false, checkIn, checkOut);

        assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void rejectsCheckOutNotAfterCheckInDate() {
        LocalDateTime checkIn = LocalDateTime.of(2026, 8, 3, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2026, 8, 3, 18, 0);

        assertThatThrownBy(() -> parkingFeeService.calculate(true, checkIn, checkOut))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
