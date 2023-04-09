package com.start.account;

import java.io.Serializable;

import com.start.account.form.JoinForm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Account  {

	@Id @GeneratedValue
	private Long id;
	@Column(unique = true)
	private String nickname;
	@Column(unique = true)
	private String email;
	private String password;
	private String role;
	
	Account(JoinForm joinForm, String role) {
		setEmail(joinForm.getEmail());
		setNickname(joinForm.getNickname());
		setPassword(joinForm.getPassword());
		setRole(role);
	} 
}
