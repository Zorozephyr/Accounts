package com.vishnu.accounts.service.impl;

import com.vishnu.accounts.dto.*;
import com.vishnu.accounts.entity.Accounts;
import com.vishnu.accounts.entity.Customer;
import com.vishnu.accounts.exception.ResourceNotFoundException;
import com.vishnu.accounts.mapper.AccountsMapper;
import com.vishnu.accounts.mapper.CustomerMapper;
import com.vishnu.accounts.repository.AccountsRepository;
import com.vishnu.accounts.repository.CustomerRepository;
import com.vishnu.accounts.service.ICustomerService;
import com.vishnu.accounts.service.clients.CardsFeignClient;
import com.vishnu.accounts.service.clients.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    @Autowired
    public CustomerServiceImpl(AccountsRepository accountsRepository, CustomerRepository customerRepository, CardsFeignClient cardsFeignClient, LoansFeignClient loansFeignClient) {
        this.accountsRepository = accountsRepository;
        this.customerRepository = customerRepository;
        this.cardsFeignClient = cardsFeignClient;
        this.loansFeignClient = loansFeignClient;
    }

    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNum) {
        Customer customer = customerRepository.findByMobileNumber(mobileNum).orElseThrow(
                ()-> new ResourceNotFoundException("Customer","MobileNumber", mobileNum)
        );

        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                ()-> new ResourceNotFoundException("Account","CustomerId", mobileNum)
        );
        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(mobileNum);
        customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());

        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(mobileNum);
        customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());

        return customerDetailsDto;
    }
}
