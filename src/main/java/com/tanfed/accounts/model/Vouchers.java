package com.tanfed.accounts.model;

import java.util.List;

import com.tanfed.accounts.dto.ContraEntryViewDto;
import com.tanfed.accounts.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vouchers {

	private List<OpeningBalance> openingBalance;
	private OpeningBalance openingBalanceData;

	private List<CashReceiptVoucher> cashReceiptVoucher;
	private CashReceiptVoucher cashReceiptVoucherData;

	private List<AdjustmentReceiptVoucher> adjustmentReceiptVoucher;
	private AdjustmentReceiptVoucher adjustmentReceiptVoucherData;

	private List<PaymentVoucher> paymentVoucher;
	private PaymentVoucher paymentVoucherData;

	private List<JournalVoucher> journalVoucher;
	private JournalVoucher journalVoucherData;

	private List<AccountsMaster> accountsMaster;
	private AccountsMaster accountsMasterData;

	private List<TaxInfo> taxInfo;
	private TaxInfo taxInfoData;

	private List<BeneficiaryMaster> beneficiaryMaster;
	private BeneficiaryMaster beneficiaryMasterData;

	private List<SupplierAdvance> supplierAdvance;
	private SupplierAdvance supplierAdvanceData;

	private List<DebitOrCreditNote> drCrNote;
	private DebitOrCreditNote drCrNoteData;

	private List<SundryCrOb> sundryCrOb;
	private SundryCrOb sundryCrObData;

	private List<SundryDrOb> sundryDrOb;
	private SundryDrOb sundryDrObData;

	private List<BillsGstOb> billsGstOb;
	private BillsGstOb billsGstObData;

	private List<ContraEntryViewDto> contraEntry;
	private List<ReconciliationEntry> reconciliationEntry;
}
