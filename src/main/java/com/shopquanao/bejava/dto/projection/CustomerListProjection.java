package com.shopquanao.bejava.dto.projection;

import java.time.LocalDateTime;

public interface CustomerListProjection {

    Integer getUserId();

    String getFullName();

    String getEmail();

    String getPhone();

    Integer getGender();

    LocalDateTime getCreatedAt();

    Integer getStatus();

}
