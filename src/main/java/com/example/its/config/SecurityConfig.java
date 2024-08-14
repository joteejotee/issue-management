package com.example.its.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//HTTPリクエストのセキュリティ設定を行うためのクラス
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//Spring SecurityのWebセキュリティ機能を有効化
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
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

    //メソッドがBean（オブジェクト）を生成することを示す
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/home").permitAll() // ホームページは誰でもアクセス可能
                        .anyRequest().authenticated() // その他のリクエストは認証が必要
                )
                .formLogin(login -> login.permitAll()
                )
                .logout(logout -> logout.permitAll()
                );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

