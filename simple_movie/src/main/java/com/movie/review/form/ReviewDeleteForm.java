package com.movie.review.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewDeleteForm {

	@NotNull
	private Long id;
	@NotBlank
	private String movieSeq;
	@NotBlank
	private String title;
	@NotBlank
	private String docid;
	@NotBlank
	private String nickname;
	
}
