package com.example.Ahi.service.paymentservice;

import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Payment;
import com.example.Ahi.exception.AhiException;
import com.example.Ahi.exception.ErrorCode;
import com.example.Ahi.repository.MemberRepository;
import com.example.Ahi.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentUtils {
    @Value("${toss-secret-key}")
    private String secretKey;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    public String generateAuthorization() throws UnsupportedEncodingException {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes("UTF-8"));
        return "Basic " + new String(encodedBytes);
    }
    public Member getMember(String memberId){
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null)
            throw new AhiException(ErrorCode.USER_NOT_FOUND);
        return member;
    }

    public Payment getPayment(String orderId){
        Payment payment = paymentRepository.findById(orderId).orElse(null);
        if(payment == null){
            throw new AhiException(ErrorCode.INVALID_INPUT);
        }
        return payment;
    }

    public void verifyMember(String orderIdOfMember, String memberId){
        if(!orderIdOfMember.equals(memberId)){
            throw new AhiException(ErrorCode.INVALID_INPUT);
        }
    }

    public List<Payment> getPaymentList(String memberId){
        return paymentRepository.findByMemberId(getMember(memberId));
    }
}
