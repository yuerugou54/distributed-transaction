package com.mxd.dt.controller;

import com.mxd.dt.dao.AccountDAO;
import com.mxd.dt.dto.Transfer;
import com.mxd.dt.entity.Account;
import com.mxd.dt.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController()
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private AccountService accountService;

    @Value("${bank.code}")
    @Setter
    private String defaultBankCode;

    @GetMapping
    @Operation(summary = "retrieve account details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account Found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Account.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request.", content = @Content),
            @ApiResponse(responseCode = "404", description = "No account found.", content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Account> getAccount(@RequestParam("account-number") String accountNumber) {
        return ResponseEntity.of(accountDAO.findById(accountNumber));
    }

    @PostMapping
    @Operation(summary = "create a new account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully Created.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Account.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Account Number Exists.", content = @Content)
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Account> createAccount(@Valid @RequestBody Account account) {

        if (accountDAO.findById(account.getNumber()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        account.setBankCode(defaultBankCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountDAO.save(account));
    }

    @RequestMapping("/{account-number}/direct-transfer")
    @PutMapping
    @Operation(summary = "create a new account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Created.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Account.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account Number NotFound.", content = @Content),
            @ApiResponse(responseCode = "417", description = "Insufficient Funds.", content = @Content)
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Account> transfer(@PathVariable(value = "account-number", required = true) String accountNumber,
                                            @Valid @RequestBody Transfer transfer) {

        if (!accountDAO.findById(accountNumber).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(accountService.directTransfer(accountNumber, transfer));
    }

    @RequestMapping("/{account-number}/direct-deposit")
    @PutMapping
    @Operation(summary = "create a new account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Created.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Account.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request.", content = @Content),
            @ApiResponse(responseCode = "420", description = "Account Number NotFound.", content = @Content)
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Account> deposit(@PathVariable(value = "account-number", required = true) String accountNumber,
                                           @Valid @RequestBody Transfer transfer) {

        if (!accountDAO.findById(accountNumber).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(accountService.directDeposit(accountNumber, transfer));
    }


}
