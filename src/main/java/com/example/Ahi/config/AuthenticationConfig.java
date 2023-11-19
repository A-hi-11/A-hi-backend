package com.example.Ahi.config;

import com.example.Ahi.service.MemberService;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final MemberService memberService;

    @Value("${jwt-secret-key}")
    private String secretKey;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
//                .cors(Customizer.withDefaults())
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/**").permitAll()
                                .requestMatchers("/memberCheck").authenticated()
//                                //TODO: 배포 후 설정
//                                .requestMatchers("/user/signin","user/signup").permitAll()
//                                .requestMatchers(HttpMethod.POST, "/**").authenticated()
//                                .requestMatchers(HttpMethod.GET, "/**").authenticated()
//                                .requestMatchers(HttpMethod.PUT, "/**").authenticated()
//                                .requestMatchers(HttpMethod.DELETE, "/**").authenticated()
                )
                .addFilterBefore(new JwtFilter(memberService,secretKey), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
