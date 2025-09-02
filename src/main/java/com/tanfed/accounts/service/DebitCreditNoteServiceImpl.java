package com.tanfed.accounts.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tanfed.accounts.config.JwtTokenValidator;
import com.tanfed.accounts.entity.DebitOrCreditNote;
import com.tanfed.accounts.repository.DebitOrCreditNoteRepo;
import com.tanfed.accounts.utils.CodeGenerator;

@Service
public class DebitCreditNoteServiceImpl implements DebitCreditNoteService {

	@Autowired
	private CodeGenerator codeGenerator;

	@Autowired
	private DebitOrCreditNoteRepo debitOrCreditNoteRepo;

	@Override
	public ResponseEntity<String> saveDebitCreditNote(DebitOrCreditNote obj, String jwt) throws Exception {
		try {
			String code = codeGenerator.generateDrCrNumber(obj.getNoteFor(), obj.getActivity());
			obj.setDrCrNo(code);
			String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
			obj.setEmpId(Arrays.asList(empId));
			debitOrCreditNoteRepo.save(obj);
			return new ResponseEntity<String>("Created Successfully" + "\n" + obj.getNoteFor() + " Number: " + code,
					HttpStatus.CREATED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<DebitOrCreditNote> findDrCrNoteByOfficeName(String officeName) throws Exception {
		try {
			return debitOrCreditNoteRepo.findByOfficeName(officeName);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public DebitOrCreditNote fetchDebitOrCreditNoteByVoucherNo(String drCrNo) throws Exception {
		try {
			return debitOrCreditNoteRepo.findByDrCrNo(drCrNo).get();
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

}
