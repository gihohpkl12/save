package com.movie.movie;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.movie.kmdb_moviedata.KmdbApi;
import com.movie.kmdb_moviedata.form.MovieForm;

@Component
public class MovieService {

	@Autowired
	private KmdbApi kmdbApi;

	private List<MovieForm> recentMovieList = new ArrayList<>();
	private List<MovieForm> allRecentMovieList = new ArrayList<>();
	
	final String NO_POSTER = "img/NoData.PNG";

	private LocalDateTime standardTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));

	synchronized public List<MovieForm> getfindRecentMovies(boolean gettingAllMovies) {
		
		if(LocalDateTime.now().isAfter(standardTime.plusHours(24)) || recentMovieList.size() == 0 || allRecentMovieList.size() == 0) {
			standardTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));
			setRecentMovies();
		}
		
		if(gettingAllMovies) {
			return allRecentMovieList;
		}
		
		return recentMovieList;
	}
	
	public MovieForm findMovie(String title, String seq, String docid) {
		
		ArrayList<MovieForm> result = (ArrayList<MovieForm>)kmdbApi.findByMultiConditions(new ArrayList<>(Arrays.asList(title, seq, docid)));
		return result.get(0);
	}
	
	public List<MovieForm> findMovies(String title, String actor, String director) {
		
		ArrayList<MovieForm> result = (ArrayList<MovieForm>)kmdbApi.findByMultiConditions(new ArrayList<>(Arrays.asList(title, actor, director, "listCount=50")));
		return result;
	}

	public String getRecentMoviesDate() {
		return LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1)
				.minusMonths(3).toString().replace("-", "");
	}

	public void setRecentMovies() {
		ArrayList<MovieForm> result = new ArrayList<>();
		ArrayList<String> querys = new ArrayList<>();
		querys.add("releaseDts=" + getRecentMoviesDate());
		querys.add("listCount=50");
		querys.add("sort=prodYear");
		
		if(recentMovieList.size() != 0) {
			recentMovieList.clear();
		}
		
		if(allRecentMovieList.size() != 0) {
			allRecentMovieList.clear();
		}

		result = (ArrayList<MovieForm>)kmdbApi.findByMultiConditions(querys);
		allRecentMovieList = result;
		for(int i = 0; i < result.size(); i++) {
			if(recentMovieList.size() >= 12) {
				break;
			}
			
			if(result.get(i).getPosters().get(0).equals(NO_POSTER)) {
				continue;
			}
			
			recentMovieList.add(result.get(i));
		}
	}
}
