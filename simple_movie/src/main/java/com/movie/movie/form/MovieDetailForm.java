package com.movie.movie.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MovieDetailForm {

	@NotBlank(message = "title null")
	private String title;
	@NotBlank(message = "movieSeq null")
	private String movieSeq;
	@NotBlank(message = "docid null")
	private String docid;
}
