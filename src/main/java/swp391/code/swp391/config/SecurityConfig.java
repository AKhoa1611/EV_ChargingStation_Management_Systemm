package swp391.code.swp391.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
//            .cors(cors -> cors.disable())
            .authorizeHttpRequests(auth ->auth
                .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/charging-stations/**").permitAll()
                    .requestMatchers("/api/charging-points/**").permitAll()
                    .requestMatchers("/api/connector-types/**").permitAll()
                .anyRequest().permitAll()
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) //Không dùng session vì đang dùng JWT
            );

        return http.build();
    }
    @Bean
    public JwtDecoder jwtDecoder() { //Cấu hình giải mã JWT với thuật toán HS512
        String key = "123456";
        SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "hs512"); //Tạo SecretKey từ chuỗi key với thuật toán HS512
        return NimbusJwtDecoder.withSecretKey(secretKey) //NimbusJwtDecoder là một triển khai của JwtDecoder sử dụng thư viện Nimbus JOSE + JWT để giải mã và xác thực JWT
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }
}