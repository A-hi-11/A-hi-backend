package com.example.Ahi.config;

import com.example.Ahi.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final MemberService memberService;
    private final DefaultOAuth2UserService oAuth2UserService;

    @Value("${jwt-secret-key}")
    private String secretKey;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String gRedirectUrl;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String nRedirectUrl;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
//                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                        authorize
                                //TODO: 배포 후 설정
                                .requestMatchers("/naver/redirect","/google/redirect","/user/signin","/user/signup","/user/mail","/google-login","/user/mail/check","/naver-login","/oauth2/**").permitAll()
                                .requestMatchers("/prompt/view","/prompt/view/info").permitAll()
                                .requestMatchers("/prompt/comment/read").permitAll()
                                .requestMatchers("/gpt/**").permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
//              .exceptionHandling(exceptionHandling -> exceptionHandeling.authenticationEntryPoint(new FailedAuthenticationEntryPoint())

                .addFilterBefore(new JwtFilter(memberService,secretKey), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                                .redirectionEndpoint(endpoint -> endpoint.baseUri("/oauth2/callback/*"))
                                .userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserService))
//                        .redirectionEndpoint(redirection-> redirection
//                                .baseUri(gRedirectUrl)
//                                .baseUri(nRedirectUrl)

//                        )
                );
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
