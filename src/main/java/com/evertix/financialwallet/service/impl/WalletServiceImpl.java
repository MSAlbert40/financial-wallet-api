package com.evertix.financialwallet.service.impl;

import com.evertix.financialwallet.controller.commons.MessageResponse;
import com.evertix.financialwallet.controller.constants.ResponseConstants;
import com.evertix.financialwallet.model.*;
import com.evertix.financialwallet.model.dto.SaveWalletRequest;
import com.evertix.financialwallet.model.enums.EExpense;
import com.evertix.financialwallet.model.enums.EWallet;
import com.evertix.financialwallet.model.request.WalletRequest;
import com.evertix.financialwallet.repository.*;
import com.evertix.financialwallet.service.WalletService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class WalletServiceImpl implements WalletService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EnterpriseRepository enterpriseRepository;

    @Autowired
    TypeWalletRepository typeWalletRepository;

    @Autowired
    DiscountRepository discountRepository;

    @Autowired
    RateRepository rateRepository;

    @Autowired
    ExpenseRepository expenseRepository;

    @Autowired
    WalletRepository walletRepository;

    @Override
    public ResponseEntity<MessageResponse> getAllWallet() {
        try {
            List<Wallet> walletList = this.walletRepository.findAll();
            if (walletList.isEmpty()) {
                return this.getNotWalletContent();
            }
            MessageResponse response = MessageResponse.builder()
                    .code(ResponseConstants.SUCCESS_CODE)
                    .message(ResponseConstants.MSG_SUCCESS_CONS)
                    .data(walletList)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.ERROR_CODE)
                            .message("Internal Error: " + sw.toString())
                            .build());
        }
    }

    @Override
    public ResponseEntity<MessageResponse> getWallet(Long walletId) {
        try {
            Wallet wallet = this.walletRepository.findById(walletId).orElse(null);
            MessageResponse response = MessageResponse.builder()
                    .code(ResponseConstants.SUCCESS_CODE)
                    .message(ResponseConstants.MSG_SUCCESS_CONS)
                    .data(wallet)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.ERROR_CODE)
                            .message("Internal Error: " + sw.toString())
                            .build());
        }
    }

    @Override
    public ResponseEntity<MessageResponse> getAllWallet(String typeWallet, Long enterpriseId) {
        try {
            // Identify Type Wallet
            EWallet wallet;
            if (typeWallet == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.builder()
                                .code(ResponseConstants.ERROR_CODE)
                                .message("Sorry, Type Wallet not found")
                                .build());
            } else {
                wallet = switch (typeWallet) {
                    case "WALLET_LETTERS" -> EWallet.WALLET_LETTERS;
                    case "WALLET_BILLS" -> EWallet.WALLET_BILLS;
                    case "WALLET_RECEIPTS_OF_HONORARY" -> EWallet.WALLET_RECEIPTS_OF_HONORARY;
                    default -> throw new RuntimeException("Sorry, Type Wallet is wrong.");
                };
            }

            List<Wallet> walletList = this.walletRepository.findAllByTypeWalletNameAndEnterpriseId(wallet, enterpriseId);
            if (walletList == null || walletList.isEmpty()) {
                return this.getNotWalletContent();
            }
            MessageResponse response = MessageResponse.builder()
                    .code(ResponseConstants.SUCCESS_CODE)
                    .message(ResponseConstants.MSG_SUCCESS_CONS)
                    .data(walletList)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.ERROR_CODE)
                            .message("Internal Error: " + sw.toString())
                            .build());
        }
    }

    @Override
    public ResponseEntity<MessageResponse> getAllWalletPaginated(String typeWallet, Long enterpriseId, Pageable pageable) {
        try {
            // Identify Type Wallet
            EWallet wallet;
            if (typeWallet == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.builder()
                                .code(ResponseConstants.ERROR_CODE)
                                .message("Sorry, Type Wallet not found")
                                .build());
            } else {
                wallet = switch (typeWallet) {
                    case "WALLET_LETTERS" -> EWallet.WALLET_LETTERS;
                    case "WALLET_BILLS" -> EWallet.WALLET_BILLS;
                    case "WALLET_RECEIPTS_OF_HONORARY" -> EWallet.WALLET_RECEIPTS_OF_HONORARY;
                    default -> throw new RuntimeException("Sorry, Type Wallet is wrong.");
                };
            }

            Page<Wallet> walletPage = this.walletRepository.findAllByTypeWalletNameAndEnterpriseId(wallet, enterpriseId, pageable);
            if (walletPage == null || walletPage.isEmpty()) {
                return this.getNotWalletContent();
            }
            MessageResponse response = MessageResponse.builder()
                    .code(ResponseConstants.SUCCESS_CODE)
                    .message(ResponseConstants.MSG_SUCCESS_CONS)
                    .data(walletPage)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.ERROR_CODE)
                            .message("Internal Error: " + sw.toString())
                            .build());
        }
    }

    @Override
    public ResponseEntity<MessageResponse> addWallet(WalletRequest wallet, String typeWallet, Long enterpriseId, Long rateId) {
        try {
            // Validate if Rate Exists
            Rate rate = this.rateRepository.findById(rateId).orElse(null);
            if (rate == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.builder()
                                .code(ResponseConstants.ERROR_CODE)
                                .message("Don't exists rate with ID: " + rateId)
                                .build());
            }

            // Validate if Enterprise Exists
            Enterprise enterprise = this.enterpriseRepository.findById(enterpriseId).orElse(null);
            if (enterprise == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.builder()
                                .code(ResponseConstants.ERROR_CODE)
                                .message("Don't exists enterprise with ID: " + enterpriseId)
                                .build());
            }

            // Identify Type Wallet
            TypeWallet typeWallets;
            if (typeWallet == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.builder()
                                .code(ResponseConstants.ERROR_CODE)
                                .message("Sorry, Type Wallet not found")
                                .build());
            } else {
                typeWallets = switch (typeWallet) {
                    case "WALLET_LETTERS" -> typeWalletRepository.findAllByName(EWallet.WALLET_LETTERS)
                            .orElseThrow(() -> new RuntimeException("Sorry, Type Wallet not found"));
                    case "WALLET_BILLS" -> typeWalletRepository.findAllByName(EWallet.WALLET_BILLS)
                            .orElseThrow(() -> new RuntimeException("Sorry, Type Wallet not found"));
                    case "WALLET_RECEIPTS_OF_HONORARY" -> typeWalletRepository.findAllByName(EWallet.WALLET_RECEIPTS_OF_HONORARY)
                            .orElseThrow(() -> new RuntimeException("Sorry, Type Wallet not found"));
                    default -> throw new RuntimeException("Sorry, Type Wallet is wrong.");
                };
            }

            // Complete Validation
            Wallet saveWallet = this.convertToEntity(wallet);
            // Set Type Wallet, Enterprise & Rate
            saveWallet.setTypeWallet(typeWallets);
            saveWallet.setEnterprise(enterprise);
            saveWallet.setRate(rate);
            // Initial Variable
            saveWallet.setValueTotalDelivered(new BigDecimal(0));
            saveWallet.setValueTotalReceived(new BigDecimal(0));
            saveWallet.setValueTCEA(new BigDecimal(0));
            saveWallet.setDaysTotalPeriod(0);
            // Save Wallet
            walletRepository.save(saveWallet);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.SUCCESS_CODE)
                            .message("Successful creation request")
                            .data(this.convertToResource(saveWallet))
                            .build());
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.ERROR_CODE)
                            .message("Internal Error: " + sw.toString())
                            .build());
        }
    }

    @Override
    public ResponseEntity<MessageResponse> addDiscounts(Long walletId, Long discountId) {
        try {
            // Validate if Wallet Exists
            Wallet wallet = this.walletRepository.findById(walletId).orElse(null);
            if (wallet == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.builder()
                                .code(ResponseConstants.ERROR_CODE)
                                .message("Don't exists wallet with ID: " + walletId)
                                .build());
            }

            BigDecimal valueTCEA;
            // Validate if Discount Exists
            Discount discount = this.discountRepository.findById(discountId).orElse(null);
            if (!wallet.getDiscounts().contains(discount)) {
                wallet.getDiscounts().add(discount);
                financialOperation(wallet, discount);
            }
            if (discount != null) {
                // Set Days Total Period
                wallet.setDaysTotalPeriod(wallet.getDaysTotalPeriod() + discount.getDaysPeriod());
                // Set Value Total Received
                wallet.setValueTotalReceived(wallet.getValueTotalReceived().add(discount.getValueReceived()));
                // Set Value Total Delivered
                wallet.setValueTotalDelivered(wallet.getValueTotalDelivered().add(discount.getValueDelivered()));
                // Set Total TCEA
                valueTCEA = this.TotalTCEA(wallet.getValueTotalReceived(), wallet.getValueTotalDelivered(), wallet.getDaysTotalPeriod(), wallet.getRate().getDaysYear());
                wallet.setValueTCEA(valueTCEA.setScale(7, RoundingMode.HALF_EVEN));
            }
            walletRepository.save(wallet);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.SUCCESS_CODE)
                            .message("Successful Add Discounts")
                            .data(this.convertToResource(wallet))
                            .build());
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.ERROR_CODE)
                            .message("Internal Error: " + sw.toString())
                            .build());
        }
    }

    @Override
    public void financialOperation(Wallet wallet, Discount discount) {
        Integer daysPeriod;
        BigDecimal rateEffective, rateDiscount, TCEA, expenseInitial, expenseFinal;
        BigDecimal valueDiscount, valueNet, valueReceived, valueDelivered;
        switch (wallet.getRate().getTypeRate().getName().toString()) {
            case "RATE_NOMINAL":
                daysPeriod = daysPeriod(discount.getExpirationAt(), wallet.getRate().getDiscountAt());
                discount.setDaysPeriod(daysPeriod);

                rateEffective = rateEffectiveNominal(daysPeriod, wallet.getRate().getValueRate(), wallet.getRate().getDaysRate(),
                        wallet.getRate().getDaysCapitalization());
                discount.setRateEffective(rateEffective.setScale(7, RoundingMode.HALF_EVEN));

                rateDiscount = rateDiscount(rateEffective);
                discount.setRateDiscount(rateDiscount.setScale(7, RoundingMode.HALF_EVEN));

                valueDiscount = valueDiscount(discount.getValueNominal(), rateDiscount);
                discount.setValueDiscount(valueDiscount.setScale(2, RoundingMode.HALF_EVEN));

                expenseInitial = expenseInitial(wallet.getExpenses(), discount.getValueNominal());
                discount.setExpenseInitial(expenseInitial.setScale(2, RoundingMode.HALF_EVEN));

                expenseFinal = expenseFinal(wallet.getExpenses(), discount.getValueNominal());
                discount.setExpenseFinal(expenseFinal.setScale(2, RoundingMode.HALF_EVEN));

                valueNet = valueNet(discount.getValueNominal(), valueDiscount);
                discount.setValueNet(valueNet.setScale(2, RoundingMode.HALF_EVEN));

                valueReceived = valueReceived(valueNet, expenseInitial, discount.getRetention());
                discount.setValueReceived(valueReceived.setScale(2, RoundingMode.HALF_EVEN));

                valueDelivered = valueDelivered(discount.getValueNominal(), expenseFinal, discount.getRetention());
                discount.setValueDelivered(valueDelivered.setScale(2, RoundingMode.HALF_EVEN));

                TCEA = TCEA(valueReceived.setScale(2, RoundingMode.HALF_EVEN),
                        valueDelivered.setScale(2, RoundingMode.HALF_EVEN), daysPeriod, wallet.getRate().getDaysYear());
                discount.setTCEA(TCEA.setScale(7, RoundingMode.HALF_EVEN));
                break;
            case "RATE_EFFECTIVE":
                daysPeriod = daysPeriod(discount.getExpirationAt(), wallet.getRate().getDiscountAt());
                discount.setDaysPeriod(daysPeriod);

                rateEffective = rateEffective(daysPeriod, wallet.getRate().getValueRate());
                discount.setRateEffective(rateEffective.setScale(7, RoundingMode.HALF_EVEN));

                rateDiscount = rateDiscount(rateEffective);
                discount.setRateDiscount(rateDiscount.setScale(7, RoundingMode.HALF_EVEN));

                valueDiscount = valueDiscount(discount.getValueNominal(), rateDiscount);
                discount.setValueDiscount(valueDiscount.setScale(2, RoundingMode.HALF_EVEN));

                expenseInitial = expenseInitial(wallet.getExpenses(), discount.getValueNominal());
                discount.setExpenseInitial(expenseInitial.setScale(2, RoundingMode.HALF_EVEN));

                expenseFinal = expenseFinal(wallet.getExpenses(), discount.getValueNominal());
                discount.setExpenseFinal(expenseFinal.setScale(2, RoundingMode.HALF_EVEN));

                valueNet = valueNet(discount.getValueNominal(), valueDiscount);
                discount.setValueNet(valueNet.setScale(2, RoundingMode.HALF_EVEN));

                valueReceived = valueReceived(valueNet, expenseInitial, discount.getRetention());
                discount.setValueReceived(valueReceived.setScale(2, RoundingMode.HALF_EVEN));

                valueDelivered = valueDelivered(discount.getValueNominal(), expenseFinal, discount.getRetention());
                discount.setValueDelivered(valueDelivered.setScale(2, RoundingMode.HALF_EVEN));

                TCEA = TCEA(valueReceived.setScale(2, RoundingMode.HALF_EVEN),
                        valueDelivered.setScale(2, RoundingMode.HALF_EVEN), daysPeriod, wallet.getRate().getDaysYear());
                discount.setTCEA(TCEA.setScale(7, RoundingMode.HALF_EVEN));
                break;
            default: break;
        }
    }

    @Override
    public Integer daysPeriod(LocalDate expiration, LocalDate discount) {
        long daysPeriod = DAYS.between(discount, expiration);
        return (int) daysPeriod;
    }

    @Override
    public BigDecimal rateEffective(Integer daysPeriod, BigDecimal valueRate) {
        double firstStep = daysPeriod.doubleValue() / 360;
        double secondStep = (valueRate.doubleValue() / 100) + 1;
        double rateEffective = (Math.pow(secondStep, firstStep) - 1) * 100;
        return new BigDecimal(rateEffective);
    }

    @Override
    public BigDecimal rateEffectiveNominal(Integer daysPeriod, BigDecimal valueRate, Integer daysRate, Integer daysCapitalization) {
        double convertDaysRate = daysRate.doubleValue() / daysCapitalization.doubleValue();
        double convertDaysPeriod = daysPeriod.doubleValue() / daysCapitalization.doubleValue();

        double firstStep = ((valueRate.doubleValue() / 100) / convertDaysRate) + 1;
        double rateEffectiveNominal = (Math.pow(firstStep, convertDaysPeriod) - 1) * 100;
        return new BigDecimal(rateEffectiveNominal);
    }

    @Override
    public BigDecimal rateDiscount(BigDecimal rateEffective) {
        double rateDiscount = ((rateEffective.doubleValue() / 100) / (1 + (rateEffective.doubleValue() / 100))) * 100;
        return new BigDecimal(rateDiscount);
    }

    @Override
    public BigDecimal valueDiscount(BigDecimal valueNominal, BigDecimal rateDiscount) {
        double valueDiscount = valueNominal.doubleValue() * (rateDiscount.doubleValue() / 100);
        return new BigDecimal(valueDiscount);
    }

    @Override
    public BigDecimal expenseInitial(List<Expense> expenses, BigDecimal valueNominal) {
        double expenseInitial = 0;
        for (Expense expense: expenses) {
            if (expense.getTypeExpense().getName() == EExpense.EXPENSE_INITIAL) {
                if (expense.getTypeValue().equals("Percentage")){
                    expenseInitial = expenseInitial + (expense.getValue().doubleValue() * valueNominal.doubleValue());
                }
                else if (expense.getTypeValue().equals("Cash")) expenseInitial = expenseInitial + expense.getValue().doubleValue();
            }
        }
        return new BigDecimal(expenseInitial);
    }

    @Override
    public BigDecimal expenseFinal(List<Expense> expenses, BigDecimal valueNominal) {
        double expenseFinal = 0;
        for (Expense expense: expenses) {
            if (expense.getTypeExpense().getName() == EExpense.EXPENSE_FINAL) {
                if (expense.getTypeValue().equals("Percentage")){
                    expenseFinal = expenseFinal + (expense.getValue().doubleValue() * valueNominal.doubleValue());
                }
                else if (expense.getTypeValue().equals("Cash")) expenseFinal = expenseFinal + expense.getValue().doubleValue();
            }
        }
        return new BigDecimal(expenseFinal);
    }

    @Override
    public BigDecimal valueNet(BigDecimal valueNominal, BigDecimal valueDiscount) {
        double valueNet = valueNominal.doubleValue() - valueDiscount.doubleValue();
        return new BigDecimal(valueNet);
    }

    @Override
    public BigDecimal valueReceived(BigDecimal valueNet, BigDecimal expenseInitial, BigDecimal retention) {
        double valueReceived = valueNet.doubleValue() - expenseInitial.doubleValue() - retention.doubleValue();
        return new BigDecimal(valueReceived);
    }

    @Override
    public BigDecimal valueDelivered(BigDecimal valueNominal, BigDecimal expenseFinal, BigDecimal retention) {
        double valueDelivered = valueNominal.doubleValue() + expenseFinal.doubleValue() - retention.doubleValue();
        return new BigDecimal(valueDelivered);
    }

    @Override
    public BigDecimal TCEA(BigDecimal valueReceived, BigDecimal valueDelivered, Integer daysPeriod, Integer daysYear) {
        double firstStep = daysYear.doubleValue() / daysPeriod.doubleValue();
        double secondStep = valueDelivered.doubleValue() / valueReceived.doubleValue();
        double TCEA = (Math.pow(secondStep, firstStep) - 1) * 100;
        return new BigDecimal(TCEA);
    }

    private BigDecimal TotalTCEA(BigDecimal valueReceived, BigDecimal valueDelivered, Integer daysPeriod, Integer daysYear) {
        double firstStep = daysYear.doubleValue() / daysPeriod.doubleValue();
        double secondStep = valueDelivered.doubleValue() / valueReceived.doubleValue();
        double TCEA = (Math.pow(secondStep, firstStep) - 1) * 100;
        return new BigDecimal(TCEA);
    }

    private Wallet convertToEntity(WalletRequest wallet) {
        return modelMapper.map(wallet, Wallet.class);
    }

    private SaveWalletRequest convertToResource(Wallet wallet) {
        SaveWalletRequest resource = modelMapper.map(wallet, SaveWalletRequest.class);
        resource.setEnterprise(wallet.getEnterprise().getName());
        resource.setTypeWallet(wallet.getTypeWallet().getName().toString());
        resource.setRate(wallet.getRate().getTypeRate().getName().toString());
        return resource;
    }

    private ResponseEntity<MessageResponse> getNotWalletContent() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(MessageResponse.builder()
                        .code(ResponseConstants.WARNING_CODE)
                        .message(ResponseConstants.MSG_WARNING_CONS)
                        .data(null)
                        .build());
    }
}
