package com.movie.account.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccountJoinForm {
	
	@NotBlank(message = "이메일을 입력해주세요")
	private String email;
	
	@NotBlank(message = "닉네임을 입력해주세요")
	private String nickname;
	
	@NotBlank(message = "패스워드를 입력해주세요")
	private String password;
	
	@NotBlank(message = "패스워드가 동일하지 않습니다")
	private String repeatPassword;

	private String role;
}
