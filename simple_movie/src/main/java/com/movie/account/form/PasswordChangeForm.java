package com.movie.account.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordChangeForm {
	
	@NotBlank(message = "비밀번호를 입력해주세요")
	private String password;
	
	@NotBlank(message = "새로운 비밀번호를 입력해주세요")
	private String newPassword;
	
	@NotBlank(message = "새로운 비밀번호가 일치하지 않습니다")
	private String repeatPassword;

}
