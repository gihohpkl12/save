package com.movie.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movie.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>{

	List<Review> findByTitleAndMovieSeqAndDocidOrderByCreateDateDesc(String title, String movieSeq, String docid);
	
	List<Review> findTop5ByOrderByCreateDateDesc();
	
	Optional<Review> findById(Long id);
	
	void deleteAllByNickname(String nickname);
}
