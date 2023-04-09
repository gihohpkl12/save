package com.movie.kmdb_moviedata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.movie.kmdb_moviedata.form.MovieForm;

@Component
public class KmdbApi {
	
	// Component로 등록 && 주입 받아야 null이 아님
	@Value("${kmdb.api.key}")
	private String key;

	@Value("${kmdb.api.url}")
	private String url;

	public List<MovieForm> findByMovieTitle(String title) {
		List<MovieForm> results = new ArrayList<>();
		StringBuilder kmdbUrl = makeUrl(true);
		
		try {
			kmdbUrl.append("title="+URLEncoder.encode(title,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			// 여기 exception 하나 추가
		}
		
		results = getMovieData(kmdbUrl.toString());
		
		return results;
	}
	
	public List<MovieForm> findByMovieSeq(String seq) {
		List<MovieForm> results = new ArrayList<>();
		StringBuilder kmdbUrl = makeUrl(true);
		
		try {
			kmdbUrl.append("seq="+URLEncoder.encode(seq,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			// 여기 exception 하나 추가
		}
		
		results = getMovieData(kmdbUrl.toString());
		
		return results;
	}
	
	public List<MovieForm> findByMultiConditions(List<String> querys) {
		List<MovieForm> results = new ArrayList<>();
		StringBuilder kmdbUrl = makeUrl(true);
		
		try {
			for(int i = 0; i < querys.size(); i++) {
				String[] temp = querys.get(i).split("=");
				if(temp.length > 1) {
					if(!temp[1].equals("null")) {
						kmdbUrl.append(temp[0] + "=" + URLEncoder.encode(temp[1],"UTF-8"));
						
						if(i != querys.size()-1) {
							kmdbUrl.append("&");
						}
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			// 여기 exception 하나 추가
		}
		System.out.println("chekc "+kmdbUrl.toString());
		results = getMovieData(kmdbUrl.toString());
		
		return results;
	}
	
	public StringBuilder makeUrl(boolean details){
		StringBuilder sb = new StringBuilder();

		sb.append(url);
		if (details) {
			sb.append("Y&ServiceKey=");
		} else {
			sb.append("N&ServiceKey=");
		}
		sb.append(key);
		sb.append("&");

		return sb;
	}
	
	public HttpURLConnection connection(String kmdbUrl) throws IOException {
		URL connectionUrl = new URL(kmdbUrl);
		HttpURLConnection con = (HttpURLConnection) connectionUrl.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Content-type", "application/json");
		

		return con;
	}

	public StringBuilder readData(HttpURLConnection con) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;

		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();

		return sb;
	}
	
	public List<MovieForm> getMovieData(String url) {
		ArrayList<MovieForm> results = new ArrayList<>();
//		String kmdbUrl = makeUrl(querys, true);
		
		HttpURLConnection con;
		
		try {
			con = connection(url);
			if (con.getResponseCode() == con.HTTP_OK) {
				/*
				 * KMDB JSON 구조 total - JSONArray : data - JSONArray : result
				 */
				StringBuilder sb = readData(con);
				JSONObject totalData = JSONObject.fromObject(sb.toString());
				JSONArray data = totalData.getJSONArray("Data");
				JSONArray resultData = data.getJSONObject(0).getJSONArray("Result");
				
				System.out.println("result data size "+resultData.size());
				for (int i = 0; i < resultData.size(); i++) {
					MovieForm movie = new MovieForm();
					JSONObject getData = resultData.getJSONObject(i);

					movie.setDocid(getData.getString("DOCID"));
					if(movie.getDocid() == null || movie.getDocid().equals("")) {
						movie.setDocid("-");
					}
					
					movie.setMovieSeq(getData.getString("movieSeq"));
					if(movie.getMovieSeq() == null || movie.getMovieSeq().equals("")) {
						movie.setMovieSeq("-");
					}
					
					movie.setProdYear(getData.getString("prodYear"));
					if(movie.getProdYear() == null || movie.getProdYear().equals("")) {
						movie.setProdYear("-");
					}
					
					movie.setTitleEng(getData.getString("titleEng"));
					if(movie.getTitleEng() == null || movie.getTitleEng().equals("")) {
						movie.setTitleEng("-");
					}
					
					movie.setNation(getData.getString("nation"));
					if(movie.getNation() == null || movie.getNation().equals("")) {
						movie.setNation("-");
					}
					
					movie.setCompany(getData.getString("company"));
					if(movie.getCompany() == null || movie.getCompany().equals("")) {
						movie.setCompany("-");
					}
					
					movie.setPlot(getData.getString("plot"));
					if(movie.getPlot() == null || movie.getPlot().equals("")) {
						movie.setPlot("-");
					}
					
					movie.setRuntime(getData.getString("runtime"));
					if(movie.getRuntime() == null || movie.getRuntime().equals("")) {
						movie.setRuntime("-");
					}
					
					movie.setGenre(getData.getString("genre").replace(",", ", "));
					if(movie.getGenre() == null || movie.getGenre().equals("")) {
						movie.setGenre("-");
					}
					
					// 상관 없는 문자열 제거.
					String title = getData.getString("title");
					title = title.replaceAll(" !HS ", "");
					title = title.replaceAll(" !HS", "");
					title = title.replaceAll("!HS ", "");
					title = title.replaceAll(" !HE ", "");
					title = title.replaceAll(" !HE", "");
					title = title.replaceAll("!HE", "");
					
					if(title.charAt(0)==' ') {
						title = title.substring(1, title.length());
					}
					if(title.charAt(title.length()-1) == ' ') {
						title = title.substring(0, title.length()-1);
					}
					movie.setTitle(title);
					if(movie.getTitle() == null || movie.getTitle().equals("")) {
						movie.setTitle("-");
					}

					ArrayList<String> directors = new ArrayList<>();
					JSONArray directorData = getData.getJSONArray("director");
					for (int j = 0; j < directorData.size(); j++) {
						directors.add(directorData.getJSONObject(j).getString("directorNm"));
					}
					if(directors.size() == 0) {
						directors.add("-");
					}
					movie.setDirectors(directors);
					
					ArrayList<String> actors = new ArrayList<>();
					JSONArray actorData = getData.getJSONArray("actor");
					for (int j = 0; j < actorData.size(); j++) {
						actors.add(actorData.getJSONObject(j).getString("actorNm"));
					}
					if(actors.size() == 0) {
						actors.add("-");
					}
					movie.setActors(actors);

					JSONArray ratingData = getData.getJSONArray("rating").getJSONArray(1);
					movie.setRatingGrade(ratingData.getJSONObject(0).getString("ratingGrade"));
					movie.setRatingDate(ratingData.getJSONObject(0).getString("ratingDate"));
					
					if(movie.getRatingDate() == null || movie.getRatingDate().equals("")) {
						movie.setRatingDate("-");
					}
					
					if(movie.getRatingGrade() == null || movie.getRatingGrade().equals("")) {
						movie.setRatingGrade("-");
					}
					
					String[] temp = getData.getString("posters").split("\\|");
					ArrayList<String> posters = new ArrayList<>();
					for (int j = 0; j < temp.length; j++) {
						posters.add(temp[j]);
					}
					
					if(posters.size() == 0) {
						posters.add("img/NoData.PNG");
					} else if(posters.size() == 1 && posters.get(0).equals("")) {
						posters.remove(0);
						posters.add("img/NoData.PNG");
					}
					movie.setPosters(posters);

					results.add(movie);
				}
			} 
		} catch (JSONException e) {
			return new ArrayList<MovieForm>();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return results;
	}
}
