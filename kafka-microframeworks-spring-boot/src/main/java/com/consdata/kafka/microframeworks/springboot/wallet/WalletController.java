package com.consdata.kafka.microframeworks.springboot.wallet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/wallet")
public class WalletController {

    private StockWallet stockWallet;

    public WalletController(StockWallet stockWallet) {
        this.stockWallet = stockWallet;
    }

    @GetMapping("/")
    public Map<Integer, Wallet> get() {
        return stockWallet.getCustomerWalletMap();
    }

}
