package com.example.weblogin.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration // IoC 등록
@EnableWebSecurity // 시큐리티 활성화, springSecurityFilterChain 자동 등록
public class SecurityConfig {

    // 비밀번호 암호화 Bean 등록
    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    // 시큐리티 필터 체인 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 비활성화

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/main/**").authenticated() // 인증 필요
                .anyRequest().permitAll() // 그 외는 모두 허용
            )

            .formLogin(form -> form
                .loginPage("/signin") // 로그인 페이지 경로 (GET)
                .loginProcessingUrl("/signin") // 로그인 처리 경로 (POST)
                .defaultSuccessUrl("/main") // 로그인 성공 시 이동 경로
                .permitAll()
            );

        return http.build();
    }
}
