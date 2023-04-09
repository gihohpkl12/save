package com.movie.account;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.movie.account.domain.Account;
import com.movie.account.form.AccountForm;
import com.movie.account.form.AccountJoinForm;
import com.movie.account.form.PasswordChangeForm;
import com.movie.account.service.AccountService;
import com.movie.account.validator.AccountJoinFormValidator;
import com.movie.account.validator.PasswordChangeFormValidator;
import com.movie.exception.account.AccountException;
import com.movie.kmdb_moviedata.form.MovieForm;
import com.movie.like.LikeService;
import com.movie.like.domain.LikeDomain;
import com.movie.movie.MovieService;
import com.movie.util.LoginUserUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private LikeService likeService;
	
	@Autowired
	private MovieService movieService;
	
	@Autowired
	private AccountJoinFormValidator accountJoinFormValidator;
	
	@Autowired
	private PasswordChangeFormValidator passwordChangeFormValidator;

	private final String REDIRECT_URL = "redirect:/";
	private final String PASSWORD_CHANGE_URL = "password-change";
	private final String OLD_PASSWORD_CHANGE_URL = "old-password-change";
	private final String LOGIN_URL = "login";
	private final String JOIN_URL = "join";
	private final String MYPAGE_URL = "mypage";
	private final String LIST_URL = "list";
	private final String LOGOUT_URL = "logout";
	private final String WITHDRAW_URL = "withdraw";
	private final String FIND_NICKNAME_URL = "find-nickname";
	private final String FIND_PASSWORD_URL = "find-password";
	private final String PASSWORD_TOKEN_INPUT_URL = "password-token-input";
	
	@InitBinder("accountJoinForm")
	public void initJoinFormBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(accountJoinFormValidator);
    }
	
	@InitBinder("passwordChangeForm")
	public void initPasswordChangeFormBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(passwordChangeFormValidator);
    }
	
	@GetMapping("/login")
	public String login(@RequestParam(required =  false) String error, HttpServletRequest request, RedirectAttributes redirectModel, Model model) {
		if(LoginUserUtil.isLogin()) {
			redirectModel.addFlashAttribute("message", "로그아웃 후에 진행해주시기 바랍니다");
			return REDIRECT_URL;
		}
		
		if(error != null && !error.equals("")) {
			model.addAttribute("message", error);
		} 
		
		return LOGIN_URL;
	}
	
	@GetMapping("/join") 
	public String join(Model model, RedirectAttributes redirectModel) {
		if(LoginUserUtil.isLogin()) {
			redirectModel.addFlashAttribute("message", "로그아웃 후에 진행해주시기 바랍니다");
			return REDIRECT_URL;
		}
		
		model.addAttribute("AccountJoinForm", new AccountJoinForm());
		
		return JOIN_URL;
	}
	
	@GetMapping(value = {"/old-password-change", "/password-change"})
	public String calPasswordChangeView(Model model, HttpServletRequest request) {
		if(request.getRequestURI().equals("/old-password-change")) {
			model.addAttribute("mainTitleText", "비밀번호 변경 후 90일 이상 경과하였습니다.");
			model.addAttribute("buttonText", "30일 뒤에 변경하기");
			model.addAttribute("actionUrl", "/password-change");
			model.addAttribute("buttonUrl", "/skip-password-change");
		} else {
			model.addAttribute("mainTitleText", "비밀번호 변경");
			model.addAttribute("buttonText", "메인으로");
			model.addAttribute("actionUrl", "/password-change");
			model.addAttribute("buttonUrl", "/");
		}
		model.addAttribute("passwordChangeFrom", new PasswordChangeForm());
		
		return PASSWORD_CHANGE_URL;
	}
	
	@PostMapping("password-change")
	public String changePassword(@Validated @ModelAttribute PasswordChangeForm passwordChangeFrom, BindingResult bindingResult, RedirectAttributes redirectModel,  HttpServletResponse response, HttpServletRequest request) throws UnsupportedEncodingException {
		if(bindingResult.hasErrors()) {
			redirectModel.addFlashAttribute("message", getErrorMessages(bindingResult));
			if(request.getRequestURI().equals("/old-password-change")) {
				return REDIRECT_URL + OLD_PASSWORD_CHANGE_URL;
//				return "redirect:/old-password-change";
			}
			
			return REDIRECT_URL+PASSWORD_CHANGE_URL;
//			return "redirect:/password-change";
		}
		
		try {
			accountService.changePassword(passwordChangeFrom);
			redirectModel.addFlashAttribute("message", "비빌번호 변경 완료");
			String cookieName = URLEncoder.encode(LoginUserUtil.getUserNickname()+":passwordLastChangeDate", "UTF-8");
			Cookie cookie = new Cookie(cookieName, LocalDate.now().plusDays(90).toString());
			cookie.setMaxAge(7776000);
			response.addCookie(cookie);
		} catch (BadCredentialsException e) {
			redirectModel.addFlashAttribute("message", e.getMessage());
			if(request.getRequestURI().equals("/old-password-change")) {
				return REDIRECT_URL + OLD_PASSWORD_CHANGE_URL;
//				return "redirect:/old-password-change";
			}
			return REDIRECT_URL + PASSWORD_CHANGE_URL;
//			return "redirect:/password-change";
		} catch (AuthenticationServiceException e) {
			redirectModel.addFlashAttribute("message", e.getMessage());
			if(request.getRequestURI().equals("/old-password-change")) {
				return REDIRECT_URL + OLD_PASSWORD_CHANGE_URL;
//				return "redirect:/old-password-change";
			}
			return REDIRECT_URL + PASSWORD_CHANGE_URL;
//			return "redirect:/password-change";
		} catch (Exception e) {
			redirectModel.addFlashAttribute("message", "잠시후에 다시 시도해주시기 바랍니다.");
			if(request.getRequestURI().equals("/old-password-change")) {
				return REDIRECT_URL + OLD_PASSWORD_CHANGE_URL;
//				return "redirect:/old-password-change";
			}
			return REDIRECT_URL + PASSWORD_CHANGE_URL;
//			return "redirect:/password-change";
		}
		
		return REDIRECT_URL;
//		return "redirect:/";
	}
	
	@GetMapping("/skip-password-change")
	public String skipPasswordChange(HttpServletResponse response) throws UnsupportedEncodingException {
		if(LoginUserUtil.isLogin()) {
			String cookieName = URLEncoder.encode(LoginUserUtil.getUserNickname()+":passwordLastChangeDate", "UTF-8");
			Cookie cookie = new Cookie(cookieName, LocalDate.now().plusDays(90).toString());
			cookie.setMaxAge(7776000);
			response.addCookie(cookie);
		}
		return REDIRECT_URL;
//		return "redirect:/";
	}
	
	@PostMapping("/new-account")
	public String addAccount(@Validated @ModelAttribute AccountJoinForm newAccount, BindingResult bindingResult, HttpServletResponse response, RedirectAttributes model) {
		if(bindingResult.hasErrors()) {
			model.addFlashAttribute("message", getErrorMessages(bindingResult));
			return REDIRECT_URL+JOIN_URL;
//			return "redirect:/join";
		}
		
		try {
			accountService.checkBeforeAddAccount(newAccount);
			String cookieName = URLEncoder.encode(newAccount.getNickname()+":passwordLastChangeDate", "UTF-8"); 
			Cookie cookie = new Cookie(cookieName, LocalDate.now().plusDays(90).toString());
			cookie.setMaxAge(7776000);
			response.addCookie(cookie);
		} catch (AuthenticationServiceException e) {
			model.addFlashAttribute("message", e.getMessage());
			return REDIRECT_URL+JOIN_URL;
//			return "redirect:/join";
		} catch (Exception e) {
			System.out.println("message"+"잠시후에 다시 시도해주시기 바랍니다");
			return REDIRECT_URL+JOIN_URL;
//			return "redirect:/join";
		}
		
		return REDIRECT_URL;
//		return "redirect:/";
	}
	
	@GetMapping("/mypage")
	public String myPage(Model model, RedirectAttributes redirectModel) {
		if(LoginUserUtil.isLogin()) {
			Account account = LoginUserUtil.getAccount();
			account.setPassword(null);
			model.addAttribute("account", new AccountForm(account));
		}  else {
			redirectModel.addFlashAttribute("message", "잠시후에 다시 시도해주시기 바랍니다");
			return REDIRECT_URL;
//			return "redirect:/";
		}
		
		return MYPAGE_URL;
//		return "mypage";
	} 
	
	@GetMapping("/choice")
	public String getLikeMovie(Model model) {
		List<LikeDomain> likes = likeService.getAllLikeOfUser(LoginUserUtil.getUserNickname());
		ArrayList<MovieForm> result = new ArrayList<>();
		
		for(LikeDomain like : likes) {
			MovieForm movie = movieService.findMovie("title="+like.getTitle(), "movieSeq="+like.getMovieSeq(), "docid="+like.getDocid());
			
			if(movie != null) {
				result.add(movie);
			}
		}
		
		if(result == null || result.size() == 0) {
			model.addAttribute("movies", new ArrayList<>());
			model.addAttribute("title", "Choice");
			model.addAttribute("message", "Follow를 누른 영화가 없습니다");
		} else {
			model.addAttribute("movies", result);
			model.addAttribute("title", "Following Movie!");
		}
		
		return LIST_URL;
	}
	
	@GetMapping("withdraw")
	public String withdraw() {
		return WITHDRAW_URL;
//		return "withdraw";
	}
	
	/*
	 * 로그아웃 하는 법에 보면
	 * 세션 invalidate()하고, SecurityContextHolder에서 clearContext() 를 하거나,
	 * HttpServiletRequest에서 logout()을 호출하는 방법이 있는데
	 * 여기서 security의 기능을 그대로 이용하기 위해서 request에 응답 코드를 수정해서 post로 요청해서 '/logout'을 post로 redirect하는 방법을 씀
	 * 
	 * https://www.baeldung.com/spring-redirect-and-forward
	 * 8. Redirecting an HTTP POST Request 참고 
	 */
	@PostMapping("withdraw")
	public ModelAndView withdraw(ModelAndView modelAndView, String nickname, String password, RedirectAttributes redirectModel, HttpServletRequest request) {
		try {
			accountService.checkBeforeDeleteByUser(nickname, password);
			request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
			modelAndView.setViewName(REDIRECT_URL + LOGOUT_URL);
//			return new ModelAndView("redirect:/logout");
		} catch (AccountException e) {
			redirectModel.addFlashAttribute("message", e.getMessage());
			modelAndView.setViewName(REDIRECT_URL);
//			new ModelAndView("redirect:/");
		}
		
		return modelAndView;
//		return new ModelAndView("redirect:/");
	}
	
	@GetMapping("find/nickname")
	public String findNickname(RedirectAttributes redirectModel) {
		if(LoginUserUtil.isLogin()) {
			redirectModel.addFlashAttribute("message", "로그아웃 후에 진행해주시기 바랍니다");
			return REDIRECT_URL;
		}
		return FIND_NICKNAME_URL;
	}
	
	@PostMapping("/nickname-find")
	public String findNicknameByEmail(String email, RedirectAttributes redirectModel) {
		if(LoginUserUtil.isLogin()) {
			redirectModel.addFlashAttribute("message", "로그아웃 후에 진행해주시기 바랍니다");
			return REDIRECT_URL;
		}
		
		if(!AccountValidatorUtil.checkEmailPattern(email)) {
			redirectModel.addFlashAttribute("message", "Email 형식이 아닙니다");
			return REDIRECT_URL;
		}
		
		try {
			String nickname = accountService.getNickname(email);
			redirectModel.addFlashAttribute("message", "nickname : "+nickname);
		} catch (AccountException e) {
			redirectModel.addFlashAttribute("message", e.getMessage());
		} catch (Exception e) {
			redirectModel.addFlashAttribute("message", "잠시 후에 다시 시도해주시기 바랍니다");
		}
		
		return REDIRECT_URL;
	}
	
	@GetMapping("find/password")
	public String findPassword(RedirectAttributes redirectModel) {
		if(LoginUserUtil.isLogin()) {
			redirectModel.addFlashAttribute("message", "로그아웃 후에 진행해주시기 바랍니다");
			return REDIRECT_URL;
		}
		return FIND_PASSWORD_URL;
	}
	
	@PostMapping("/password-find")
	public String sendPasswordLink(String nickname, RedirectAttributes redirectModel, Model model) {
		if(LoginUserUtil.isLogin()) {
			redirectModel.addFlashAttribute("message", "로그아웃 후에 진행해주시기 바랍니다");
			return REDIRECT_URL;
		}
		try {
			accountService.createAndSendFindingPasswordToken(nickname);
			model.addAttribute("nickname", nickname);
		} catch(AccountException e) {
			redirectModel.addFlashAttribute("message", e.getMessage());
			return REDIRECT_URL;
		} catch (Exception e) {
			e.printStackTrace();
			redirectModel.addFlashAttribute("message", "잠시 후에 다시 시도해주시기 바랍니다");
			return REDIRECT_URL;
		}
		return PASSWORD_TOKEN_INPUT_URL;
	}
	
	@PostMapping("password-token")
	public String checkPasswordToken(String nickname, String token, RedirectAttributes redirectModel) {
		if(LoginUserUtil.isLogin()) {
			redirectModel.addFlashAttribute("message", "로그아웃 후에 진행해주시기 바랍니다");
			return REDIRECT_URL;
		}
		
		try {
			if(nickname != null && token != null && !token.equals("")) {
				accountService.checkPasswordToken(nickname, token);
				redirectModel.addFlashAttribute("message", "비밀번호가 0000으로 초기화 되었습니다");
			} else {
				redirectModel.addFlashAttribute("message", "올바른 토큰이 아닙니다");
			}
		} catch (AccountException e) {
			redirectModel.addFlashAttribute("message", e.getMessage());
		} catch (Exception e) {
			redirectModel.addFlashAttribute("message", "잠시 후에 다시 시도해주시기 바랍니다");
		}
		
		return REDIRECT_URL;
	}
	
	private String getErrorMessages(BindingResult bindingResult) {
		StringBuilder sb = new StringBuilder();
		
		for(ObjectError error : bindingResult.getAllErrors()) {
			sb.append(error.getDefaultMessage());
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
