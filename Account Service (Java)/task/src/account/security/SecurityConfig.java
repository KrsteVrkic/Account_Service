package account.security;

import static account.security.Role.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    final String user = USER.getRole();
    final String admin = ADMINISTRATOR.getRole();
    final String accountant = ACCOUNTANT.getRole();

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    public SecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(restAuthenticationEntryPoint).accessDeniedHandler(new Handler()))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions().disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/changepass").hasAnyAuthority(admin, user, accountant)
                        .requestMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyAuthority(user, accountant)
                        .requestMatchers(HttpMethod.POST, "/api/acct/payments").hasAnyAuthority(accountant)
                        .requestMatchers(HttpMethod.PUT, "/api/acct/payments").hasAnyAuthority(accountant)
                        .requestMatchers(HttpMethod.GET, "/api/admin/user/**").hasAuthority(admin)
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/user/**").hasAuthority(admin)
                        .requestMatchers(HttpMethod.PUT, "/api/admin/user/role").hasAuthority(admin)
                        .requestMatchers(HttpMethod.POST, "/actuator/shutdown").permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                        .anyRequest().denyAll()
                )
                .sessionManagement(sessions -> sessions
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return http.build();
    }
}