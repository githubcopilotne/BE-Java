package com.shopquanao.bejava.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoucherStatsResponse {
    private long total;
    private long active;
    private long nearExpiry;
    private long expired;
}
