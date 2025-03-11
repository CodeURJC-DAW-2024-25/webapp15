package com.stepx.stepx.repository;

import com.stepx.stepx.model.Coupon;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Query("SELECT c FROM Coupon c WHERE c.code = :code AND c.id = :id")
    Optional<Coupon> findByCodeAndId(@Param("code") String code, @Param("id") Long id);

}
