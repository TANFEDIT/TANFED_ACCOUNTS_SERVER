package com.tanfed.accounts.service;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tanfed.accounts.config.JwtTokenValidator;
import com.tanfed.accounts.entity.JournalVoucher;
import com.tanfed.accounts.entity.SalesJvTable;
import com.tanfed.accounts.model.Invoice;
import com.tanfed.accounts.model.ProductMaster;
import com.tanfed.accounts.model.SalesJvTableData;
import com.tanfed.accounts.model.TableDataInvoice;
import com.tanfed.accounts.repository.JournalVoucherRepo;
import com.tanfed.accounts.repository.SalesJvRepo;
import com.tanfed.accounts.response.DataForSalesJv;
import com.tanfed.accounts.utils.CodeGenerator;

@Service
public class JournalVoucherServiceImpl implements JournalVoucherService {

	@Autowired
	private JournalVoucherRepo journalVoucherRepo;

	@Autowired
	private SalesJvRepo salesJvRepo;

	@Autowired
	private CodeGenerator codeGenerator;

	private static Logger logger = LoggerFactory.getLogger(JournalVoucherServiceImpl.class);

	@Override
	public ResponseEntity<String> saveJournalVoucher(JournalVoucher obj, String jwt) throws Exception {
		try {
			String jvNo;
			do {
				jvNo = "JV" + codeGenerator.voucherNoGenerator();
			} while (journalVoucherRepo.findByVoucherNo(jvNo).isPresent());
			String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
			obj.setEmpId(Arrays.asList(empId));
			obj.setVoucherNo(jvNo);
			obj.getRows().forEach(i -> {
				i.setJv(obj);
			});
			journalVoucherRepo.save(obj);
			return new ResponseEntity<>("JV Created Successfully\n JV Number : " + jvNo, HttpStatus.CREATED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public ResponseEntity<String> saveSalesJv(SalesJvTable obj, String jwt) throws Exception {
		try {
			List<String> jvNoList = new ArrayList<String>();
			String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
			obj.getJvList().forEach(item -> {
				String jvNo;
				do {
					jvNo = "JV" + codeGenerator.voucherNoGenerator();
				} while (journalVoucherRepo.findByVoucherNo(jvNo).isPresent());
				jvNoList.add(jvNo);
				item.setVoucherNo(jvNo);
				item.setEmpId(Arrays.asList(empId));
				item.setIfmsId(
						obj.getTableData().stream().map(itemData -> itemData.getIfmsId()).collect(Collectors.toList()));
			});
			obj.setEmpId(Arrays.asList(empId));
			salesJvRepo.save(obj);
			return new ResponseEntity<>("JV Created Successfully\n JV Number : " + String.join(",", jvNoList),
					HttpStatus.CREATED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public ResponseEntity<String> editJournalVoucher(JournalVoucher obj, String jwt) throws Exception {
		try {
			JournalVoucher journalVoucher = journalVoucherRepo.findById(obj.getId()).get();
			String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
			obj.getEmpId().add(empId);
			journalVoucher.setRows(obj.getRows());

			journalVoucherRepo.save(journalVoucher);
			return new ResponseEntity<String>("Updated Successfully", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public JournalVoucher getJvByJvNo(String jvNo) throws Exception {
		try {
			JournalVoucher journalVoucher = journalVoucherRepo.findByVoucherNo(jvNo).get();

			if (journalVoucher == null) {
				throw new FileNotFoundException("No data found");
			}
			return journalVoucher;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<JournalVoucher> getJvByOfficeName(String officeName) throws Exception {
		try {
			return journalVoucherRepo.findByOfficeName(officeName);

		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<String> getJvNoByOfficeName(String officeName) throws Exception {
		try {
			return journalVoucherRepo.findByOfficeName(officeName).stream().map(JournalVoucher::getVoucherNo)
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<JournalVoucher> getJvDataByFilter(String officeName, String month) throws Exception {
		try {
			return journalVoucherRepo.findByOfficeName(officeName).stream()
					.filter(item -> item.getJvMonth().equals(month)).collect(Collectors.toList());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Autowired
	private MasterService masterService;

	@Autowired
	private InventryService inventryService;

	@Override
	public DataForSalesJv getDataForSalesJv(String officeName, String activity, String firmType, String productCategory,
			LocalDate fromDate, LocalDate toDate, String jwt) throws Exception {
		try {
			DataForSalesJv data = new DataForSalesJv();
			if (officeName != null && !officeName.isEmpty()) {
				List<ProductMaster> productData = masterService.getProductDataHandler(jwt);
				if (activity != null && !activity.isEmpty()) {
					data.setProductCategoryList(productData.stream().filter(item -> item.getActivity().equals(activity))
							.map(item -> item.getProductCategory()).collect(Collectors.toSet()));
					if (firmType != null && !firmType.isEmpty()) {
						if (productCategory != null && !productCategory.isEmpty()) {
							if (fromDate != null && toDate != null) {
								logger.info("{}", fromDate);
								logger.info("{}", toDate);
								if (firmType.equals("ALL")) {
									logger.info("{}", firmType);
									data.setTableData(
											inventryService.getInvoiceDataByOfficenameHandler(officeName, jwt).stream()
													.filter(item -> item.getActivity().equals(activity)
															&& !item.getDate().isBefore(fromDate)
															&& !item.getDate().isAfter(toDate))
													.map(item -> mapTableData(item, productCategory))
													.collect(Collectors.toList()));
								}
								data.setTableData(
										inventryService.getInvoiceDataByOfficenameHandler(officeName, jwt).stream()
												.filter(item -> item.getActivity().equals(activity)
														&& item.getFirmType().equals(firmType)
														&& !item.getDate().isBefore(fromDate)
														&& !item.getDate().isAfter(toDate))
												.map(item -> mapTableData(item, productCategory))
												.collect(Collectors.toList()));

								data.setTotalQty(data.getTableData().stream().mapToDouble(item -> item.getQty()).sum());
								data.setTotalNet(data.getTableData().stream()
										.mapToDouble(item -> item.getNetInvoiceValue()).sum());
								data.setTotalMargin(
										data.getTableData().stream().mapToDouble(item -> item.getIssueMargin()).sum());
								Double cgst = data.getTableData().stream().mapToDouble(item -> item.getCgst()).sum();
								Double sgst = data.getTableData().stream().mapToDouble(item -> item.getSgst()).sum();
								data.setTotalGst(cgst + sgst);
							}
						}
					}
				}
			}
			return data;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public SalesJvTableData mapTableData(Invoice invoice, String productCategory) {

		Map<String, Double> productQtyMap = invoice.getTableData().stream().collect(Collectors
				.groupingBy(TableDataInvoice::getProductCategory, Collectors.summingDouble(TableDataInvoice::getQty)));

		Map<String, Double> productBasicMap = invoice.getTableData().stream()
				.collect(Collectors.groupingBy(TableDataInvoice::getProductCategory,
						Collectors.summingDouble(item -> item.getBasicPrice() * item.getQty())));

		Map<String, Double> productCgstMap = invoice.getTableData().stream()
				.collect(Collectors.groupingBy(TableDataInvoice::getProductCategory,
						Collectors.summingDouble(item -> item.getCgstAmount() * item.getQty())));

		Map<String, Double> productSgstMap = invoice.getTableData().stream()
				.collect(Collectors.groupingBy(TableDataInvoice::getProductCategory,
						Collectors.summingDouble(item -> item.getSgstAmount() * item.getQty())));

		Map<String, Double> productMarginMap = invoice.getTableData().stream()
				.collect(Collectors.groupingBy(TableDataInvoice::getProductCategory,
						Collectors.summingDouble(item -> item.getMargin() * item.getQty())));

		Double qty = 0.0, basic = 0.0, cgst = 0.0, sgst = 0.0, margin = 0.0;

		if (productQtyMap.get(productCategory) != null && productBasicMap.get(productCategory) != null
				&& productCgstMap.get(productCategory) != null && productSgstMap.get(productCategory) != null
				&& productMarginMap.get(productCategory) != null) {
			qty = productQtyMap.get(productCategory);
			basic = productBasicMap.get(productCategory);
			cgst = productCgstMap.get(productCategory);
			sgst = productSgstMap.get(productCategory);
			margin = productMarginMap.get(productCategory);
		} else {
			throw new NullPointerException("No Data Found For Category : " + productCategory);
		}

		return new SalesJvTableData(null, invoice.getInvoiceNo(), invoice.getDate(), invoice.getNameOfInstitution(),
				invoice.getDistrict(), invoice.getDistrict(), qty, roundToThreeDecimalPlaces(basic + cgst + sgst),
				roundToThreeDecimalPlaces(basic), roundToThreeDecimalPlaces(cgst), roundToThreeDecimalPlaces(sgst),
				roundToThreeDecimalPlaces(margin), null);
	}

	private static double roundToThreeDecimalPlaces(double value) {
		return new BigDecimal(value).setScale(3, RoundingMode.HALF_UP).doubleValue();
	}

	@Override
	public void revertSupplierAdvanceJv(JournalVoucher obj, String jwt) throws Exception {
		try {
			JournalVoucher journalVoucher = journalVoucherRepo.findById(obj.getId()).get();
			String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
			obj.getEmpId().add(empId);
			journalVoucher.setVoucherStatus("Rejected");
			journalVoucherRepo.save(journalVoucher);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<SalesJvTable> fetchSalesJvByOfficeName(String officeName) throws Exception {
		try {
			return salesJvRepo.findByOfficeName(officeName);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

}
