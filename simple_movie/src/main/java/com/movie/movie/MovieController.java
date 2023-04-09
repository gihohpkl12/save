package com.movie.movie;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.movie.kmdb_moviedata.form.MovieForm;
import com.movie.like.LikeService;
import com.movie.like.domain.LikeDomain;
import com.movie.like.form.LikeForm;
import com.movie.movie.form.MovieDetailForm;
import com.movie.movie.form.MovieSearchForm;
import com.movie.movie.validator.MovieSearchValidator;
import com.movie.review.ReviewService;
import com.movie.review.domain.Review;
import com.movie.review.form.ReviewEnrollForm;
import com.movie.util.LoginUserUtil;

@Controller
public class MovieController {

	@Autowired
	private MovieService movieService;
	
	@Autowired
	private MovieSearchValidator movieSearchValidator;
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private LikeService likeService;
	
	@InitBinder("movieSearchForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(movieSearchValidator);
    }

	
	@GetMapping("/")
	public String home(@RequestParam(required = false) String error, Model model) {
		System.out.println("home!! "+error);
		ArrayList<Review> recentReviewMovies = (ArrayList<Review>)reviewService.getRecentReivews();
		ArrayList<MovieForm> recentMovies = (ArrayList<MovieForm>)movieService.getfindRecentMovies(false);
		
		if(recentMovies.size() > 0) {
			model.addAttribute("recentReviewMovie", recentReviewMovies);
		} else {
			model.addAttribute("recentReviewMovie", new ArrayList<>());
		}
		
		if(recentMovies.size() > 0) {
			model.addAttribute("recentMovieList", recentMovies);
		} else {
			model.addAttribute("recentMovieList", new ArrayList<>());
		}
		
		if(error != null && !error.equals("")) {
			model.addAttribute("message", error);
		}
		
		model.addAttribute("movieSearchForm", new MovieSearchForm());
		return "main";
	}
	

	@GetMapping("/details")
	public String movieDetails(@Validated MovieDetailForm movieDetailForm, BindingResult bindingResult, Model model, RedirectAttributes redirectModel)  {
		
		if(bindingResult.hasErrors()) {
			redirectModel.addFlashAttribute("message", "영화 정보가 없습니다");
			for(ObjectError error : bindingResult.getAllErrors()) {
				System.out.println(error.getDefaultMessage());
			}
			return "redirect:/";
		}

		ArrayList<Review> reviews = (ArrayList<Review>)reviewService.findReviewsByTitleAndMovieSeqAndDocid(movieDetailForm.getTitle(), movieDetailForm.getMovieSeq(), movieDetailForm.getDocid());
		MovieForm movie = (MovieForm) movieService.findMovie("title="+movieDetailForm.getTitle(), "movieSeq="+ movieDetailForm.getMovieSeq(), "docid="+ movieDetailForm.getDocid());
		
		if(movie == null) {
			redirectModel.addFlashAttribute("message", "영화 정보가 없습니다");
			return "redirect:/";
		}
		model.addAttribute("movie", movie);
		
		if(reviews.size() > 0) {
			model.addAttribute("reviews", reviews);
		} else {
			model.addAttribute("reviews", new ArrayList<>());
		}
		
		String nickname = LoginUserUtil.getUserNickname();
		LikeDomain like = likeService.isEnrollLike(new LikeForm(movieDetailForm.getTitle(), movieDetailForm.getMovieSeq(), movieDetailForm.getDocid(), nickname));
		
		if(like != null) {
			model.addAttribute("like", "FOLLOWING");
		} else {
			model.addAttribute("like", "FOLLOW");
		}
		
		model.addAttribute("reviewEnrollForm", new ReviewEnrollForm());
		return "detail";
	}
	
	@PostMapping("/search")
	public String search(@Validated MovieSearchForm movieSearchForm, BindingResult bindingResult, RedirectAttributes model) throws UnsupportedEncodingException {
		
		if(bindingResult.hasErrors()) {
			model.addFlashAttribute("message", bindingResult.getGlobalError().getDefaultMessage());
			return "redirect:/";
		}
		
		StringBuilder sb = new StringBuilder();
		boolean appendCharCheck = true;
		
		sb.append("redirect:/search-result");
		if(movieSearchForm.getActor() != null && !movieSearchForm.getActor().equals("")) {
			if(appendCharCheck) {
				sb.append("?");
				appendCharCheck = false;
			}
			sb.append("actor="+URLEncoder.encode(movieSearchForm.getActor(), "UTF-8"));
		}
		
		if(movieSearchForm.getTitle() != null && !movieSearchForm.getTitle().equals("")) {
			if(appendCharCheck) {
				sb.append("?");
				appendCharCheck = false;
			} else {
				sb.append("&");
			}
			sb.append("title="+URLEncoder.encode(movieSearchForm.getTitle(), "UTF-8"));
		}
		
		if(movieSearchForm.getDirector() != null && !movieSearchForm.getDirector().equals("")) {
			if(appendCharCheck) {
				sb.append("?");
				appendCharCheck = false;
			} else {
				sb.append("&");
			}
			sb.append("director="+URLEncoder.encode(movieSearchForm.getDirector(), "UTF-8"));
		}
		
		return sb.toString();
	}
	
	@GetMapping("/search-result")
	public String searchResult(@Validated MovieSearchForm movieSearchForm, BindingResult bindingResult, Model model, RedirectAttributes redirectModel) {
	
		if(bindingResult.hasErrors()) {
			redirectModel.addFlashAttribute("redirectModel"+bindingResult.getGlobalError().getDefaultMessage());
			return "redirect:/";
		} 
		
		ArrayList<MovieForm> result  = (ArrayList<MovieForm>)movieService.findMovies("title="+movieSearchForm.getTitle(), "actor="+movieSearchForm.getActor(), "director="+movieSearchForm.getDirector());
		
		if(result == null || result.size() == 0) {
			model.addAttribute("movies", new ArrayList<>());
			model.addAttribute("title", "Search Result");
			model.addAttribute("message", "검색 결과가 없습니다.");
		} else {
			model.addAttribute("movies", result);
			model.addAttribute("title", "Search Result");
		}
				
		return "list";
	}
	
	@GetMapping("all-recent-movie")
	public String searchAllRecentMovie(Model model) {
		model.addAttribute("movies", movieService.getfindRecentMovies(true));
		model.addAttribute("title", "Search Result");
		return "list";
	}
}
