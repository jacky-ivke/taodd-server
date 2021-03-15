 package com.esports.core.filter;

 import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

 @WebFilter(urlPatterns = "/*")
 @Component
 public class CachingContentFilter implements Filter{

     @Override
     public void init(FilterConfig filterConfig) throws ServletException {
     }

     @Override
     public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
         HttpServletRequest request = (HttpServletRequest) req;
         HttpServletResponse response = (HttpServletResponse) resp;
         HttpServletResponse responseWrapper = response;
         responseWrapper.addHeader("x-frame-options","SAMEORIGIN");
         String contentType = request.getContentType();
         if (contentType != null && contentType.contains("multipart/form-data")) {
             MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
             MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
             request = multipartRequest;
         }else {
             MultiReadHttpServletRequest requestWrapper = new MultiReadHttpServletRequest(request);
             request = requestWrapper;
         }
         chain.doFilter(request, responseWrapper);
     }

     @Override
     public void destroy() {
     }
 }
