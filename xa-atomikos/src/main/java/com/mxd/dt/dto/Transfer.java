package com.mxd.dt.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@Data
@Builder
public class Transfer {

    @NotEmpty(message = "Bank Code is Required.")
    private String bankCode;

    @NotEmpty(message = "Account Number is Required.")
    private String accountNumber;

    @Min(1)
    private BigDecimal amount;

}
