package com.jump.rest;


import com.jump.model.BankAccount;
import com.jump.service.BankAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@Slf4j
public class BankAccountController {

	@Autowired
	public BankAccountService bankAccountService;
	
	
	@PostMapping("/worker")
	public ResponseEntity<?> createBankAccount(@RequestBody BankAccount bankAccount, HttpServletRequest request) throws URISyntaxException {
		
		bankAccountService.createBankAccount(bankAccount);
		
		log.info("created bank account {}", bankAccount);
		
		URI uri = new URI(request.getRequestURL() + "worker/" + bankAccount.getAccountId());
		
		return ResponseEntity.created(uri).build();				
	}
	
	
	@GetMapping("/worker/{accountId}")
	public ResponseEntity<BankAccount> getBankAccount(@PathVariable("accountId") String accountId){
		
		BankAccount account = bankAccountService.retrieveBankAccount(accountId);
		
		log.info("retrieved bank account {}", account);
		
		return ResponseEntity.ok(account);				
	}
	
}
