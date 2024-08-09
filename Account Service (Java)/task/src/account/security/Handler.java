package account.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import java.io.IOException;
import java.time.LocalDateTime;

public class Handler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("{ " +
                "\"timestamp\":\"" + LocalDateTime.now() +"\"," +
                "\"status\":403," +
                "\"error\":\"Forbidden\"," +
                "\"message\":\"Access Denied!\"," +
                "\"path\":\"" + request.getRequestURI() +"\"" +
                "}");
    }
}