package com.example.Ahi.service.paymentservice;

import com.example.Ahi.domain.Payment;
import com.example.Ahi.dto.paymentDto.PaymentCancelDto;
import com.example.Ahi.exception.AhiException;
import com.example.Ahi.exception.ErrorCode;
import com.example.Ahi.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class PaymentCancelService {
    private final PaymentUtils paymentUtils;
    // TODO: 결제 취소 메소드 추가 필요
    public ResponseEntity<Object> cancelPayment(PaymentCancelDto request, String memberId) throws UnsupportedEncodingException, ParseException {
        Payment payment = paymentUtils.getPayment(request.getOrderId());
        paymentUtils.verifyMember(payment.getMemberId().getMemberId(), memberId);
        checkPaymentAlreadyCanceled(payment);

        String url = generatePaymentUrl(payment.getPaymentKey());
        HttpEntity<String> entity = createHttpEntity(request.getCancelReason());
        // 성공 여부에 따라 다른 메세지 보내기
        JSONObject result = sendCancelRequest(url, entity);

        return ResponseEntity.ok(paymentUtils.checkStatus(payment, result));
    }


    private String generatePaymentUrl(String paymentKey) {
        return "https://api.tosspayments.com/v1/payments/" + paymentKey +"/cancel";
    }

    private HttpEntity<String> createHttpEntity(String cancelReason) throws UnsupportedEncodingException {
        String auth = paymentUtils.generateAuthorization();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", auth);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cancelReason", cancelReason);

        return new HttpEntity<>(jsonObject.toString(), headers);
    }

    private JSONObject sendCancelRequest(String url, HttpEntity<String> entity) throws ParseException {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        String utf8Body = new String(response.getBody().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(utf8Body);
        return (JSONObject) obj;
    }
    private void checkPaymentAlreadyCanceled(Payment payment){
        if(payment.getStatus().equals(PaymentStatus.CANCELED.getStatus())){
            throw new AhiException(ErrorCode.ALREADY_CANCELED);
        }
    }
}
