 package com.esports.core.filter;

import org.springframework.util.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

 public class MultiReadHttpServletRequest extends HttpServletRequestWrapper {

     private final Map<String, String[]> parameterMap;

     private final byte[] body;

     public MultiReadHttpServletRequest(HttpServletRequest request) throws IOException {
         super(request);
         parameterMap = request.getParameterMap();
         String sessionStream = getBodyString(request);
         if(!StringUtils.isEmpty(sessionStream)) {
             body = sessionStream.getBytes(StandardCharsets.UTF_8);
         }else {
             body = new byte[] {};
         }
     }

     @Override
     public Enumeration<String> getParameterNames() {
         Vector<String> vector = new Vector<>(parameterMap.keySet());
         Vector<String> values = new Vector<String>(parameterMap.keySet());
         if(!vector.isEmpty()) {
             Iterator<String> iterator = vector.iterator();
             while(iterator.hasNext()) {
                 values.add(modify(iterator.next()));
             }
         }
         return values.elements();
     }

     @Override
     public Object getAttribute(String name) {
         Object value = super.getAttribute(name);
         if (null != value && value instanceof String) {
             value = modify((String) value);
         }
         return value;
     }

     @Override
     public String getParameter(String name) {
         String[] results = parameterMap.get(name);
         if (results == null || results.length <= 0)
             return null;
         else {
             return modify(results[0]);
         }
     }

     @Override
     public String[] getParameterValues(String name) {
         String[] results = parameterMap.get(name);
         if (results == null || results.length <= 0)
             return null;
         else {
             int length = results.length;
             for (int i = 0; i < length; i++) {
                 results[i] = modify(results[i]);
             }
             return results;
         }
     }

     /**
      * 自定义的一个简单修改原参数的方法，即：给原来的参数值前面添加了一个修改标志的字符串
      *
      * @param string
      *            原参数值
      * @return 修改之后的值 ,这里并不进行改变
      */
     private String modify(String value) {
         value = stripXSS(value);
         return value;
     }

     public String getBodyString(final ServletRequest request) {
         StringBuilder sb = new StringBuilder();
         InputStream inputStream = null;
         BufferedReader reader = null;
         try {
             inputStream = cloneInputStream(request.getInputStream());
             if(null == inputStream){
                 return "";
             }
             reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             String line = "";
             while ((line = reader.readLine()) != null) {
                 sb.append(line);
             }
         } catch (IOException e) {
             e.printStackTrace();
         } finally {
             if (inputStream != null) {
                 try {
                     inputStream.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
             if (reader != null) {
                 try {
                     reader.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
         }
         return sb.toString();
     }

     /**
      * 复制输入流
      */
     private InputStream cloneInputStream(ServletInputStream inputStream) {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         byte[] buffer = new byte[1024];
         int len;
         try {
             if(null != inputStream){
                 while ((len = inputStream.read(buffer)) > -1) {
                     byteArrayOutputStream.write(buffer, 0, len);
                 }
             }
             byteArrayOutputStream.flush();
         } catch (IOException e) {
             e.printStackTrace();
         }
         return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
     }

     @Override
     public ServletInputStream getInputStream() {
         final ByteArrayInputStream bais = new ByteArrayInputStream(body);
         return new ServletInputStream() {
             @Override
             public int read() {
                 return bais.read();
             }

             @Override
             public boolean isFinished() {
                 return false;
             }

             @Override
             public boolean isReady() {
                 return false;
             }

             @Override
             public void setReadListener(ReadListener listener) {

             }
         };
     }

     @Override
     public BufferedReader getReader() throws IOException {
         return new BufferedReader(new InputStreamReader(getInputStream()));
     }

     private String stripXSS(String value) {
         if(StringUtils.isEmpty(value)) {
             // Avoid anything in a <script>alert(a);</script>" type of e­xpression
             Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
             value = scriptPattern.matcher(value).replaceAll("");
             // 删除单个的 </script> 标签
             scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
             value = scriptPattern.matcher(value).replaceAll("");
             // 删除单个的<script ...> 标签
             scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
             value = scriptPattern.matcher(value).replaceAll("");

             // Avoid eval(...) e­xpressions
             scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
             value = scriptPattern.matcher(value).replaceAll("");
             // Avoid e­xpression(...) e­xpressions
             scriptPattern = Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
             value = scriptPattern.matcher(value).replaceAll("");

             // Avoid javascript:... e­xpressions
             scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
             value = scriptPattern.matcher(value).replaceAll("");
             // Avoid vbscript:... e­xpressions
             scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
             value = scriptPattern.matcher(value).replaceAll("");
             // Avoid onload= e­xpressions
             scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
             value = scriptPattern.matcher(value).replaceAll("");
         }
         return value;

     }


 }
