package com.consdata.kafka.microframeworks.springboot.wallet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/wallet")
public class WalletController {

    private CustomerWallet customerWallet;

    public WalletController(CustomerWallet customerWallet) {
        this.customerWallet = customerWallet;
    }

    @GetMapping("/")
    public Map<Integer, Wallet> get() {
        return customerWallet.getCustomerWalletMap();
    }

}
