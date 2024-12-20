package com.StudentManagementSystem.AllConfig.AppSecurityService;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("deprecation")
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserInfoUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// Check if the request is for the excluded endpoint
		if (isExcludedEndpoint(request)) {
			filterChain.doFilter(request, response); // Skip authentication and proceed
			return;
		}

		String authHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
			try {
				username = jwtService.extractUsername(token);
			} catch (IllegalArgumentException e) {
				logger.error("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				// Create a Result object for expired JWT token
//				Result result = new Result();
//				result.setFlag(false);
//				result.setCode(HttpStatus.UNAUTHORIZED.value());
//				result.setMessage("JWT expired. Please log in again.");

				// Set the response status and write the Result object as JSON response
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				response.setContentType("application/json");
				response.getWriter().write("JWT expired. Please log in again");
				return;
			} catch (MalformedJwtException e) {
				// Create a Result object for invalid JWT token
//				Result result = new Result();
//				result.setFlag(false);
//				result.setCode(HttpStatus.UNAUTHORIZED.value());
//				result.setMessage("Invalid JWT token.");

				// Set the response status and write the Result object as JSON response
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				response.setContentType("application/json");
				response.getWriter().write("Invalid JWT token");
				return;
			}
		} else {
			logger.warn("JWT Token does not begin with Bearer String");
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			if (jwtService.validateToken(token, userDetails)) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		filterChain.doFilter(request, response);
	}

	private boolean isExcludedEndpoint(HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		String[] excludedEndpoints = {

				"/api/v1/test"

		};

		return Arrays.asList(excludedEndpoints).contains(requestURI);
	}

}
