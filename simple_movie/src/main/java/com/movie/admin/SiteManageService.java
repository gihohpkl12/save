package com.movie.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class SiteManageService {

	public void reloadSiteAndUrl(HttpServletRequest request) {
		String DEFAULT_CSRF_TOKEN_ATTR_NAME = HttpSessionCsrfTokenRepository.class.getName().concat(".CSRF_TOKEN");
		CsrfToken csrfToken = (CsrfToken) request.getSession().getAttribute(DEFAULT_CSRF_TOKEN_ATTR_NAME);
		
		try {
			URL connectionUrl = new URL("http://localhost:8080/authority-reload");
			HttpURLConnection con = (HttpURLConnection) connectionUrl.openConnection();
			
			CookieCsrfTokenRepository a = new CookieCsrfTokenRepository();
			
			System.out.println(csrfToken.getToken());
//			CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
			con.setRequestMethod("POST");
//			con.setRequestMethod("GET");
			con.setRequestProperty("Content-type", "application/json");
			con.setRequestProperty("X-CSRF-HEADER", "X-XSRF-TOKEN");
			con.setRequestProperty("X-CSRF-PARAM", "_csrf");
			con.setRequestProperty("X-CSRF-TOKEN", csrfToken.getToken());
			con.setDoOutput(true);
			
//			JSONObject data = new JSONObject();
//			data.put("nickname", LoginUserUtil.getUserNickname());
//			data.put("password", LoginUserUtil.getAccount().getPassword());
//			
//			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
//            wr.write(data.toString());
//            wr.flush();
            
            StringBuilder sb = new StringBuilder();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
            } 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
