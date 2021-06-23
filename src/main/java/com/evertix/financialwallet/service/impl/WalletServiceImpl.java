package com.evertix.financialwallet.service.impl;

import com.evertix.financialwallet.controller.commons.MessageResponse;
import com.evertix.financialwallet.controller.constants.ResponseConstants;
import com.evertix.financialwallet.model.*;
import com.evertix.financialwallet.model.dto.SaveWalletRequest;
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
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EnterpriseRepository enterpriseRepository;

    @Autowired
    TypeWalletRepository typeWalletRepository;

    @Autowired
    RateRepository rateRepository;

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
