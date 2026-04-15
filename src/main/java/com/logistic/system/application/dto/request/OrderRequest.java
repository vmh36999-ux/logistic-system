package com.logistic.system.application.dto.request;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class OrderRequest {
    private Long accountId;
    private String receiverName;
    private String receiverPhone;
    private Long receiverProvinceId;
    private String receiverAddress;
    private String paymentMethod;
    private String note;
    private List<OrderItemRequest> items;

    // getter and setter
    public OrderRequest() {
    }

    // setter
    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    // getter
    public List<OrderItemRequest> getItems() {
        return items;
    }

    // setter
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    // getter
    public Long getAccountId() {
        return accountId;
    }

    // setter
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    // getter
    public String getReceiverName() {
        return receiverName;
    }

    // setter
    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    // getter
    public String getReceiverPhone() {
        return receiverPhone;
    }

    // setter
    public void setReceiverProvinceId(Long receiverProvinceId) {
        this.receiverProvinceId = receiverProvinceId;
    }

    // getter
    public Long getReceiverProvinceId() {
        return receiverProvinceId;
    }

    // setter
    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    // getter
    public String getReceiverAddress() {
        return receiverAddress;
    }

    // setter
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // getter
    public String getPaymentMethod() {
        return paymentMethod;
    }

    // setter
    public void setNote(String note) {
        this.note = note;
    }

    // getter
    public String getNote() {
        return note;
    }

}