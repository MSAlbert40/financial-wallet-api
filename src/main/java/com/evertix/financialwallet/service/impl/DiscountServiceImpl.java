package com.evertix.financialwallet.service.impl;

import com.evertix.financialwallet.controller.commons.MessageResponse;
import com.evertix.financialwallet.controller.constants.ResponseConstants;
import com.evertix.financialwallet.model.Discount;
import com.evertix.financialwallet.model.Expense;
import com.evertix.financialwallet.model.Wallet;
import com.evertix.financialwallet.model.dto.SaveDiscountRequest;
import com.evertix.financialwallet.model.enums.EExpense;
import com.evertix.financialwallet.model.request.DiscountRequest;
import com.evertix.financialwallet.repository.DiscountRepository;
import com.evertix.financialwallet.repository.WalletRepository;
import com.evertix.financialwallet.service.DiscountService;
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
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class DiscountServiceImpl implements DiscountService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    DiscountRepository discountRepository;

    @Override
    public ResponseEntity<MessageResponse> getAllDiscount() {
        try {
            List<Discount> discountList = this.discountRepository.findAll();
            if (discountList.isEmpty()) { return this.getNotDiscountContent(); }
            MessageResponse response = MessageResponse.builder()
                    .code(ResponseConstants.SUCCESS_CODE)
                    .message(ResponseConstants.MSG_SUCCESS_CONS)
                    .data(discountList)
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
    public ResponseEntity<MessageResponse> getAllDiscount(Long walletId) {
        try {
            List<Discount> discountList = this.discountRepository.findAllByWalletId(walletId);
            if (discountList == null || discountList.isEmpty()){
                this.getNotDiscountContent();
            }
            MessageResponse response = MessageResponse.builder()
                    .code(ResponseConstants.SUCCESS_CODE)
                    .message(ResponseConstants.MSG_SUCCESS_CONS)
                    .data(discountList)
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
    public ResponseEntity<MessageResponse> getAllDiscountPaginated(Long walletId, Pageable pageable) {
        try {
            Page<Discount> discountPage = this.discountRepository.findAllByWalletId(walletId, pageable);
            if (discountPage == null || discountPage.isEmpty()){
                this.getNotDiscountContent();
            }
            MessageResponse response = MessageResponse.builder()
                    .code(ResponseConstants.SUCCESS_CODE)
                    .message(ResponseConstants.MSG_SUCCESS_CONS)
                    .data(discountPage)
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
    public ResponseEntity<MessageResponse> addDiscount(DiscountRequest discount, Long walletId) {
        try {
            // Validate if Wallet Exists
            Wallet wallet = walletRepository.findById(walletId).orElse(null);
            if (wallet == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.builder()
                                .code(ResponseConstants.ERROR_CODE)
                                .message("Don't exists wallet with ID: " + walletId)
                                .build());
            }

            BigDecimal valueTCEA;
            // Validate Complete
            Discount saveDiscount = this.convertToEntity(discount);
            // Financial Operation
            financialOperation(discount, saveDiscount, wallet);
            // Set Wallet
            saveDiscount.setWallet(wallet);
            // Set Days Total Period
            wallet.setDaysTotalPeriod(wallet.getDaysTotalPeriod() + saveDiscount.getDaysPeriod());
            // Set Value Total Received
            wallet.setValueTotalReceived(wallet.getValueTotalReceived().add(saveDiscount.getValueReceived()));
            // Set Value Total Delivered
            wallet.setValueTotalDelivered(wallet.getValueTotalDelivered().add(saveDiscount.getValueDelivered()));
            // Set Total TCEA
            valueTCEA = this.TotalTCEA(wallet.getValueTotalReceived(), wallet.getValueTotalDelivered(), wallet.getDaysTotalPeriod(), wallet.getRate().getDaysYear());
            wallet.setValueTCEA(valueTCEA.setScale(7, RoundingMode.HALF_EVEN));
            // Save Discount
            discountRepository.save(saveDiscount);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.SUCCESS_CODE)
                            .message("Successful creation request")
                            .data(this.convertToResource(saveDiscount))
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
    public void financialOperation(DiscountRequest request, Discount discount, Wallet wallet) {
        Integer daysPeriod;
        BigDecimal expenseInitial, expenseFinal;
        BigDecimal rateEffective, rateDiscount, TCEA;
        BigDecimal valueDiscount, valueNet, valueReceived, valueDelivered;
        switch (wallet.getRate().getTypeRate().getName().toString()) {
            case "RATE_NOMINAL":
                daysPeriod = daysPeriod(request.getExpirationAt(), wallet.getRate().getDiscountAt());
                discount.setDaysPeriod(daysPeriod);

                rateEffective = rateEffectiveNominal(daysPeriod, wallet.getRate().getValueRate(), wallet.getRate().getDaysRate(),
                        wallet.getRate().getDaysCapitalization());
                discount.setRateEffective(rateEffective.setScale(7, RoundingMode.HALF_EVEN));

                rateDiscount = rateDiscount(rateEffective);
                discount.setRateDiscount(rateDiscount.setScale(7, RoundingMode.HALF_EVEN));

                valueDiscount = valueDiscount(request.getValueNominal(), rateDiscount);
                discount.setValueDiscount(valueDiscount.setScale(2, RoundingMode.HALF_EVEN));

                expenseInitial = expenseInitial(wallet.getExpenses(), request.getValueNominal());
                discount.setExpenseInitial(expenseInitial.setScale(2, RoundingMode.HALF_EVEN));

                expenseFinal = expenseFinal(wallet.getExpenses(), request.getValueNominal());
                discount.setExpenseFinal(expenseFinal.setScale(2, RoundingMode.HALF_EVEN));

                valueNet = valueNet(request.getValueNominal(), valueDiscount);
                discount.setValueNet(valueNet.setScale(2, RoundingMode.HALF_EVEN));

                valueReceived = valueReceived(valueNet, expenseInitial, request.getRetention());
                discount.setValueReceived(valueReceived.setScale(2, RoundingMode.HALF_EVEN));

                valueDelivered = valueDelivered(request.getValueNominal(), expenseFinal, request.getRetention());
                discount.setValueDelivered(valueDelivered.setScale(2, RoundingMode.HALF_EVEN));

                TCEA = TCEA(valueReceived.setScale(2, RoundingMode.HALF_EVEN),
                        valueDelivered.setScale(2, RoundingMode.HALF_EVEN), daysPeriod, wallet.getRate().getDaysYear());
                discount.setTCEA(TCEA.setScale(7, RoundingMode.HALF_EVEN));
                break;
            case "RATE_EFFECTIVE":
                daysPeriod = daysPeriod(request.getExpirationAt(), wallet.getRate().getDiscountAt());
                discount.setDaysPeriod(daysPeriod);

                rateEffective = rateEffective(daysPeriod, wallet.getRate().getValueRate());
                discount.setRateEffective(rateEffective.setScale(7, RoundingMode.HALF_EVEN));

                rateDiscount = rateDiscount(rateEffective);
                discount.setRateDiscount(rateDiscount.setScale(7, RoundingMode.HALF_EVEN));

                valueDiscount = valueDiscount(request.getValueNominal(), rateDiscount);
                discount.setValueDiscount(valueDiscount.setScale(2, RoundingMode.HALF_EVEN));

                expenseInitial = expenseInitial(wallet.getExpenses(), request.getValueNominal());
                discount.setExpenseInitial(expenseInitial.setScale(2, RoundingMode.HALF_EVEN));

                expenseFinal = expenseFinal(wallet.getExpenses(), request.getValueNominal());
                discount.setExpenseFinal(expenseFinal.setScale(2, RoundingMode.HALF_EVEN));

                valueNet = valueNet(request.getValueNominal(), valueDiscount);
                discount.setValueNet(valueNet.setScale(2, RoundingMode.HALF_EVEN));

                valueReceived = valueReceived(valueNet, expenseInitial, request.getRetention());
                discount.setValueReceived(valueReceived.setScale(2, RoundingMode.HALF_EVEN));

                valueDelivered = valueDelivered(request.getValueNominal(), expenseFinal, request.getRetention());
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

    private Discount convertToEntity(DiscountRequest discount) { return modelMapper.map(discount, Discount.class); }

    private SaveDiscountRequest convertToResource(Discount discount) { return modelMapper.map(discount, SaveDiscountRequest.class); }

    private ResponseEntity<MessageResponse> getNotDiscountContent(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(MessageResponse.builder()
                        .code(ResponseConstants.WARNING_CODE)
                        .message(ResponseConstants.MSG_WARNING_CONS)
                        .data(null)
                        .build());
    }
}
