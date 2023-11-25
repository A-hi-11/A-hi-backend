package com.example.Ahi.utils;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class MailUtilImpl implements MailUtil{

    @Autowired
    JavaMailSender emailSender;

    private final RedisUtil redisUtil;

    private Long expiredMs = 1000*60*5L; //5분
    public static final int CODE = createCode();
    @Value("${AdminMail.id}")
    private String host_mail_address;

    @Value("${AdminMail.password}")
    private String password;


    private MimeMessage createMessage(String to)throws Exception{
        MimeMessage  message = emailSender.createMimeMessage();

        message.setFrom(new InternetAddress("host_mail_address","에이하이"));//보내는 사람
        message.addRecipients(MimeMessage.RecipientType.TO, to);//보내는 대상
        message.setSubject("[에이하이]이메일 인증");//제목

        String msgg="";
        msgg+= "<div style='margin:20px;'>";
        msgg+= "<h2> 안녕하세요 프롬프트 공유 플랫폼 에이하이입니다. </h2>";
        msgg+= "<br>";
        msgg+= "<p>아래 코드를 복사해 입력해주세요<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<div style='font-size:130%'>";
        msgg+= "CODE : <strong>";
        msgg+= "<br/>"+CODE+"</strong><div><br/> ";
        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용

        return message;
    }

    public static int createCode() {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int key = random.nextInt(100000) % 100000;
        int head = random.nextInt(1,9);
        int final_code = head*100000+key;
        return final_code;
    }
    @Override
    public int sendMessage(String email) throws Exception {
        MimeMessage message = createMessage(email);
        try{//예외처리
            emailSender.send(message);
            saveCode(email,String.valueOf(CODE));
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return CODE;
    }

    public void saveCode(String member_id,String code){
        redisUtil.setData(member_id,code,expiredMs);
    }
}