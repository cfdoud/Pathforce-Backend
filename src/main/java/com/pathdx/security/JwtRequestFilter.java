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
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;   // <-- inject interface
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * For local profile we usually skip JWT entirely.
 * If you want that behavior, keep @Profile("!local") here.
 * If you want JWT on local too, remove the @Profile line.
 */
@Profile("!local")
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired private JwtTokenUtil jwtTokenUtil;

    // ⬇️ inject the interface so it works with either CustomUserDetails (non-local)
    // or the InMemoryUserDetailsManager (local), depending on active profile
    @Autowired private UserDetailsService userDetailsService;

    @Autowired private BasicAuthUtil basicAuthUtil;
    @Autowired private SamlAuthUtil samlAuthUtil;
    @Autowired private UsersRepository usersRepository;

    @Value("${secretKey}")
    private String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
        throws ServletException, IOException {

        String tokenHeader = request.getHeader("Authorization");
        String bearerToken = null, basicToken = null, samlToken = null;

        if (StringUtils.hasText(tokenHeader) && tokenHeader.startsWith("Bearer ")) {
            LOGGER.info("Bearer token check");
            bearerToken = tokenHeader.substring(7);
            bearerTokenAuthentication(bearerToken, request, response, filterChain);
            return;
        } else if (StringUtils.hasText(tokenHeader) && tokenHeader.startsWith("Basic ")) {
            LOGGER.info("basic token check");
            basicToken = tokenHeader.substring(6);
            basicTokenAuthentication(basicToken, request, response, filterChain);
            return;
        } else if (StringUtils.hasText(tokenHeader) && tokenHeader.startsWith("Saml ")) {
            LOGGER.info("Saml token check");
            samlToken = tokenHeader.substring(5);
            boolean validated = samlAuthUtil.validateToken(samlToken);
            if (validated) {
                filterChain.doFilter(request, response);
            } else {
                LOGGER.error("Token is expired and not valid anymore");
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
            return;
        } else {
            LOGGER.info("No token provided");
            LOGGER.error("Token is expired and not valid anymore");
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
    }

    private void bearerTokenAuthentication(String bearerToken,
                                           HttpServletRequest request,
                                           HttpServletResponse response,
                                           FilterChain filterChain)
        throws ServletException, IOException {

        try {
            String username = jwtTokenUtil.getUsernameFromToken(bearerToken);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(bearerToken, userDetails)) {
                    UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    LOGGER.info("Authenticated user {} , setting security context", username);
                    SecurityContextHolder.getContext().setAuthentication(auth);
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
            LOGGER.error("JWT signature invalid", e);
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    private void basicTokenAuthentication(String basicToken,
                                          HttpServletRequest request,
                                          HttpServletResponse response,
                                          FilterChain filterChain)
        throws ServletException, IOException {

        try {
            if (basicAuthUtil.decryptText(basicToken, secretKey)) {
                filterChain.doFilter(request, response);
            } else {
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
            LOGGER.error("JWT signature invalid", e);
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    private void samlTokenAuthentication(String basicToken,
                                         HttpServletRequest request,
                                         HttpServletResponse response,
                                         FilterChain filterChain)
        throws ServletException, IOException {

        try {
            if (basicAuthUtil.decryptText(basicToken, secretKey)) {
                filterChain.doFilter(request, response);
            } else {
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
            LOGGER.error("JWT signature invalid", e);
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
