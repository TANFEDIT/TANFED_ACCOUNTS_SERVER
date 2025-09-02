package com.tanfed.accounts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tanfed.accounts.entity.SundryDrCrTable;
@Repository
public interface SundryDrCrTableRepo extends JpaRepository<SundryDrCrTable, Long> {

	public SundryDrCrTable findByMonthAndSubHeadAndOfficeNameAndFormType(String month, String subHead, String officeName, String formType);
}
