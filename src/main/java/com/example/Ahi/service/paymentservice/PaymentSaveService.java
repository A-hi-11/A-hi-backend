package com.example.Ahi.service.paymentservice;

import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Payment;
import com.example.Ahi.dto.paymentDto.PaymentSaveRequestDto;
import com.example.Ahi.exception.AhiException;
import com.example.Ahi.exception.ErrorCode;
import com.example.Ahi.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentSaveService {
    private final PaymentRepository paymentRepository;
    private final PaymentUtils paymentUtils;
    public String savePayment(PaymentSaveRequestDto request, String memberId){
        Member member = paymentUtils.getMember(memberId);
        Payment payment = paymentRepository.findById(request.getOrderId()).orElse(null);
        if(payment != null){
            throw new AhiException(ErrorCode.INVALID_INPUT);
        }
        paymentRepository.save(request.toPayment(member));
        return request.getOrderId();
    }

}
