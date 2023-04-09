package com.movie.like.domain;

import com.movie.like.form.LikeForm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class LikeDomain {

	@Id @GeneratedValue
	private Long id;
	
	@Column(nullable = false)
	private String nickname;
	
	@Column(nullable = false)
	private String docid;
	
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	private String movieSeq;
	
	public LikeDomain(LikeForm likeForm) {
		setNickname(likeForm.getNickname());
		setTitle(likeForm.getTitle());
		setMovieSeq(likeForm.getMovieSeq());
		setDocid(likeForm.getDocid());
	}
}
