package account.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {
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
                .exceptionHandling(ex -> ex.authenticationEntryPoint(restAuthenticationEntryPoint))
                // Postman
                .csrf(csrf -> csrf.disable())
                // H2 console
                .headers(headers -> headers.frameOptions().disable())
                .authorizeHttpRequests(auth -> auth
                        // New user signup - NONE
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        // Changing password - BASIC
                        .requestMatchers(HttpMethod.POST, "/api/auth/changepass").authenticated()
                        // Get all payment info by period - BASIC
                        .requestMatchers(HttpMethod.GET, "/api/empl/payment").authenticated()
                        // Store payment info in database - NONE
                        .requestMatchers(HttpMethod.POST, "/api/acct/payments").permitAll()
                        // Update salaries of specified users in the database - NONE
                        .requestMatchers(HttpMethod.PUT, "/api/acct/payments").permitAll()
                        // Fix this later
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(HttpMethod.POST, "/actuator/shutdown").permitAll()
                        .anyRequest().permitAll()
                )
                .sessionManagement(sessions -> sessions
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return http.build();
    }
}