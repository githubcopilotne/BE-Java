package com.shopquanao.bejava.repository;

import com.shopquanao.bejava.dto.projection.VoucherListProjection;
import com.shopquanao.bejava.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {

    List<VoucherListProjection> findAllBy();

    boolean existsByVoucherCode(String voucherCode);

    boolean existsByVoucherCodeAndVoucherIdNot(String voucherCode, Integer voucherId);
}
