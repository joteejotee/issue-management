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

//@Configurationは、Springの設定クラスであると示す。@Ena～はspring securityを有効化する
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    //メソッドがBean（オブジェクト）を生成することを示す
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // 静的リソースを完全に許可
                        .requestMatchers("/images/**", "/css/**", "/js/**", "/favicon.ico").permitAll()
                        .requestMatchers("/webjars/**", "/static/**", "/public/**").permitAll()
                        // APIエンドポイントを完全に許可
                        .requestMatchers("/api/**").permitAll()
                        // 基本ページを許可
                        .requestMatchers("/", "/home", "/login").permitAll()
                        .anyRequest().authenticated() // その他のリクエストは認証が必要
                )
                .formLogin(login -> login
                        .loginPage("/login") // カスタムログインページ
                        .loginProcessingUrl("/perform_login") // ログイン処理URL
                        .defaultSuccessUrl("/issues", true) // ログイン成功時のリダイレクト先
                        .failureUrl("/login?error=true") // ログイン失敗時のリダイレクト先
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**") // APIエンドポイントのCSRF保護を無効
                )
                .userDetailsService(customUserDetailsService); // カスタムUserDetailsServiceを使用
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

