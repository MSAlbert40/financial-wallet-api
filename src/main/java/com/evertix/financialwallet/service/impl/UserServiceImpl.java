package com.evertix.financialwallet.service.impl;

import com.evertix.financialwallet.controller.commons.MessageResponse;
import com.evertix.financialwallet.controller.constants.ResponseConstants;
import com.evertix.financialwallet.model.User;
import com.evertix.financialwallet.model.dto.SaveUserRequest;
import com.evertix.financialwallet.repository.UserRepository;
import com.evertix.financialwallet.security.request.LoginRequest;
import com.evertix.financialwallet.security.request.SignUpRequest;
import com.evertix.financialwallet.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public ResponseEntity<MessageResponse> updateUser(SignUpRequest signUpRequest, String username) {
        try {
            // Validate if User Exists
            User saveUser = this.userRepository.findByUsername(username).orElse(null);
            if (saveUser == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.builder()
                                .code(ResponseConstants.ERROR_CODE)
                                .message("Don't exists user with username: " + username)
                                .build());
            }
            // Update Enterprise Data
            saveUser.setUsername(signUpRequest.getUsername());
            saveUser.setPassword(encoder.encode(signUpRequest.getPassword()));
            saveUser.setEmail(signUpRequest.getEmail());
            saveUser.setName(signUpRequest.getName());
            saveUser.setLastName(signUpRequest.getLastName());
            saveUser.setDni(signUpRequest.getDni());
            saveUser.setPhone(signUpRequest.getPhone());
            // Save Update
            saveUser = userRepository.save(saveUser);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.SUCCESS_CODE)
                            .message("Successful update")
                            .data(this.convertToResource(saveUser))
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
    public ResponseEntity<MessageResponse> updatePassword(LoginRequest loginRequest) {
        try {
            User passwordUser = userRepository.findByUsernameOrEmail(loginRequest.getUsername(), loginRequest.getUsername()).orElse(null);
            if (passwordUser == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.builder()
                                .code(ResponseConstants.ERROR_CODE)
                                .message("Don't exists user with username or email: " + loginRequest.getUsername())
                                .build());
            }
            // Change Password
            passwordUser.setPassword(encoder.encode(loginRequest.getPassword()));
            // Save Update
            userRepository.save(passwordUser);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.SUCCESS_CODE)
                            .message("Successful password change")
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

    private User convertToEntity(SignUpRequest user) { return modelMapper.map(user, User.class); }

    private SaveUserRequest convertToResource(User user) {
        SaveUserRequest resource = modelMapper.map(user, SaveUserRequest.class);
        resource.setRole(user.getRole().getName().toString());
        return resource;
    }
}
