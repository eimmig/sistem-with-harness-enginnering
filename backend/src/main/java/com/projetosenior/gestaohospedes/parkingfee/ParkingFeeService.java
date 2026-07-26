package com.projetosenior.gestaohospedes.parkingfee;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ParkingFeeService {

    private static final BigDecimal WEEKDAY_FEE = new BigDecimal("15.00");
    private static final BigDecimal WEEKEND_FEE = new BigDecimal("20.00");
    private static final Set<DayOfWeek> WEEKEND_DAYS = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    public BigDecimal calculate(boolean parkingRequested, LocalDateTime checkIn, LocalDateTime checkOut) {
        if (!parkingRequested) {
            return BigDecimal.ZERO;
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }

        LocalDate checkInDate = checkIn.toLocalDate();
        LocalDate checkOutDate = checkOut.toLocalDate();
        // Mesma regra de DailyRateService: check-out no mesmo dia do check-in cobra a taxa minima
        // de 1 dia (o dia do check-in) em vez de rejeitar.
        long nights = Math.max(1, ChronoUnit.DAYS.between(checkInDate, checkOutDate));

        BigDecimal total = BigDecimal.ZERO;
        for (long i = 0; i < nights; i++) {
            DayOfWeek dayOfWeek = checkInDate.plusDays(i).getDayOfWeek();
            total = total.add(WEEKEND_DAYS.contains(dayOfWeek) ? WEEKEND_FEE : WEEKDAY_FEE);
        }
        return total;
    }
}
