package com.evertix.financialwallet.controller;

import com.evertix.financialwallet.controller.commons.MessageResponse;
import com.evertix.financialwallet.security.request.LoginRequest;
import com.evertix.financialwallet.security.request.SignUpRequest;
import com.evertix.financialwallet.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@Tag(name = "User", description = "API is Ready")
@RequestMapping("api/user")
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @PutMapping("/{username}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update User", description = "Update User",
            security = @SecurityRequirement(name = "bearerAuth"), tags = {"User"})
    public ResponseEntity<MessageResponse> update(@RequestBody @Valid SignUpRequest signUpRequest,
                                                  @PathVariable String username) {
        return this.userService.updateUser(signUpRequest, username);
    }

    @PutMapping("/")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Change Password", description = "Change Password",
            security = @SecurityRequirement(name = "bearerAuth"), tags = {"User"})
    public ResponseEntity<MessageResponse> password(@RequestBody @Valid LoginRequest loginRequest) {
        return this.userService.updatePassword(loginRequest);
    }
}
