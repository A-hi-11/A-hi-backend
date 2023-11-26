package com.example.Ahi.service;


import com.example.Ahi.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final RedisUtil redisUtil;

    public boolean codeCheck(String member_id, int code) {
        boolean result = false;
        //이메일주소로 코드 찾아 비교
        String givenCode = redisUtil.getData(member_id);
        if (givenCode==null || givenCode.isEmpty()){
            return false;
        }
        if (givenCode.equals(String.valueOf(code))){
            result=true;
            redisUtil.deleteData(member_id);
        }

        return result;
    }
}
