package com.movie.review;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.movie.exception.review.ReviewException;
import com.movie.review.domain.Review;
import com.movie.review.form.ReviewDeleteForm;
import com.movie.review.form.ReviewEnrollForm;
import com.movie.review.repository.ReviewRepository;

@Service
public class ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;
	
	public void enrollReview(ReviewEnrollForm reviewEnrollForm) {
		try {
			Review review = new Review(reviewEnrollForm);
			reviewRepository.save(review);
		} catch (Exception e) {
			throw new ReviewException("리뷰 등록에 실패하였습니다");
		}
	}
	
	public List<Review> getRecentReivews() {
		return reviewRepository.findTop5ByOrderByCreateDateDesc();
	}
	
	public List<Review> findReviewsByTitleAndMovieSeqAndDocid(String title, String movieSeq, String docid) {
		return reviewRepository.findByTitleAndMovieSeqAndDocidOrderByCreateDateDesc(title, movieSeq, docid);
	}

	public void deleteReview(ReviewDeleteForm reviewDeleteForm) {
		try {
			Review review = reviewRepository.findById(reviewDeleteForm.getId()).get();
			
			if(review.getNickname().equals(reviewDeleteForm.getNickname())) {
				reviewRepository.delete(review);
			} else {
				throw new ReviewException("리뷰를 등록한 사용자가 삭제할 수 있습니다");
			}
		} catch (NoSuchElementException e) {
			throw new ReviewException("해당 리뷰가 존재하지 않습니다");
		}
	}
}


