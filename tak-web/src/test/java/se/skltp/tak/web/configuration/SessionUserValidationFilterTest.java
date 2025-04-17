package se.skltp.tak.web.configuration;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionUserValidationFilterTest {

    private UserDetailsService userDetailsService;
    private SessionUserValidationFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        userDetailsService = mock(UserDetailsService.class);
        filter = new SessionUserValidationFilter(userDetailsService);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldProceedIfUserExists() throws Exception {
        // given
        UserDetails userDetails = new User("testuser", "pass", List.of());
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).sendRedirect(any());
    }

    @Test
    void shouldInvalidateSessionIfUserDoesNotExist() throws Exception {
        // given
        UserDetails userDetails = new User("ghostuser", "pass", List.of());
        when(userDetailsService.loadUserByUsername("ghostuser"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        var session = mock(jakarta.servlet.http.HttpSession.class);
        when(request.getSession()).thenReturn(session);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(session).invalidate();
        verify(response).sendRedirect(request.getContextPath());
        verify(filterChain, never()).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldDoNothingIfNotAuthenticated() throws Exception {
        // given
        // Ingen auth satt
        SecurityContextHolder.clearContext();

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(any());
    }
}
