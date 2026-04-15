package com.logistic.system.domain.service;

import com.logistic.system.domain.enums.AccountRole;
import com.logistic.system.domain.enums.AccountStatus;
import com.logistic.system.domain.model.Account;
import com.logistic.system.domain.model.Customer;

import org.springframework.stereotype.Service;

@Service
public class CustomerDomainService {

    /**
     * Kiểm tra khách hàng có đang hoạt động không
     */
    public boolean isActive(Customer customer) {
        return customer != null && AccountStatus.ACTIVE.equals(customer.getStatus());
    }

    /**
     * Validate thông tin khách hàng trước khi lưu
     */
    public void validateCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (customer.getFirstName() == null || customer.getLastName() == null) {
            throw new IllegalArgumentException("Customer first name and last name is required");
        }
        if (customer.getEmail() == null || !customer.getEmail().contains("@")) {
            throw new IllegalArgumentException("Valid email is required");
        }
        if (customer.getPhone() == null || customer.getPhone().length() < 9) {
            throw new IllegalArgumentException("Valid phone number is required");
        }
    }

    /**
     * Kiểm tra quyền của khách hàng (ví dụ chỉ CUSTOMER mới được đặt hàng)
     */
    public boolean canPlaceOrder(Customer customer, Account account) {
        return isActive(customer) && AccountRole.CUSTOMER.equals(account.getRole());
    }
}
