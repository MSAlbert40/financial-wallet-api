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
                    case "WALLET_MIXED" -> EWallet.WALLET_MIXED;
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
                    case "WALLET_MIXED" -> EWallet.WALLET_MIXED;
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
    public ResponseEntity<MessageResponse> addWallet(WalletRequest wallet, Long enterpriseId) {
        try {
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

            // Complete Validation
            Wallet saveWallet = this.convertToEntity(wallet);
            // Enterprise
            saveWallet.setEnterprise(enterprise);
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
        if (resource.getTypeWallet() != null) resource.setTypeWallet(wallet.getTypeWallet().getName().toString());
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
