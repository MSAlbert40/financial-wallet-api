package com.evertix.financialwallet.model.request;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class WalletRequest {
    @NotNull(message = "Currency cannot be null")
    @NotBlank(message = "Currency cannot be blank")
    @Size(max = 30)
    private String currency;
}
