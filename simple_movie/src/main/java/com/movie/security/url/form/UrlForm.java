package com.movie.security.url.form;

import java.util.List;

import com.movie.security.role.domain.Role;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UrlForm {

	private Long id;
	@NotBlank(message = "URL이 null입니다")
	private String url;
	private long orderNum;
	@NotBlank(message = "권한명이 null입니다")
	private String roleName;
	private boolean isEdit;
	
}
