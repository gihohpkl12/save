package com.movie.review.domain;

import java.time.LocalDateTime;
import com.movie.review.form.ReviewEnrollForm;
import com.movie.review.form.ReviewDeleteForm;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Review {

	@Id @GeneratedValue
	private Long id;
	@Column(nullable = false)
	private String content;
	@Column(nullable = false)
	private String title;
	@Column(nullable = false)
	private String docid;
	@Column(nullable = false)
	private String movieSeq;
	@Column(nullable = false)
	private String nickname;
	@Column(nullable = false)
	private LocalDateTime createDate;
	
	public Review(ReviewDeleteForm reviewDeleteForm) {
		setTitle(reviewDeleteForm.getTitle());
		setDocid(reviewDeleteForm.getDocid());
		setMovieSeq(reviewDeleteForm.getMovieSeq());
		setNickname(reviewDeleteForm.getNickname());
	}
	
	public Review(ReviewEnrollForm reviewEnrollForm) {
		setContent(reviewEnrollForm.getContent());
		setTitle(reviewEnrollForm.getTitle());
		setDocid(reviewEnrollForm.getDocid());
		setMovieSeq(reviewEnrollForm.getMovieSeq());
		setNickname(reviewEnrollForm.getNickname());
		setCreateDate(LocalDateTime.now());
	}
	
}
