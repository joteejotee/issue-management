package com.example.its.config;

import com.example.its.domain.user.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//HTTPリクエストのセキュリティ設定を行うためのクラス
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//Spring SecurityのWebセキュリティ機能を有効化
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//以下三つはユーザーの認証情報を管理するためのクラス
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
//パスワードの暗号化に使用されるエンコーダー
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//パスワードのエンコード処理を行うインターフェース
import org.springframework.security.crypto.password.PasswordEncoder;
//メモリ上にユーザー情報を保存するクラス
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//HTTPセキュリティのフィルターチェーンを設定するクラス
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configurationは、Springの設定クラスであると示す。@Ena～はspring securityを有効化する
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    //メソッドがBean（オブジェクト）を生成することを示す
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/files/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/", "/login").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .defaultSuccessUrl("/issues", true)
                )
                .logout(logout -> logout
                        .permitAll()
                        .logoutSuccessUrl("/login")
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

