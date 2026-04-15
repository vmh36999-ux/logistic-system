package com.logistic.system.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistic.system.application.dto.request.CustomerRequest;
import com.logistic.system.application.dto.reponse.CustomerResponse;
import com.logistic.system.domain.model.Customer;
import com.logistic.system.domain.service.CustomerDomainService;
import com.logistic.system.infrastructure.mapper.CustomerMapper;
import com.logistic.system.infrastructure.persistence.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerApplicationService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CustomerDomainService customerDomainService;

    /**
     * Lấy thông tin khách hàng theo accountId
     */
    public CustomerResponse getCustomerInfo(Long accountId) {
        // 1. Lấy dữ liệu cũ từ DB
        var entity = customerRepository.findByAccount_AccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        // 2. Chuyển sang Domain Model để xử lý nghiệp vụ
        var customer = customerMapper.toDomain(entity);

        // Kiểm tra trạng thái bằng domain service
        if (!customerDomainService.isActive(customer)) {
            throw new RuntimeException("Customer is not active");
        }

        // Trả về DTO cho client
        return customerMapper.toResponse(customer);
    }

    /**
     * Cập nhật thông tin khách hàng
     */
    @Transactional // Nên có transactional để đảm bảo an toàn dữ liệu
    public CustomerResponse updateCustomer(Long accountId, CustomerRequest request) {
        // 1. Tìm Entity hiện tại từ DB
        var entity = customerRepository.findByAccount_AccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        // 2. Map sang Domain để thực hiện logic nghiệp vụ
        Customer customer = customerMapper.toDomain(entity);

        // Áp dụng dữ liệu mới từ request
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setDefaultAddress(request.getAddress());

        // 4. Kiểm tra logic nghiệp vụ (Domain Service)
        customerDomainService.validateCustomer(customer);
        customerMapper.updateEntityFromDomain(customer, entity);
        // 5. Lưu lại entity
        var updatedEntity = customerRepository.save(customerMapper.toEntity(customer));

        return customerMapper.toResponse(customerMapper.toDomain(updatedEntity));
    }

}
