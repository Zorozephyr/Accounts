package com.vishnu.accounts.service;

import com.vishnu.accounts.dto.CustomerDto;

public interface IAccountsService {

    /**
    *
    *@param customerDto - CustomerDto Object
     */
    void createAccount(CustomerDto customerDto);

    /**
     *
     * @param mobileNumber
     * @return
     */
    CustomerDto fetchAccount(String mobileNumber);
}
