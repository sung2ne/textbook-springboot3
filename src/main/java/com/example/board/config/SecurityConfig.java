// 수정: src/main/java/com/example/board/config/SecurityConfig.java (SecurityFilterChain 추가)
package com.example.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 요청 인가 설정
            .authorizeHttpRequests(auth -> auth
                // 정적 리소스는 모두 허용
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                // 글쓰기, 수정은 인증 필요 (구체적 경로를 먼저 선언)
                .requestMatchers("/boards/new", "/boards/{id}/edit").authenticated()
                // 홈, 게시판 목록, 상세는 모두 허용
                .requestMatchers("/", "/boards", "/boards/{id}").permitAll()
                // 로그인, 회원가입은 모두 허용
                .requestMatchers("/login", "/members/signup").permitAll()
                // H2 콘솔은 개발용으로 허용
                .requestMatchers("/h2-console/**").permitAll()
                // 나머지 요청은 인증 필요
                .anyRequest().authenticated()
            )
            // 폼 로그인 설정
            .formLogin(form -> form
                .loginPage("/login")              // 커스텀 로그인 페이지
                .defaultSuccessUrl("/boards")     // 로그인 성공 시 이동
                .permitAll()
            )
            // 로그아웃 설정
            .logout(logout -> logout
                .logoutSuccessUrl("/")            // 로그아웃 후 홈으로
                .permitAll()
            );

        // H2 콘솔용 설정 (개발 환경)
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
