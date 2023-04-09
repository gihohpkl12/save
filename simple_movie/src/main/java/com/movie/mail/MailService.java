package com.movie.mail;


import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.movie.exception.MailException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Data
public class MailService {
	
//	@Value("${app.host}")
//	private String host;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private MailTokenRepository mailTokenRepository;
	
	public String createToken() {
		return UUID.randomUUID().toString();
	}

    public void sendEmail(EmailMessage emailMessage, String nickname, String token) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
			// 두번째 인자는 이메일에 첨부파일을 넣을 것이면 true
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
			mimeMessageHelper.setTo(emailMessage.getTo());
			mimeMessageHelper.setSubject(emailMessage.getSubject());
			// 2번째 인자는 html을 사용할 것인지 여부. 쓸거면 true
			mimeMessageHelper.setText(emailMessage.getMessage(), true);
			log.info("send email: {}", emailMessage.getMessage());
			javaMailSender.send(mimeMessage);
			
			MailToken mail = new MailToken();
//			mail.setEmailToken(host);
			mail.setNickname(nickname);
			mail.setEmailToken(token);
			mailTokenRepository.save(mail);
			
		} catch (MessagingException e) {
			log.error("메일 전송 실패");
			throw new MailException(e.getMessage());
		}
    }
    
    public boolean checkTokenAndNickname(String nickname, String token) {
    	Optional<MailToken> mailToken = mailTokenRepository.findByNicknameAndEmailToken(nickname, token);
    	
    	if(!mailToken.isPresent()) {
    		mailTokenRepository.deleteByNickname(nickname);
    		return false;
    	}
    	
    	deleteToken(mailToken.get());
    	return true;
    }

	private void deleteToken(MailToken mailToken) {
		mailTokenRepository.delete(mailToken);
	}
}
