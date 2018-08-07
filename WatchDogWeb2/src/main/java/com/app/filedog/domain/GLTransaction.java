package com.app.filedog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
/***
 * 
 * @author intakhabalam.s@hcl.com
 *
 */
@XmlRootElement(name = "GLTransaction")
@XmlAccessorType(XmlAccessType.FIELD)
public class GLTransaction {

	private String GLClassificationEnumVal;

	private String GLOriginRegionZoneCode;

	private String VoucherReferenceNumberTypeEnumVal;

	private String CreatedByUserCode;

	private String AccountingPeriod;

	private String BatchSequenceNumber;

	private String CustomerCode;

	private String SourceOfGLAccountNumberEnumVal;

	private String TripEntityTypeEnumVal;

	private String GLTypeCode;

	private String DivisionCode;

	private String GLAccountNumber;

	private String TransactionAmount;

	private String ChargeCode;

	private String SystemShipmentNumber;

	private String CommodityCode;

	private String CreatedDateTime;

	private String ServiceCode;

	private String ShipmentEntryTypeCode;

	private String LogisticsGroupCode;

	private String CreditOrDebitEnumVal;

	private String ProfitCenterCode;

	private String FiscalYear;

	private String FreightBillNumber;

	private String GLTransactionStatusEnumVal;

	private String GLTransactionTypeEnumVal;

	private String GLTransactionStatusDistinctAPAREnumVal;

	private String SystemGLTransactionID;

	private String GLDestinationRegionZoneCode;

	private String CurrencyCode;

	private String SystemShipmentID;

	private String VoucherNumber;

	private String CarrierCode;

	private String SystemShipmentLegID;

	private String CostCenterCode;

	private String TransactionRunNumber;

	private String ExchangeRate;

	private String ReferenceNumber;

	private String Memo;

	private String CarrierDescription;

	private String SystemLoadID;

	private String ChargeDetailTypeEnumVal;

	private String CustomerDescription;

	public String getGLClassificationEnumVal() {
		return GLClassificationEnumVal;
	}

	public void setGLClassificationEnumVal(String GLClassificationEnumVal) {
		this.GLClassificationEnumVal = GLClassificationEnumVal;
	}

	public String getGLOriginRegionZoneCode() {
		return GLOriginRegionZoneCode;
	}

	public void setGLOriginRegionZoneCode(String GLOriginRegionZoneCode) {
		this.GLOriginRegionZoneCode = GLOriginRegionZoneCode;
	}

	public String getVoucherReferenceNumberTypeEnumVal() {
		return VoucherReferenceNumberTypeEnumVal;
	}

	public void setVoucherReferenceNumberTypeEnumVal(String VoucherReferenceNumberTypeEnumVal) {
		this.VoucherReferenceNumberTypeEnumVal = VoucherReferenceNumberTypeEnumVal;
	}

	public String getCreatedByUserCode() {
		return CreatedByUserCode;
	}

	public void setCreatedByUserCode(String CreatedByUserCode) {
		this.CreatedByUserCode = CreatedByUserCode;
	}

	public String getAccountingPeriod() {
		return AccountingPeriod;
	}

	public void setAccountingPeriod(String AccountingPeriod) {
		this.AccountingPeriod = AccountingPeriod;
	}

	public String getBatchSequenceNumber() {
		return BatchSequenceNumber;
	}

	public void setBatchSequenceNumber(String BatchSequenceNumber) {
		this.BatchSequenceNumber = BatchSequenceNumber;
	}

	public String getCustomerCode() {
		return CustomerCode;
	}

	public void setCustomerCode(String CustomerCode) {
		this.CustomerCode = CustomerCode;
	}

	public String getSourceOfGLAccountNumberEnumVal() {
		return SourceOfGLAccountNumberEnumVal;
	}

	public void setSourceOfGLAccountNumberEnumVal(String SourceOfGLAccountNumberEnumVal) {
		this.SourceOfGLAccountNumberEnumVal = SourceOfGLAccountNumberEnumVal;
	}

	public String getTripEntityTypeEnumVal() {
		return TripEntityTypeEnumVal;
	}

	public void setTripEntityTypeEnumVal(String TripEntityTypeEnumVal) {
		this.TripEntityTypeEnumVal = TripEntityTypeEnumVal;
	}

	public String getGLTypeCode() {
		return GLTypeCode;
	}

	public void setGLTypeCode(String GLTypeCode) {
		this.GLTypeCode = GLTypeCode;
	}

	public String getDivisionCode() {
		return DivisionCode;
	}

	public void setDivisionCode(String DivisionCode) {
		this.DivisionCode = DivisionCode;
	}

	public String getGLAccountNumber() {
		return GLAccountNumber;
	}

	public void setGLAccountNumber(String GLAccountNumber) {
		this.GLAccountNumber = GLAccountNumber;
	}

	public String getTransactionAmount() {
		return TransactionAmount;
	}

	public void setTransactionAmount(String TransactionAmount) {
		this.TransactionAmount = TransactionAmount;
	}

	public String getChargeCode() {
		return ChargeCode;
	}

	public void setChargeCode(String ChargeCode) {
		this.ChargeCode = ChargeCode;
	}

	public String getSystemShipmentNumber() {
		return SystemShipmentNumber;
	}

	public void setSystemShipmentNumber(String SystemShipmentNumber) {
		this.SystemShipmentNumber = SystemShipmentNumber;
	}

	public String getCommodityCode() {
		return CommodityCode;
	}

	public void setCommodityCode(String CommodityCode) {
		this.CommodityCode = CommodityCode;
	}

	public String getCreatedDateTime() {
		return CreatedDateTime;
	}

	public void setCreatedDateTime(String CreatedDateTime) {
		this.CreatedDateTime = CreatedDateTime;
	}

	public String getServiceCode() {
		return ServiceCode;
	}

	public void setServiceCode(String ServiceCode) {
		this.ServiceCode = ServiceCode;
	}

	public String getShipmentEntryTypeCode() {
		return ShipmentEntryTypeCode;
	}

	public void setShipmentEntryTypeCode(String ShipmentEntryTypeCode) {
		this.ShipmentEntryTypeCode = ShipmentEntryTypeCode;
	}

	public String getLogisticsGroupCode() {
		return LogisticsGroupCode;
	}

	public void setLogisticsGroupCode(String LogisticsGroupCode) {
		this.LogisticsGroupCode = LogisticsGroupCode;
	}

	public String getCreditOrDebitEnumVal() {
		return CreditOrDebitEnumVal;
	}

	public void setCreditOrDebitEnumVal(String CreditOrDebitEnumVal) {
		this.CreditOrDebitEnumVal = CreditOrDebitEnumVal;
	}

	public String getProfitCenterCode() {
		return ProfitCenterCode;
	}

	public void setProfitCenterCode(String ProfitCenterCode) {
		this.ProfitCenterCode = ProfitCenterCode;
	}

	public String getFiscalYear() {
		return FiscalYear;
	}

	public void setFiscalYear(String FiscalYear) {
		this.FiscalYear = FiscalYear;
	}

	public String getFreightBillNumber() {
		return FreightBillNumber;
	}

	public void setFreightBillNumber(String FreightBillNumber) {
		this.FreightBillNumber = FreightBillNumber;
	}

	public String getGLTransactionStatusEnumVal() {
		return GLTransactionStatusEnumVal;
	}

	public void setGLTransactionStatusEnumVal(String GLTransactionStatusEnumVal) {
		this.GLTransactionStatusEnumVal = GLTransactionStatusEnumVal;
	}

	public String getGLTransactionTypeEnumVal() {
		return GLTransactionTypeEnumVal;
	}

	public void setGLTransactionTypeEnumVal(String GLTransactionTypeEnumVal) {
		this.GLTransactionTypeEnumVal = GLTransactionTypeEnumVal;
	}

	public String getGLTransactionStatusDistinctAPAREnumVal() {
		return GLTransactionStatusDistinctAPAREnumVal;
	}

	public void setGLTransactionStatusDistinctAPAREnumVal(String GLTransactionStatusDistinctAPAREnumVal) {
		this.GLTransactionStatusDistinctAPAREnumVal = GLTransactionStatusDistinctAPAREnumVal;
	}

	public String getSystemGLTransactionID() {
		return SystemGLTransactionID;
	}

	public void setSystemGLTransactionID(String SystemGLTransactionID) {
		this.SystemGLTransactionID = SystemGLTransactionID;
	}

	public String getGLDestinationRegionZoneCode() {
		return GLDestinationRegionZoneCode;
	}

	public void setGLDestinationRegionZoneCode(String GLDestinationRegionZoneCode) {
		this.GLDestinationRegionZoneCode = GLDestinationRegionZoneCode;
	}

	public String getCurrencyCode() {
		return CurrencyCode;
	}

	public void setCurrencyCode(String CurrencyCode) {
		this.CurrencyCode = CurrencyCode;
	}

	public String getSystemShipmentID() {
		return SystemShipmentID;
	}

	public void setSystemShipmentID(String SystemShipmentID) {
		this.SystemShipmentID = SystemShipmentID;
	}

	public String getVoucherNumber() {
		return VoucherNumber;
	}

	public void setVoucherNumber(String VoucherNumber) {
		this.VoucherNumber = VoucherNumber;
	}

	public String getCarrierCode() {
		return CarrierCode;
	}

	public void setCarrierCode(String CarrierCode) {
		this.CarrierCode = CarrierCode;
	}

	public String getSystemShipmentLegID() {
		return SystemShipmentLegID;
	}

	public void setSystemShipmentLegID(String SystemShipmentLegID) {
		this.SystemShipmentLegID = SystemShipmentLegID;
	}

	public String getCostCenterCode() {
		return CostCenterCode;
	}

	public void setCostCenterCode(String CostCenterCode) {
		this.CostCenterCode = CostCenterCode;
	}

	public String getTransactionRunNumber() {
		return TransactionRunNumber;
	}

	public void setTransactionRunNumber(String TransactionRunNumber) {
		this.TransactionRunNumber = TransactionRunNumber;
	}

	public String getExchangeRate() {
		return ExchangeRate;
	}

	public void setExchangeRate(String ExchangeRate) {
		this.ExchangeRate = ExchangeRate;
	}

	public String getReferenceNumber() {
		return ReferenceNumber;
	}

	public void setReferenceNumber(String ReferenceNumber) {
		this.ReferenceNumber = ReferenceNumber;
	}

	public String getMemo() {
		return Memo;
	}

	public void setMemo(String Memo) {
		this.Memo = Memo;
	}

	public String getCarrierDescription() {
		return CarrierDescription;
	}

	public void setCarrierDescription(String CarrierDescription) {
		this.CarrierDescription = CarrierDescription;
	}

	public String getSystemLoadID() {
		return SystemLoadID;
	}

	public void setSystemLoadID(String SystemLoadID) {
		this.SystemLoadID = SystemLoadID;
	}

	public String getChargeDetailTypeEnumVal() {
		return ChargeDetailTypeEnumVal;
	}

	public void setChargeDetailTypeEnumVal(String ChargeDetailTypeEnumVal) {
		this.ChargeDetailTypeEnumVal = ChargeDetailTypeEnumVal;
	}

	public String getCustomerDescription() {
		return CustomerDescription;
	}

	public void setCustomerDescription(String CustomerDescription) {
		this.CustomerDescription = CustomerDescription;
	}

}
