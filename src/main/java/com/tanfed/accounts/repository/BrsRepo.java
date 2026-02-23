package com.tanfed.accounts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tanfed.accounts.entity.BRS;
@Repository
public interface BrsRepo extends JpaRepository<BRS, Long> {

	public List<BRS> findByOfficeName(String officeName);
}
