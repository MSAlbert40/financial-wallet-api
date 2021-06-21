package com.evertix.financialwallet.service;

import com.evertix.financialwallet.controller.commons.MessageResponse;
import com.evertix.financialwallet.security.request.LoginRequest;
import com.evertix.financialwallet.security.request.SignUpRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<MessageResponse> updateUser(SignUpRequest signUpRequest, String username);
    ResponseEntity<MessageResponse> updatePassword(LoginRequest loginRequest);
}
