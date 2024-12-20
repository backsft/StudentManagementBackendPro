package com.StudentManagementSystem.AllConfig.AppSecurityService;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${app.secret}")
	private String SECRET;

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {

		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();

	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}


	public String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userName);
	}

	private String createToken(Map<String, Object> claims, String userName) {
		return Jwts.builder().setClaims(claims).setSubject(userName).setIssuedAt(new Date(System.currentTimeMillis()))

				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 20)) // 90 days
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}

	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public boolean isValidToken(String token) {
		try {
			// Parse the token
			Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace("Bearer ", "")) // Remove
																										// present
					.getBody();

			// You can add additional validation logic here if needed

			// System.out.println(" token validated
			// ___________________________________________"+true);
			// System.out.println(" token is
			// ___________________________________________"+claims);

			return true; // Token is valid
		} catch (Exception e) {

			// System.out.println(" token validated
			// ___________________________________________"+false);

			return false; // Token is invalid or expired
		}
	}

	public JwtInfo decodeJwt(String jwtToken) {
		try {
			Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(jwtToken).getBody();
			String username = claims.getSubject();
			return new JwtInfo(username);
		} catch (Exception e) {
			// You may want to log the exception for further analysis
			throw new RuntimeException("JWT Decoding Error", e);
		}
	}

	public String extractToken(String authorizationHeader) {
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		}
		throw new IllegalArgumentException("Invalid or missing Authorization header");
	}

	public String extractMobileNoFromAuthorizationHeader(String authorizationHeader) {
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		}
		throw new IllegalArgumentException("Invalid or missing Authorization header");
	}

}
