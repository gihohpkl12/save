package com.movie.mail;

import lombok.Data;

@Data
public class EmailMessage {

	private String to;
	private String subject;
	private String message;
}