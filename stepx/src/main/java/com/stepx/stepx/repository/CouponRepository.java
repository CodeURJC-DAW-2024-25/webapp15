package com.stepx.stepx.repository;


import com.stepx.stepx.model.Coupon;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.model.ShoeSizeStock;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {


}
