package com.evertix.financialwallet.service.impl;

import com.evertix.financialwallet.controller.commons.MessageResponse;
import com.evertix.financialwallet.controller.constants.ResponseConstants;
import com.evertix.financialwallet.model.Rate;
import com.evertix.financialwallet.model.TypeRate;
import com.evertix.financialwallet.model.Wallet;
import com.evertix.financialwallet.model.dto.SaveRateRequest;
import com.evertix.financialwallet.model.enums.ERate;
import com.evertix.financialwallet.model.request.RateRequest;
import com.evertix.financialwallet.repository.RateRepository;
import com.evertix.financialwallet.repository.TypeRateRepository;
import com.evertix.financialwallet.repository.WalletRepository;
import com.evertix.financialwallet.service.RateService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@Service
public class RateServiceImpl implements RateService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    TypeRateRepository typeRateRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    RateRepository rateRepository;

    @Override
    public ResponseEntity<MessageResponse> getAllRate() {
        try {
            List<Rate> rateList = this.rateRepository.findAll();
            if (rateList.isEmpty()) { return this.getNotRateContent(); }
            MessageResponse response = MessageResponse.builder()
                    .code(ResponseConstants.SUCCESS_CODE)
                    .message(ResponseConstants.MSG_SUCCESS_CONS)
                    .data(rateList)
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
    public ResponseEntity<MessageResponse> getRate(Long walletId) {
        try {
            Rate rate = this.rateRepository.findByWalletId(walletId);
            MessageResponse response = MessageResponse.builder()
                    .code(ResponseConstants.SUCCESS_CODE)
                    .message(ResponseConstants.MSG_SUCCESS_CONS)
                    .data(rate)
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
    public ResponseEntity<MessageResponse> getAllRatePaginated(String typeRateName, Pageable pageable) {
        try {
            // Identify Type Rate
            ERate typeRate = switch (typeRateName) {
                case "RATE_NOMINAL" -> ERate.RATE_NOMINAL;
                case "RATE_EFFECTIVE" -> ERate.RATE_EFFECTIVE;
                default -> throw new RuntimeException("Not fount Type Rate");
            };

            Page<Rate> ratePage = this.rateRepository.findAllByTypeRateName(typeRate, pageable);
            if (ratePage == null || ratePage.isEmpty()) { return this.getNotRateContent(); }
            MessageResponse response = MessageResponse.builder()
                    .code(ResponseConstants.SUCCESS_CODE)
                    .message(ResponseConstants.MSG_SUCCESS_CONS)
                    .data(ratePage)
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
    public ResponseEntity<MessageResponse> addRate(RateRequest rate, String typeRateName, Long walletId) {
        try {
            // Validate if Rate Exists
            Wallet wallet = this.walletRepository.findById(walletId).orElse(null);
            if (wallet == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.builder()
                                .code(ResponseConstants.ERROR_CODE)
                                .message("Don't exists wallet with ID: " + walletId)
                                .build());
            }

            // Identify Type Rate
            TypeRate typeRate;
            if (typeRateName == null){
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.builder()
                                .code(ResponseConstants.ERROR_CODE)
                                .message("Sorry, Type Rate not found")
                                .build());
            } else {
                typeRate = switch (typeRateName) {
                    case "RATE_NOMINAL" -> typeRateRepository.findByName(ERate.RATE_NOMINAL)
                            .orElseThrow(() -> new RuntimeException("Sorry, Type Rate not found"));
                    case "RATE_EFFECTIVE" -> typeRateRepository.findByName(ERate.RATE_EFFECTIVE)
                            .orElseThrow(() -> new RuntimeException("Sorry, Type Rate not found"));
                    default -> throw new RuntimeException("Sorry, Type Rate is wrong.");
                };
            }

            // Create New Rate
            Rate saveRate = this.convertToEntity(rate);

            // Set Type Rate & Wallet
            saveRate.setTypeRate(typeRate);
            saveRate.setWallet(wallet);
            // Save Rate
            rateRepository.save(saveRate);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.SUCCESS_CODE)
                            .message("Successful creation request")
                            .data(this.convertToResource(saveRate))
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
    public ResponseEntity<MessageResponse> updateRate(RateRequest rate, Long rateId) {
        try {
            // Validate if Rate Exists
            Rate saveRate = this.rateRepository.findById(rateId).orElse(null);
            if (saveRate == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.builder()
                                .code(ResponseConstants.ERROR_CODE)
                                .message("Don't exists rate with ID: " + rateId)
                                .build());
            }

            // Update Rate Data
            saveRate.setDaysRate(rate.getDaysRate());
            saveRate.setPeriodRate(rate.getPeriodRate());
            saveRate.setDaysRate(rate.getDaysRate());
            saveRate.setValueRate(rate.getValueRate());
            saveRate.setDiscountAt(rate.getDiscountAt());
            if (saveRate.getTypeRate().getName().toString().equals("RATE_NOMINAL")){
                saveRate.setPeriodCapitalization(rate.getPeriodCapitalization());
                saveRate.setDaysCapitalization(rate.getDaysCapitalization());
            }
            // Save Update
            saveRate = rateRepository.save(saveRate);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.SUCCESS_CODE)
                            .message("Successful update")
                            .data(this.convertToResource(saveRate))
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
    public ResponseEntity<MessageResponse> deleteRate(Long rateId) {
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

            // Delete Rate
            rateRepository.delete(rate);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.SUCCESS_CODE)
                            .message("Successful delete")
                            .data(this.convertToResource(rate))
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

    private Rate convertToEntity(RateRequest rate) { return modelMapper.map(rate, Rate.class); }

    private SaveRateRequest convertToResource(Rate rate) {
        SaveRateRequest resource = modelMapper.map(rate, SaveRateRequest.class);
        resource.setTypeRate(rate.getTypeRate().getName().toString());
        return resource;
    }

    private ResponseEntity<MessageResponse> getNotRateContent(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(MessageResponse.builder()
                        .code(ResponseConstants.WARNING_CODE)
                        .message(ResponseConstants.MSG_WARNING_CONS)
                        .data(null)
                        .build());
    }
}
