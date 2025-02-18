package com.vishnu.accounts.service.impl;

import com.vishnu.accounts.constants.AccountsConstants;
import com.vishnu.accounts.dto.AccountsDto;
import com.vishnu.accounts.dto.CustomerDto;
import com.vishnu.accounts.entity.Accounts;
import com.vishnu.accounts.entity.Customer;
import com.vishnu.accounts.exception.CustomerAlreadyExistsException;
import com.vishnu.accounts.exception.ResourceNotFoundException;
import com.vishnu.accounts.mapper.AccountsMapper;
import com.vishnu.accounts.mapper.CustomerMapper;
import com.vishnu.accounts.repository.AccountsRepository;
import com.vishnu.accounts.repository.CustomerRepository;
import com.vishnu.accounts.service.IAccountsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;


@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private CustomerRepository customerRepository;

    //Single Constructor implies it will be automatically autowired

    @Override
    public void createAccount(CustomerDto customerDto) {
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Optional<Customer> optionalCustomer = customerRepository.findByMobileNumber(customer.getMobileNumber());
        if(optionalCustomer.isPresent()){
            throw new CustomerAlreadyExistsException("Customer already registered with given Mobile Number"+ customer.getMobileNumber());
        }
        customer.setCreatedAt(LocalDateTime.now());
        customer.setCreatedBy("Anonymous");
        Customer savedCustomer = customerRepository.save(customer);
        Accounts newAccount = createNewAccount(customer);
        accountsRepository.save(newAccount);

    }

    /**
     * @param mobileNumber
     * @return
     */
    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
            Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(()-> new ResourceNotFoundException("Customer","mobileNumber",mobileNumber));
            Accounts account = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(()-> new ResourceNotFoundException("Account","customerId",customer.getCustomerId().toString()));
            CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
            customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(account, new AccountsDto()));
            return customerDto;
    }

    private Accounts createNewAccount(Customer customer){
        Accounts account = new Accounts();
        account.setCustomerId(customer.getCustomerId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);

        account.setAccountNumber(randomAccNumber);
        account.setBranchAddress(AccountsConstants.ADDRESS);
        account.setAccountType(AccountsConstants.SAVINGS);
        account.setCreatedAt(LocalDateTime.now());
        account.setCreatedBy("Anonymous");
        return account;
    }

}
