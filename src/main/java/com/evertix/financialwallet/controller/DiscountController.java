package com.evertix.financialwallet.controller;

import com.evertix.financialwallet.controller.commons.MessageResponse;
import com.evertix.financialwallet.model.request.DiscountRequest;
import com.evertix.financialwallet.service.DiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@Tag(name = "Discount", description = "API is Ready")
@RequestMapping("api/discount")
@RestController
public class DiscountController {
    @Autowired
    DiscountService discountService;

    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "View All Discount", description = "View All Discount",
            security = @SecurityRequirement(name = "bearerAuth"), tags = {"Discount"})
    public ResponseEntity<MessageResponse> getAll() {
        return this.discountService.getAllDiscount();
    }

    @GetMapping("/")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "View All Discount by Wallet", description = "View All Discount by Wallet",
            security = @SecurityRequirement(name = "bearerAuth"), tags = {"Discount"})
    public ResponseEntity<MessageResponse> getAll(@RequestParam Long walletId) {
        return this.discountService.getAllDiscount(walletId);
    }

    @GetMapping("/paged")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "View All Discount paginated by Wallet", description = "View All Discount paginated by Wallet",
            parameters = {
                    @Parameter(in = ParameterIn.QUERY
                            , description = "Page you want to retrieve (0..N)"
                            , name = "page"
                            , content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))),
                    @Parameter(in = ParameterIn.QUERY
                            , description = "Number of records per page."
                            , name = "size"
                            , content = @Content(schema = @Schema(type = "integer", defaultValue = "20"))),
            },
            security = @SecurityRequirement(name = "bearerAuth"), tags = {"Discount"})
    public ResponseEntity<MessageResponse> getAll(@PageableDefault @Parameter(hidden = true) Pageable pageable,
                                                  @RequestParam Long walletId) {
        return this.discountService.getAllDiscountPaginated(walletId, pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Add Discount", description = "Add Discount",
            security = @SecurityRequirement(name = "bearerAuth"), tags = {"Discount"})
    public ResponseEntity<MessageResponse> add(@RequestBody @Valid DiscountRequest discount,
                                               @RequestParam Long walletId){
        return this.discountService.addDiscount(discount, walletId);
    }
}
