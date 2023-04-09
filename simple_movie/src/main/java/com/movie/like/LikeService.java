package com.movie.like;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.movie.exception.like.LikeException;
import com.movie.like.domain.LikeDomain;
import com.movie.like.form.LikeForm;
import com.movie.like.repository.LikeRepository;

@Service
public class LikeService {

	@Autowired
	LikeRepository likeRepository;
	
	public List<LikeDomain> getAllLikeOfUser(String nickname) {
		List<LikeDomain> result = likeRepository.findAllByNickname(nickname);
		return result;
	}
	
	public LikeDomain isEnrollLike(LikeForm likeForm) {
		Optional<LikeDomain> like = likeRepository.findByNicknameAndTitleAndMovieSeqAndDocid(likeForm.getNickname(), likeForm.getTitle(), likeForm.getMovieSeq(), likeForm.getDocid());
		
		if(!like.isPresent()) {
			return null;
		}
		
		return like.get();
	}

	public void enrollLike(LikeForm likeForm) {
		if(likeRepository.findByNicknameAndTitleAndMovieSeqAndDocid(likeForm.getNickname(), likeForm.getTitle(), likeForm.getMovieSeq(), likeForm.getDocid()).isPresent()) {
			throw new LikeException("해당 영화에 이미 좋아요가 눌려있습니다");
		} else {
			LikeDomain like = new LikeDomain(likeForm);
			likeRepository.save(like);
		}
	}

	public void deleteLike(LikeForm likeForm) {
		Optional<LikeDomain> like = likeRepository.findByNicknameAndTitleAndMovieSeqAndDocid(likeForm.getNickname(), likeForm.getTitle(), likeForm.getMovieSeq(), likeForm.getDocid());
		if(!like.isPresent()) {
			throw new LikeException("해당 영화에 좋아요가 눌려있지 않습니다");
		} else {
			likeRepository.delete(like.get());
		}
	}
}
