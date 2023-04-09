package com.movie.review.form;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewEnrollForm {

	@NotBlank(message = "내용을 입력해주세요")
	private String content;
	@NotBlank
	private String title;
	@NotBlank
	private String docid;
	@NotBlank
	private String movieSeq;
	
	private String nickname;
	private LocalDate createDate;
}
