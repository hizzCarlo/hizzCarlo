package co.surveygenerator.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import co.surveygenerator.security.services.UserDetailsServiceImpl;


public class JwtTokenFilter extends OncePerRequestFilter {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(JwtTokenFilter.class);

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;

	private String getToken(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer"))
			return header.replace("Bearer ", "");
		return null;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			String token = getToken(request);
			if (token != null && jwtProvider.validateToken(token)) {
				String userName = jwtProvider.getUserNameFromToken(token);
				UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(userName);
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(auth);

			}
		} catch (Exception e) {
			logger.error("error:" + e.getMessage());
		}
		filterChain.doFilter(request, response);

	}

}
