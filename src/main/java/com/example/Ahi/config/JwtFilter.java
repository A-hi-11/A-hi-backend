package com.example.Ahi.config;

import com.example.Ahi.service.MemberService;
import com.example.Ahi.utils.JwtUtil;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final MemberService memberService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //헤더에서 authorization부분 가져오기
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        //authorizatio에 아무것도 없을때 -> 로그인 안한 사용자(바로 return)
        if(authorization==null){
            filterChain.doFilter(request,response);
            return;
        }

        //authorization에서 token가져오기
        String token=authorization.replace("Bearer ","");
        //만료된 토큰인지 아닌지 체크 -->만료(바로 return)
        if(JwtUtil.isExpired(token,secretKey)){
            filterChain.doFilter(request,response);
            return;
        }
        //토큰에서 사용자Id 꺼내오기
        final String member_id = JwtUtil.extractMember(token,secretKey);


        //가입한 사용자에 대해 막혀있는 권한에 대해 허용 설정
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(member_id,null, List.of(new SimpleGrantedAuthority("USER")));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request,response);
    }
}
