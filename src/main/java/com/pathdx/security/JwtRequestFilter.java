package com.pathdx.security;

import com.pathdx.repository.UsersRepository;
import com.pathdx.utils.BasicAuthUtil;
import com.pathdx.utils.JwtTokenUtil;
import com.pathdx.utils.SamlAuthUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Service
public class JwtRequestFilter extends OncePerRequestFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private CustomUserDetails userDetailService;

	@Autowired
	BasicAuthUtil basicAuthUtil;

	@Autowired
	SamlAuthUtil samlAuthUtil;


	@Autowired
	UsersRepository usersRepository;

	@Value("${secretKey}")
	private String secretKey;


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		String bearerToken = null;
		String basicToken = null;
		String samlToken=null;
		String token = request.getHeader("Authorization");
		if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
			LOGGER.info("Bearer token check");
			bearerToken = token.substring(7);
			bearerTokenAuthentication(bearerToken, request, response, filterChain);
			//filterChain.doFilter(request, response);
		} else if (StringUtils.hasText(token) && token.startsWith("Basic ")) {
			LOGGER.info("basic token check");
			basicToken = token.substring(6);
			basicTokenAuthentication(basicToken, request, response, filterChain);
		//	filterChain.doFilter(request, response);
		} else if(StringUtils.hasText(token) && token.startsWith("Saml ")) {
			LOGGER.info("Saml token check");
			samlToken = token.substring(5);
			boolean validated = samlAuthUtil.validateToken(samlToken);
			LOGGER.error("Token is expired and not valid anymore");
			if(validated){
				filterChain.doFilter(request, response);
			}else{
				LOGGER.info("No token check");
				LOGGER.error("Token is expired and not valid anymore");
				SecurityContextHolder.clearContext();
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
		}else{
			LOGGER.info("No token check");
			LOGGER.error("Token is expired and not valid anymore");
			SecurityContextHolder.clearContext();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			//filterChain.doFilter(request, response);
		}
	}

	private void bearerTokenAuthentication(String bearerToken, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String username = null;
		try {
			username = jwtTokenUtil.getUsernameFromToken(bearerToken);
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailService.loadUserByUsername(username);
				if (jwtTokenUtil.validateToken(bearerToken, userDetails)) {
					UsernamePasswordAuthenticationToken authentication = new
							UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					logger.info("Authenticated user " + username + ", setting security context");
					SecurityContextHolder.getContext().setAuthentication(
							authentication);
					filterChain.doFilter(request, response);
				}

			}
		} catch (IllegalArgumentException e) {
			LOGGER.error("User not exists", e);
			SecurityContextHolder.clearContext();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		} catch (ExpiredJwtException e) {
			LOGGER.error("Token is expired and not valid anymore", e);
			SecurityContextHolder.clearContext();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		} catch (SignatureException e) {
			LOGGER.error("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.", e);
			SecurityContextHolder.clearContext();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		}
	}

	private void basicTokenAuthentication(String basicToken, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
            if(basicAuthUtil.decryptText(basicToken,secretKey)){
				filterChain.doFilter(request, response);
			}else{
				LOGGER.error("Token is expired and not valid anymore");
				SecurityContextHolder.clearContext();
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}

		} catch (IllegalArgumentException e) {
			LOGGER.error("User not exists", e);
			SecurityContextHolder.clearContext();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		} catch (ExpiredJwtException e) {
			LOGGER.error("Token is expired and not valid anymore", e);
			SecurityContextHolder.clearContext();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		} catch (SignatureException e) {
			LOGGER.error("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.", e);
			SecurityContextHolder.clearContext();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		}
	}
	private void samlTokenAuthentication(String basicToken, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			if(basicAuthUtil.decryptText(basicToken,secretKey)){
				filterChain.doFilter(request, response);
			}else{
				LOGGER.error("Token is expired and not valid anymore");
				SecurityContextHolder.clearContext();
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}

		} catch (IllegalArgumentException e) {
			LOGGER.error("User not exists", e);
			SecurityContextHolder.clearContext();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		} catch (ExpiredJwtException e) {
			LOGGER.error("Token is expired and not valid anymore", e);
			SecurityContextHolder.clearContext();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		} catch (SignatureException e) {
			LOGGER.error("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.", e);
			SecurityContextHolder.clearContext();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		}
	}
	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

}
