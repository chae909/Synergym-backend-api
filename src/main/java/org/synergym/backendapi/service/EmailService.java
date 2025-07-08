package org.synergym.backendapi.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public void sendVerificationEmail(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[SynergyM] 이메일 인증 코드 안내");
        message.setText("인증 코드: " + verificationCode + "\n\n이 코드를 5분 내에 입력해주세요.");

        try {
            javaMailSender.send(message);
            log.info("{} 주소로 인증 이메일 발송 성공", to);
        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", e.getMessage());
            throw new RuntimeException("이메일 발송에 실패했습니다.");
        }
    }

    /**
     * 강제탈퇴 안내 이메일을 발송합니다.
     * @param to 수신자 이메일 주소
     */
    @Async
    public void sendForcedWithdrawalEmail(String to, String nickname, String reason) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            // 1. Thymeleaf 컨텍스트 생성 및 데이터 추가
            Context context = new Context();
            context.setVariable("nickname", nickname);
            context.setVariable("reason", reason);

            // 2. 템플릿 처리하여 HTML 생성
            String html = templateEngine.process("emails/forced-withdrawal", context);

            // 3. 이메일 메시지 설정
            helper.setTo(to);
            helper.setSubject("[SynergyM] 계정 비활성화 안내");
            helper.setText(html, true); // true는 HTML 메일임을 의미

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            // 예외 처리
            log.error("HTML 이메일 발송 실패", e);
        }
    }
}
