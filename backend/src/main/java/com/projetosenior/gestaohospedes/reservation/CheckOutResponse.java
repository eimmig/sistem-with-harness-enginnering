package com.projetosenior.gestaohospedes.reservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CheckOutResponse(
        Long reservationId,
        BigDecimal dailyRateTotal,
        BigDecimal parkingFeeTotal,
        BigDecimal lateCheckOutFee,
        BigDecimal total,
        LocalDateTime actualCheckOut) {
}
