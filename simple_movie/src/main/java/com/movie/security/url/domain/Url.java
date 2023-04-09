package com.movie.security.url.domain;

import com.movie.security.role.domain.Role;
import com.movie.security.url.form.UrlForm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Url {

	@Id @GeneratedValue
	private Long id;
	
	@Column(unique = true)
	private String url;
	
	private long orderNum;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id")
	private Role role;
	
	public Url(UrlForm urlForm, Role role) {
		setUrl(urlForm.getUrl());
		setOrderNum(urlForm.getOrderNum());
		System.out.println(urlForm.getOrderNum());
		setRole(role);
	}
	
	public Url(UrlForm urlForm) {
		setUrl(urlForm.getUrl());
		System.out.println(urlForm.getOrderNum());
		setOrderNum(urlForm.getOrderNum());
	}
}
