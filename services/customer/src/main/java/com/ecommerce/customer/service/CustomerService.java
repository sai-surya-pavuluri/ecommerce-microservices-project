package com.ecommerce.customer.service;

import com.ecommerce.customer.dto.CustomerRequest;
import com.ecommerce.customer.dto.CustomerResponse;
import com.ecommerce.customer.exception.CustomerNotFoundException;
import com.ecommerce.customer.model.Customer;
import com.ecommerce.customer.repository.CustomerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    private static ModelMapper modelMapper;

    public String createCustomer(@Valid CustomerRequest customerRequest) {
        Customer customer = getCustomer(customerRequest);
        var newCustomer = customerRepository.save(customer);
        return newCustomer.getId();
    }

    public Customer getCustomer(CustomerRequest customerRequest){
        return modelMapper.map(customerRequest, Customer.class);
    }

    public String updateCustomer(@Valid CustomerRequest customerRequest) {
        var customer = customerRepository.findById(customerRequest.id())
                .orElseThrow(() ->new CustomerNotFoundException(String.format("Customer details not found for id : %s", customerRequest.id())));
        mergeCustomer(customer, customerRequest);
        var updatedCustomer = customerRepository.save(customer);
        return updatedCustomer.getId();
    }

    public void mergeCustomer(Customer customer, CustomerRequest customerRequest) {
        if(customerRequest.firstName() != null) {
            customer.setFirstName(customerRequest.firstName());
        }
        if(customerRequest.lastName() != null) {
            customer.setLastName(customerRequest.lastName());
        }
        if(customerRequest.email() != null) {
            customer.setEmail(customerRequest.email());
        }
        if(customerRequest.address() != null) {
            customer.setAddress(customerRequest.address());
        }

    }

    public List<CustomerResponse> getAllCustomers() {

        return customerRepository.findAll().stream().map(customer -> modelMapper.map(customer, CustomerResponse.class)).collect(Collectors.toList());
    }

    public Boolean existsById(String customerId) {
        return customerRepository.existsById(customerId);
    }

    public CustomerResponse findById(String customerId) {
        return modelMapper.map(customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException(String.format("No customer found with id : %s", customerId))), CustomerResponse.class);
    }

    public void deleteById(String customerId) {
        customerRepository.deleteById(customerId);
    }
}
