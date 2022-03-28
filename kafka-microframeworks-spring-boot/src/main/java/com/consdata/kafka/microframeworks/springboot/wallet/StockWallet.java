package com.consdata.kafka.microframeworks.springboot.wallet;

import com.consdata.kafka.microframeworks.springboot.order.Order;
import com.consdata.kafka.microframeworks.springboot.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@Slf4j
@Service
public class StockWallet {

    private final ConcurrentMap<Integer, Wallet> customerWalletMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void initWalletWithRandomValues() {
        IntStream.range(0, 100).forEach(i -> customerWalletMap.put(i, Wallet.generateRandomWallet()));
    }

    public void initTestWallet() {
        IntStream.range(0, 100).forEach(i -> customerWalletMap.put(i, Wallet.generateTestWallet()));
    }

    public Transaction process(Order sellOrder, Order buyOrder) {
        int sellPrice = sellOrder.getDesiredPricePerStock() * sellOrder.getAmount();
        int buyPrice = buyOrder.getDesiredPricePerStock() * buyOrder.getAmount();

        int sellerId = sellOrder.getCustomerId();
        int buyerId = buyOrder.getCustomerId();

        Transaction transaction = Transaction
                .builder()
                .sellingCustomerId(sellerId)
                .buyingCustomerId(buyerId)
                .stockSymbol(sellOrder.getStockSymbol())
                .amount(sellOrder.getAmount())
                .price(sellPrice)
                .build();

        if (buyPrice >= sellPrice && sellerId != buyerId) {
            return execute(transaction);
        }

        return transaction;
    }

    public Transaction execute(Transaction transaction) {
        lockWalletsAndExecuteTransaction(transaction, this::executeTransaction);
        return transaction;
    }

    private void lockWalletsAndExecuteTransaction(Transaction transaction, Consumer<Transaction> transactionConsumer) {
        customerWalletMap.compute(transaction.smallerCustomerId(), (customerId1, outerWallet) -> {
            customerWalletMap.compute(transaction.biggerCustomerId(), (customerId2, innerWallet) -> {
                if (transactionIsPossible(transaction)) {
                    transactionConsumer.accept(transaction);
                }
                return innerWallet;
            });
            return outerWallet;
        });
    }

    public void executeTransaction(Transaction transaction) {
        log.debug("Execution transaction: {}", transaction);

        Wallet sellerWallet = customerWalletMap.get(transaction.getSellingCustomerId());
        Wallet buyerWallet = customerWalletMap.get(transaction.getBuyingCustomerId());

        sellerWallet.changeBalance(transaction.getPrice());
        buyerWallet.changeBalance(-transaction.getPrice());

        sellerWallet.changeStockAmount(transaction.getStockSymbol(), -transaction.getAmount());
        buyerWallet.changeStockAmount(transaction.getStockSymbol(), transaction.getAmount());

        transaction.setExecutionTimestamp(new Date());
    }

    private boolean transactionIsPossible(Transaction transaction) {
        return buyerHasMoney(transaction.getBuyingCustomerId(), transaction.getPrice())
                && sellerHasStocks(transaction.getSellingCustomerId(), transaction.getStockSymbol(), transaction.getAmount());
    }

    private boolean sellerHasStocks(int customerId, String stockSymbol, int amount) {
        return customerWalletMap.get(customerId).hasEnoughStocks(stockSymbol, amount);
    }

    private boolean buyerHasMoney(int customerId, int amount) {
        return customerWalletMap.get(customerId).hasEnoughCash(amount);
    }

    public Map<Integer, Wallet> getCustomerWalletMap() {
        return customerWalletMap;
    }
}
