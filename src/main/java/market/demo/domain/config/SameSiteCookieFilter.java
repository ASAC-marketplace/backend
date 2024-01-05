//package market.demo.domain.config;
//
//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpServletResponseWrapper;
//
//import java.io.IOException;
//
//@WebFilter("/*")
//public class SameSiteCookieFilter implements Filter {
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        // 필터 초기화 시 필요한 로직 (필요한 경우)
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper((HttpServletResponse) response) {
//            @Override
//            public void addHeader(String name, String value) {
//                if ("Set-Cookie".equalsIgnoreCase(name)) {
//                    super.addHeader(name, value + "; SameSite=None");
//                } else {
//                    super.addHeader(name, value);
//                }
//            }
//        };
//
//        chain.doFilter(request, wrappedResponse);
//    }
//
//    @Override
//    public void destroy() {
//        // 필터 제거 시 필요한 로직 (필요한 경우)
//    }
//}
