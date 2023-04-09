package com.movie;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.movie.kmdb_moviedata.KmdbApi;
import com.movie.kmdb_moviedata.form.MovieForm;

@SpringBootTest
class SimpleMovieApplicationTests {

	@Autowired
	KmdbApi test;
	
//	@Test
//	void kmdbApiTest() throws IOException {
//		ArrayList<MovieForm> a =  (ArrayList)test.findByMovieTitle("아바타");
//		
//		if(a.size() !=0) {
//			
//			System.out.println(a.get(0).getMovieSeq());
//			System.out.println(a.get(0).getTitle());
//		} else {
//			
//		}
//		
//	}
	
	@Test
	void timeTest() {
//		LocalDateTime st = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));
//		System.out.println("st Time: "+st);
//		LocalDateTime next = LocalDateTime.now();
//		
//		System.out.println("plus hour "+st.plusHours(24));
//		
//		System.out.println("time sp?? "+LocalDate.now().minusMonths(1).toString());
//		
//		
//		
//		if(next.isAfter(st.plusHours(20))) {
//			System.out.println(" true ? " + st+" va "+next);
//		} else {
//			System.out.println(" false ? "+ st+" va "+next);
//		}
		
		LocalDate cur = LocalDate.now();
		LocalDate lastDate = LocalDate.parse(cur.toString(), DateTimeFormatter.ISO_DATE);
		System.out.println("cur "+cur.toString());
		System.out.println("last "+lastDate.toString());
		
		System.out.println(cur);
		System.out.println(lastDate);
	}

}
