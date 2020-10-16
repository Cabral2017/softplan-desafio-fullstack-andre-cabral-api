package com.softplan.desafio.security.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.softplan.desafio.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${softplan.app.jwtSecret}")
	private String jwtSecret;

	@Value("${softplan.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder()
				.setSubject((userPrincipal.getUsername()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (MalformedJwtException e) {
			logger.error("JWT token inválido", e.getMessage());
		} catch (SignatureException e) {
			logger.error("Assinatura de token inválida", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token não é suportado", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token está expirado", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty", e.getMessage());
		}

		return false;
	}
}
