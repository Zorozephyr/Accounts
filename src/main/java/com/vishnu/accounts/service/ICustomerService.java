package com.vishnu.accounts.service;

import com.vishnu.accounts.dto.CustomerDetailsDto;

public interface ICustomerService {

    CustomerDetailsDto fetchCustomerDetails(String mobileNum);
}
