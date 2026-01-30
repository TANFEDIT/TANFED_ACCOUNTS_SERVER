package com.tanfed.accounts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tanfed.accounts.entity.ContraEntry;
@Repository
public interface ContraEntryRepo extends JpaRepository<ContraEntry, Long> {

	public ContraEntry findByContraId(String contraId);
	
	public List<ContraEntry> findByOfficeName(String officeName);
}
