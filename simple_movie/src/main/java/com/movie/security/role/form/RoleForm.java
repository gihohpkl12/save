package com.movie.security.role.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleForm {

	private Long id;
	@NotBlank(message = "권한명이 null입니다")
	private String roleName;
	@NotBlank(message = "Level이 null입니다")
	private long level;
	boolean isEdit;
}
