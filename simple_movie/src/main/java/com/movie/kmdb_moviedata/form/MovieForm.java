package com.movie.kmdb_moviedata.form;

import java.util.List;
import lombok.Data;

@Data
public class MovieForm {
	
	private String title;
	private String docid;
	private String movieSeq;
	private String titleEng;
	private String prodYear;
	
	private String genre;
	private String ratingDate;
	private String ratingGrade;
	private String nation;
	private String plot;
	
	private String runtime;
	private String company;
	private List<String> posters;
	private List<String> directors;
	private List<String> actors;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("title="+title);
		sb.append("&docid="+docid);
		sb.append("&titleEng="+titleEng);
		sb.append("&prodYear="+prodYear);
		sb.append("&genre="+genre);
		sb.append("&ratingDate="+ratingDate);
		sb.append("&ratingGrade="+ratingGrade);
		sb.append("&nation="+nation);
		sb.append("&plot="+plot);
		sb.append("&runtime="+runtime);
		sb.append("&company="+company);
		
		sb.append("&posters=");
		for(int i = 0; i < posters.size(); i++) {
			sb.append(posters.get(i));
			if(i != posters.size()-1) {
				sb.append("?");
			}
		}
		
		sb.append("&directors=");
		for(int i = 0; i < directors.size(); i++) {
			sb.append(directors.get(i));
			if(i != directors.size()-1) {
				sb.append("?");
			}
		}
		
		sb.append("&actors=");
		for(int i = 0; i < actors.size(); i++) {
			sb.append(actors.get(i));
			if(i != actors.size()-1) {
				sb.append("?");
			}
		}
		
		return sb.toString();
	}
}