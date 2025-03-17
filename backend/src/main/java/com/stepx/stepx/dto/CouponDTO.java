
package com.stepx.stepx.dto;

import java.math.BigDecimal;

public class CouponDTO {

    private Long id;
    private String code;
    private BigDecimal discount;

    // Constructors
    public CouponDTO() {
    }

    public CouponDTO(Long id, String code, BigDecimal discount) {
        this.id = id;
        this.code = code;
        this.discount = discount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    // toString method
    @Override
    public String toString() {
        return "CouponDTO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", discount=" + discount +
                '}';
    }
}