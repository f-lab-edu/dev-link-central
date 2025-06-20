package dev.member.service;

import dev.member.entity.Member;
import dev.member.exception.MemberError;
import dev.member.exception.MemberException;
import dev.member.service.dto.MailCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MailService {

    private static final String FROM_ADDRESS = "alstjr706@gmail.com";

    private final MailSender mailSender;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void sendPasswordResetMail(String userEmail) {
        String tempPassword = generateTemporaryPassword();
        updateToTemporaryPassword(userEmail, tempPassword);

        MailCommand mailCommand = buildPasswordResetMailCommand(userEmail, tempPassword);
        sendMail(mailCommand);
    }

    public void sendMail(MailCommand mailCommand) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailCommand.getAddress());
        message.setFrom(FROM_ADDRESS);
        message.setSubject(mailCommand.getTitle());
        message.setText(mailCommand.getMessage());

        try {
            mailSender.send(message);
        } catch (MailException ex) {
            throw new MemberException(MemberError.MAIL_SEND_FAIL);
        }
    }

    @Transactional
    public void updateToTemporaryPassword(String userEmail, String temporaryPassword) {
        Member member = memberService.findByEmail(userEmail);
        member.changePassword(temporaryPassword, passwordEncoder);
    }

    private MailCommand buildPasswordResetMailCommand(String userEmail, String tempPassword) {
        return MailCommand.builder()
                .address(userEmail)
                .title("임시비밀번호 안내 이메일 입니다.")
                .message(passwordResetMessage(tempPassword))
                .build();
    }

    private String passwordResetMessage(String tempPassword) {
        return "안녕하세요. 회원님의 임시비밀번호 안내 관련 이메일 입니다. " +
                "임시 비밀번호는 " + tempPassword + " 입니다.";
    }

    private String generateTemporaryPassword() {
        char[] charSet = {
                '0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9', 'A', 'B',
                'C', 'D', 'E', 'F', 'G', 'H',
                'I', 'J', 'K', 'L', 'M', 'N',
                'O', 'P', 'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X', 'Y', 'Z'
        };
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int idx = (int) (charSet.length * Math.random());
            str.append(charSet[idx]);
        }
        return str.toString();
    }
}
