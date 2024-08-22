package assetmanagement.config;

import assetmanagement.jwt.AuthEntryPointJwt;
import assetmanagement.jwt.AuthTokenFilter;
import assetmanagement.security.UserDetailsServiceImpl;
import java.util.Arrays;
import org.springframework.security.config.Customizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurity {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {
    // CorsConfiguration configuration = new CorsConfiguration();
    // // configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
    // //
    // // Specify your frontend's domain
    // configuration.setAllowedOrigins(Arrays.asList("*"));
    // configuration.setAllowedOriginPatterns(Arrays.asList("*"));
    // configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT",
    // "DELETE"));
    // configuration.setAllowedHeaders(Arrays.asList("*"));
    // configuration.setAllowCredentials(true);

    // UrlBasedCorsConfigurationSource source = new
    // UrlBasedCorsConfigurationSource();
    // source.registerCorsConfiguration("/**", configuration);

    // return source;
    // }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector)
            throws Exception {

        MvcRequestMatcher swaggerMatcher = new MvcRequestMatcher(introspector, "/swagger-ui/**");
        MvcRequestMatcher swagger2Matcher = new MvcRequestMatcher(introspector, "/v3/api-docs/**");
        MvcRequestMatcher swagger2Matcher1 = new MvcRequestMatcher(introspector, "/ui");
        MvcRequestMatcher swagger6Matcher = new MvcRequestMatcher(introspector, "/swagger-ui.html");
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> Customizer.withDefaults())
                // .exceptionHandling(exception ->
                // exception.authenticationEntryPoint(unauthorizedHandler))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)
                        .accessDeniedHandler(accessDeniedHandler()))
                // .sessionManagement(session ->
                // session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/assets/**", "/uploads/**", "/asset/get-by-id/**",
                                "/asset/get-by-assetId-audit/**")
                        .permitAll()
                        .requestMatchers("/api/swagger-ui/**", "/swagger-ui/**", "/swagger-resources/*",
                                "/v3/api-docs/**")
                        .permitAll()
                        // .requestMatchers("/brand/get-all").hasAuthority("Admin")
                        .requestMatchers("/brand/get-all").hasAnyAuthority("Admin", "Employee")
                        .requestMatchers("/", "/login", "user/**", "asset/get-by-id/**",
                                "/asset/get-by-assetId-audit/**")
                        .permitAll()
                        .requestMatchers(swaggerMatcher, swagger2Matcher, swagger2Matcher1, swagger6Matcher).permitAll()
                        .anyRequest().authenticated());
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        // http.cors(cors -> cors.configurationSource(corsConfigurationSource())); //
        // Apply CORS configuration
        // http
        // .logout()
        // .logoutUrl("/logout")
        // .logoutSuccessUrl("/login")
        // .invalidateHttpSession(true)
        // .clearAuthentication(true); // New method to clear authentication
        return http.build();
    }

    @Bean
    public CustomAccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

}
