package com.shopquanao.bejava.dto.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface VoucherListProjection {

    Integer getVoucherId();

    String getVoucherCode();

    Integer getDiscountType();

    BigDecimal getDiscountValue();

    Integer getUsageLimit();

    Integer getUsedCount();

    LocalDateTime getExpiryDate();

    Integer getStatus();

}
