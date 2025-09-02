package com.tanfed.accounts.utils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import com.tanfed.accounts.entity.DebitOrCreditNote;
import com.tanfed.accounts.entity.SupplierAdvance;
import com.tanfed.accounts.repository.DebitOrCreditNoteRepo;
import com.tanfed.accounts.repository.SupplierAdvanceRepo;

public class CodeGenerator {

	private static final HashMap<String, String> activityAbbreviation = new HashMap<>();

	static {
		activityAbbreviation.put("Fertiliser", "FE");
		activityAbbreviation.put("Agri.Marketing", "AM");
		activityAbbreviation.put("Seeds", "SE");
		activityAbbreviation.put("Pesticides", "PE");
		activityAbbreviation.put("Implements", "IM");
		activityAbbreviation.put("Non Agri.com", "NA");
		activityAbbreviation.put("Others", "OT");
	}

	public String voucherNoGenerator() {
		Random random = new Random();

		LocalDate currentDate = LocalDate.now();
		int year = currentDate.getYear() % 100;
		int month = currentDate.getMonthValue();

		String letters = generateRandomLetters(random, 2);

		int number = random.nextInt(1000);

		String randomCode1 = String.format("%02d%02d%s%03d", year, month, letters, number);

		return randomCode1;

	}

	private static String generateRandomLetters(Random random, int count) {
		StringBuilder sb = new StringBuilder(count);
		for (int i = 0; i < count; i++) {
			char randomChar = (char) ('A' + random.nextInt(26));
			sb.append(randomChar);
		}
		return sb.toString();
	}

	@Autowired
	private SupplierAdvanceRepo supplierAdvanceRepo;

	public String generateSupplierAdvanceNo(String activity) throws Exception {
		try {
			int digitCounter = 0;
			LocalDate currentDate = LocalDate.now();
			int year = currentDate.getYear() % 100;
			int month = currentDate.getMonthValue();
			String actvt = activityAbbreviation.get(activity);
			String code;
			Optional<SupplierAdvance> byReqId;
			do {
				code = String.format("%s%02d%02d%s%03d", actvt, year, month, "ADV", digitCounter);
				byReqId = supplierAdvanceRepo.findBySupplierAdvanceNo(code);
				digitCounter++;
			} while (byReqId.isPresent());
			return code;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Autowired
	private DebitOrCreditNoteRepo debitOrCreditNoteRepo;

	public String generateDrCrNumber(String noteFor, String activity) throws Exception {
		try {
			int digitCounter = 0;

			LocalDate currentDate = LocalDate.now();
			int year = currentDate.getYear() % 100;
			int month = currentDate.getMonthValue();

			String actvt = activityAbbreviation.get(activity);
			String drcr = noteFor.equals("Credit Note") ? "CN" : "DN";
			String code;
			Optional<DebitOrCreditNote> byReqId;
			do {
				code = String.format("%s%02d%02d%s%02d", drcr, year, month, actvt, digitCounter);
				byReqId = debitOrCreditNoteRepo.findByDrCrNo(code);
				digitCounter++;
			} while (byReqId.isPresent());
			return code;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
}
