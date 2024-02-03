package com.example.Ahi.controller;

import com.example.Ahi.dto.paymentDto.PaymentCancelDto;
import com.example.Ahi.dto.paymentDto.PaymentConfirmRequestDto;
import com.example.Ahi.dto.paymentDto.PaymentListDto;
import com.example.Ahi.dto.paymentDto.PaymentSaveRequestDto;
import com.example.Ahi.service.paymentservice.PaymentCancelService;
import com.example.Ahi.service.paymentservice.PaymentConfirmService;
import com.example.Ahi.service.paymentservice.PaymentDetailService;
import com.example.Ahi.service.paymentservice.PaymentSaveService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;


@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentConfirmService paymentConfirmService;
    private final PaymentCancelService paymentCancelService;
    private final PaymentDetailService paymentDetailService;
    private final PaymentSaveService paymentSaveService;
    @PostMapping("/save")
    public ResponseEntity<String> savePayment(Authentication authentication,
                                              @RequestBody PaymentSaveRequestDto request){
        String memberId = authentication.getName();
        return ResponseEntity.ok(paymentSaveService.savePayment(request, memberId));
    }

    @PostMapping( "/confirm")
    public ResponseEntity<JSONObject> confirmPayment(Authentication authentication,
                                                     @RequestBody PaymentConfirmRequestDto request) throws Exception {
        String memberId = authentication.getName();
        return paymentConfirmService.confirmPayment(request, memberId);
    }
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelPayment(Authentication authentication,
                                                @RequestBody PaymentCancelDto request) throws UnsupportedEncodingException {
        String memberId = authentication.getName();
        return paymentCancelService.cancelPayment(request, memberId);
    }
    @GetMapping("/list")
    public List<PaymentListDto> getPaymentList(Authentication authentication){
        String memberId = authentication.getName();
        return paymentDetailService.getPaymentList(memberId);
    }

    @GetMapping("/list/{orderId}")
    public void getPaymentDetails(Authentication authentication, @PathVariable String orderId) throws UnsupportedEncodingException {
        String memberId = authentication.getName();
        paymentDetailService.getPaymentDetails(orderId, memberId);
    }
}
