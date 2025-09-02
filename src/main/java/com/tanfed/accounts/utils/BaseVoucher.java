package com.tanfed.accounts.utils;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public abstract class BaseVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String voucherStatus;

    @ElementCollection
    private List<Long> empId = new ArrayList<>();

    @ElementCollection
    private List<String> designation = new ArrayList<>();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVoucherStatus() {
        return voucherStatus;
    }

    public void setVoucherStatus(String voucherStatus) {
        this.voucherStatus = voucherStatus;
    }

    public List<Long> getEmpId() {
        return empId;
    }

    public void setEmpId(List<Long> empId) {
        this.empId = empId;
    }

    public List<String> getDesignation() {
        return designation;
    }

    public void setDesignation(List<String> designation) {
        this.designation = designation;
    }
}
