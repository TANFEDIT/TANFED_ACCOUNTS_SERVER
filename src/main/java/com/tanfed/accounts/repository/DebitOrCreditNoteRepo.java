package com.tanfed.accounts.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tanfed.accounts.entity.DebitOrCreditNote;

@Repository
public interface DebitOrCreditNoteRepo extends JpaRepository<DebitOrCreditNote, Long> {

	public Optional<DebitOrCreditNote> findByDrCrNo(String drCrNo);

	public List<DebitOrCreditNote> findByOfficeName(String officeName);
	
	@Query("SELECT e FROM DebitOrCreditNote e WHERE (e.voucherStatus = 'Pending' OR e.voucherStatus = 'Verified') AND e.officeName =:officeName")
	public List<DebitOrCreditNote> findPendingDataByOfficeName(@Param("officeName") String officeName);
	
	@Query("SELECT e FROM DebitOrCreditNote e WHERE e.voucherStatus = 'Approved' AND e.officeName =:officeName")
	public List<DebitOrCreditNote> findApprovedDataByOfficeName(@Param("officeName") String officeName);
}
