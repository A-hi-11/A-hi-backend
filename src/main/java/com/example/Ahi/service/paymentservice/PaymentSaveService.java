package com.example.Ahi.service.paymentservice;

import com.example.Ahi.domain.Member;
import com.example.Ahi.dto.paymentDto.PaymentSaveRequestDto;
import com.example.Ahi.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentSaveService {
    private final PaymentRepository paymentRepository;
    private final PaymentUtils paymentUtils;
    public String savePayment(PaymentSaveRequestDto request, String memberId){
        Member member = paymentUtils.getMember(memberId);
        paymentRepository.save(request.toPayment(member));
        return "save successfully!";
    }

}
