package com.start.account.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginForm {

	@NotBlank(message = "nickname이 null입니다")
	private String nickname;
	
	@NotBlank(message = "password가 null입니다")
	private String password;
}
