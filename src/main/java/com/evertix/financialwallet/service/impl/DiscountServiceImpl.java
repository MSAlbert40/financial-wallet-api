package com.evertix.financialwallet.service.impl;

import com.evertix.financialwallet.controller.commons.MessageResponse;
import com.evertix.financialwallet.controller.constants.ResponseConstants;
import com.evertix.financialwallet.model.Discount;
import com.evertix.financialwallet.model.Rate;
import com.evertix.financialwallet.model.Wallet;
import com.evertix.financialwallet.model.dto.SaveDiscountRequest;
import com.evertix.financialwallet.model.request.DiscountRequest;
import com.evertix.financialwallet.repository.DiscountRepository;
import com.evertix.financialwallet.repository.RateRepository;
import com.evertix.financialwallet.repository.WalletRepository;
import com.evertix.financialwallet.service.DiscountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<MessageResponse> addDiscount(DiscountRequest discount) {
        try {
            // Validate Complete
            Discount saveDiscount = this.convertToEntity(discount);
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
