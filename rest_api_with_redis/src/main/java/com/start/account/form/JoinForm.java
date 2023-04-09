package com.start.account.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinForm {

	@NotBlank(message = "닉네임을 입력해주세요")
	private String nickname;
	@NotBlank(message = "비밀번호를 입력해주세요")
	private String password;
	@NotBlank(message = "비밀번호를 한 번 더 입력해주세요")
	private String passwordRepeat;
	@NotBlank(message = "이메일을 입력해주세요")
	private String email;
}
