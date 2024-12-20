package com.StudentManagementSystem.AllConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.StudentManagementSystem.AllConfig.AppSecurityService.CustomBasicAuthenticationEntryPoint;
import com.StudentManagementSystem.AllConfig.AppSecurityService.CustomBearerTokenAccessDeniedHandler;
import com.StudentManagementSystem.AllConfig.AppSecurityService.CustomBearerTokenAuthenticationEntryPoint;
import com.StudentManagementSystem.AllConfig.AppSecurityService.JwtAuthFilter;
import com.StudentManagementSystem.AllConfig.AppSecurityService.UserInfoUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthFilter authFilter;

	@Autowired
	private UserInfoUserDetailsService detailsService;

	@Autowired
	CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;

	@Autowired
	CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler;
	@Autowired
	CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return

		http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/login",

				"/api/v1/addStudent")

				.permitAll().requestMatchers("/", "/error").permitAll().anyRequest().authenticated())
				.authenticationProvider(authenticationProvider())

				.httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(this.customBasicAuthenticationEntryPoint))

				.sessionManagement(
						sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
				.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
				.exceptionHandling(exception -> exception.accessDeniedHandler(customBearerTokenAccessDeniedHandler)
						.authenticationEntryPoint(customBearerTokenAuthenticationEntryPoint))

				.build();

	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(detailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

}
