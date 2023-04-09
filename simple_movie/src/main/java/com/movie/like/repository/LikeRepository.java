package com.movie.like.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movie.like.domain.LikeDomain;

public interface LikeRepository extends JpaRepository<LikeDomain, Long>{

	Optional<LikeDomain> findByNicknameAndTitleAndMovieSeqAndDocid(String nickname, String title, String movieSeq, String docid);
	
	List<LikeDomain> findAllByNickname(String nickname);
	
	void deleteAllByNickname(String nickname);
}
