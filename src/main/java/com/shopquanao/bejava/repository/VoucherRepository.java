package com.shopquanao.bejava.repository;

import com.shopquanao.bejava.dto.projection.VoucherListProjection;
import com.shopquanao.bejava.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {

    List<VoucherListProjection> findAllBy();

    boolean existsByVoucherCode(String voucherCode);

    boolean existsByVoucherCodeAndVoucherIdNot(String voucherCode, Integer voucherId);

    @Query("select count(v) from Voucher v where v.status = 1 and v.expiryDate > :now")
    long countActive(@Param("now") LocalDateTime now);

    @Query("select count(v) from Voucher v where v.expiryDate < :now")
    long countExpired(@Param("now") LocalDateTime now);

    @Query("select count(v) from Voucher v where v.status = 1 and v.expiryDate between :now and :end")
    long countNearExpiry(@Param("now") LocalDateTime now, @Param("end") LocalDateTime end);
}
