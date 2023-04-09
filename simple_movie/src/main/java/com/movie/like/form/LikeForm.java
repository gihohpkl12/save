package com.movie.like.form;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeForm {

	@NotBlank
	private String title;
	@NotBlank
	private String movieSeq;
	@NotBlank
	private String docid;
	@NotBlank
	private String nickname;
	
}
