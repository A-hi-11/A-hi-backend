package com.example.Ahi.service.paymentservice;

import com.example.Ahi.domain.Payment;
import com.example.Ahi.dto.paymentDto.PaymentConfirmRequestDto;
import com.example.Ahi.exception.AhiException;
import com.example.Ahi.exception.ErrorCode;
import com.example.Ahi.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentConfirmService {

    private static final String TOSS_API_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String CONTENT_TYPE = "application/json";
    private final PaymentUtils paymentUtils;
    private final PaymentRepository paymentRepository;
    public ResponseEntity<Object> confirmPayment(PaymentConfirmRequestDto requestDto, String memberId) throws Exception {
        // 검증 로직
        Payment payment = paymentUtils.getPayment(requestDto.getOrderId());
        paymentUtils.verifyMember(payment.getMemberId().getMemberId(), memberId);
        verifyRequest(payment.getAmount(), requestDto.getAmount());
        updatePayment(payment, requestDto.getPaymentKey());

        // 승인 로직
        String authorizations = paymentUtils.generateAuthorization();
        JSONObject requestData = parseJson(requestDto);
        JSONObject response = sendPaymentRequest(requestData, authorizations);

        return ResponseEntity.ok(checkStatus(payment, response));
    }

    private JSONObject parseJson(PaymentConfirmRequestDto request) throws ParseException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(request);
        JSONParser parser = new JSONParser();
        JSONObject requestData = (JSONObject) parser.parse(jsonString);
        return requestData;
    }

    private void verifyRequest(Long amountBefore, Long amountAfter){
        if(!Objects.equals(amountAfter, amountBefore)){
            throw new AhiException(ErrorCode.INVALID_INPUT);
        }
    }

    private void updatePayment(Payment payment, String paymentKey){
        payment.setPaymentKey(paymentKey);
        paymentRepository.save(payment);
    }
    private JSONObject sendPaymentRequest(JSONObject requestData, String authorizations) throws IOException, ParseException {
        URL url = new URL(TOSS_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        setConnectionProperties(connection, authorizations);

        sendRequestData(connection, requestData);
        JSONObject response = receiveResponseData(connection);

        return response;
    }

    private void setConnectionProperties(HttpURLConnection connection, String authorizations) throws ProtocolException {
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", CONTENT_TYPE);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
    }

    private void sendRequestData(HttpURLConnection connection, JSONObject requestData) throws IOException {
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
    }

    private JSONObject receiveResponseData(HttpURLConnection connection) throws IOException, ParseException {
        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);

        responseStream.close();
        return jsonObject;
    }

    private Object checkStatus(Payment payment, JSONObject response){
        Object result = response.get("status");
        if(result == null){
            return response;
        }
        payment.setStatus(PaymentStatus.DONE.getStatus());
        paymentRepository.save(payment);
        return result;
    }
}
