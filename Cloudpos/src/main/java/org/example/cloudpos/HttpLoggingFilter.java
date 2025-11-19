//package org.example.cloudpos;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.util.ContentCachingRequestWrapper;
//import org.springframework.web.util.ContentCachingResponseWrapper;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//
//@Slf4j(topic = "HTTP_LOGGER")
//@Component
//@Order(1) // 시큐리티 필터 이후/이전에 둘지 필요하면 조정
//public class HttpLoggingFilter extends OncePerRequestFilter {
//
//    private static final int MAX_PAYLOAD_LENGTH = 1024;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//
//        ContentCachingRequestWrapper reqWrapper =
//                new ContentCachingRequestWrapper(request);
//        ContentCachingResponseWrapper resWrapper =
//                new ContentCachingResponseWrapper(response);
//
//        long start = System.currentTimeMillis();
//
//        try {
//            filterChain.doFilter(reqWrapper, resWrapper);
//        } finally {
//            long took = System.currentTimeMillis() - start;
//
//            // 요청 정보
//            String method = request.getMethod();
//            String uri = request.getRequestURI();
//            String query = request.getQueryString();
//            String ip = request.getRemoteAddr();
//
//            String requestBody = getRequestBody(reqWrapper);
//            int status = resWrapper.getStatus();
//            String responseBody = getResponseBody(resWrapper);
//
//            // [요청] 로그
//            log.info("[REQ] ip={} {} {}{} body={}",
//                    ip,
//                    method,
//                    uri,
//                    (query != null ? "?" + query : ""),
//                    requestBody
//            );
//
//            // [응답] 로그
//            log.info("[RES] ip={} {} {} -> status={} time={}ms body={}",
//                    ip,
//                    method,
//                    uri,
//                    status,
//                    took,
//                    responseBody
//            );
//
//            // response body를 클라이언트로 다시 복사
//            resWrapper.copyBodyToResponse();
//        }
//    }
//
//    private String getRequestBody(ContentCachingRequestWrapper request) {
//        byte[] buf = request.getContentAsByteArray();
//        if (buf.length == 0) {
//            return "";
//        }
//        String body = new String(buf, StandardCharsets.UTF_8);
//        return trimPayload(body);
//    }
//
//    private String getResponseBody(ContentCachingResponseWrapper response) {
//        byte[] buf = response.getContentAsByteArray();
//        if (buf.length == 0) {
//            return "";
//        }
//        String body = new String(buf, StandardCharsets.UTF_8);
//        return trimPayload(body);
//    }
//
//    private String trimPayload(String body) {
//        if (body.length() > MAX_PAYLOAD_LENGTH) {
//            return body.substring(0, MAX_PAYLOAD_LENGTH) + "...(truncated)";
//        }
//        return body;
//    }
//}