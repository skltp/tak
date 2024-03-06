package se.skltp.tak.web.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class HttpHeaderFilter implements Filter {

    /**
     * Adds custom http headers to all responses
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Content-Security-Policy", "frame-ancestors 'none'");
        httpResponse.setHeader("X-Frame-Options", "DENY");
        filterChain.doFilter(request, response);
    }
}
