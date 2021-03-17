package com.jump.model;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BankAccount {

	@Setter
	@Getter
	private String accountId;

	@Setter
	@Getter
	private String accountName;

	@Setter
	@Getter
	private EnumAccountType accountType;

	@Setter
	@Getter
	private BigDecimal accountBlance;
}
