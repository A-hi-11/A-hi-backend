package com.example.Ahi.service;


import com.example.Ahi.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final RedisUtil redisUtil;

    public boolean codeCheck(String memberId, int code) {
        boolean result;
        //이메일주소로 코드 찾아 비교
        String givenCode = redisUtil.getData(memberId);
        if (givenCode==null || givenCode.isEmpty()
                || !givenCode.equals(String.valueOf(code))){
            return false;
        }
        redisUtil.deleteData(memberId);
        return true;
    }
}
