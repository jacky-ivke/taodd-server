package com.esports.network;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * HttpClient?????????
 */
@Slf4j
public class HttpClientUtils {

    static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    private static CloseableHttpClient httpClient;

    private static final PoolingHttpClientConnectionManager cm;

    private static final PoolingNHttpClientConnectionManager connManager;

    private static CloseableHttpAsyncClient asyncClient;

    /**
     * ??????????????????????????????????????????
     */
    private static final int CONNECT_TIMEOUT = 5 * 1000;

    /**
     * ?????????connect Manager(?????????)??????Connection ???????????????????????????
     */
    private static final int REQUEST_TIMEOUT = 5 * 1000;

    /**
     * ?????????????????????????????????(???????????????)??????????????????
     */
    private static final int SOCKET_TIMEOUT = 10 * 1000;

    /**
     * ???????????????????????????
     */
    private static final int MAX_TOTAL = 200;

    /**
     * ??????????????????????????????????????????
     */
    private static final int MAX_PER_ROTUE = 20;

    /**
     * ???????????????????????????
     */
    private static final int INACTIVITY = 1000;

    private static final ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {

        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            // Honor 'keep-alive' header
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    try {
                        return Long.parseLong(value) * 1000;
                    } catch (NumberFormatException ignore) {
                        log.error("format KeepAlive timeout exception, exception:" + ignore.toString());
                    }
                }
            }
            return 20 * 1000;
        }
    };

    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(CONNECT_TIMEOUT)
            .setConnectionRequestTimeout(REQUEST_TIMEOUT)
            .setSocketTimeout(SOCKET_TIMEOUT)
            .build();

    static {
        cm = initCloseableHttpClient();
        connManager = initCloseableHttpAsyncClient();
    }

    public static PoolingNHttpClientConnectionManager initCloseableHttpAsyncClient() {
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().
                setIoThreadCount(Runtime.getRuntime().availableProcessors())
                .setSoKeepAlive(true)
                .build();
        ConnectingIOReactor ioReactor = null;
        try {
            ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
        } catch (IOReactorException e) {
            e.printStackTrace();
        }
        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor);
        connManager.setMaxTotal(MAX_TOTAL);
        connManager.setDefaultMaxPerRoute(MAX_PER_ROTUE);
        if (null == asyncClient) {
            asyncClient = HttpAsyncClients.custom().
                    setConnectionManager(connManager)
                    .setDefaultRequestConfig(requestConfig)
                    .build();
        }
        return connManager;
    }

    public static PoolingHttpClientConnectionManager initCloseableHttpClient() {
        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .setSoTimeout(SOCKET_TIMEOUT)
                .setSoReuseAddress(true)
                .build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setValidateAfterInactivity(INACTIVITY);
        cm.setMaxTotal(MAX_TOTAL);
        cm.setDefaultMaxPerRoute(MAX_PER_ROTUE);
        cm.setDefaultConnectionConfig(ConnectionConfig.custom().setCharset(Charsets.UTF_8).build());
        cm.setDefaultSocketConfig(socketConfig);
        if (null == httpClient) {
            httpClient = HttpClients.custom().setRetryHandler(requestRetryHandlerConfig())
                    .setKeepAliveStrategy(myStrategy).setConnectionManager(cm).setConnectionManagerShared(true).build();
        }
        return cm;
    }

    public static HttpRequestRetryHandler requestRetryHandlerConfig() {
        // ??????????????????
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,
                                        int executionCount, HttpContext context) {
                if (executionCount >= 3) {// ?????????????????????3???????????????
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// ????????????????????????????????????????????????
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// ????????????SSL????????????
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// ??????
                    return false;
                }
                if (exception instanceof UnknownHostException) {// ????????????????????????
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// ???????????????
                    return false;
                }
                if (exception instanceof SSLException) {// SSL????????????
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // ??????????????????????????????????????????
                return !(request instanceof HttpEntityEnclosingRequest);
            }
        };
        return httpRequestRetryHandler;
    }

    public static String get(String url) {
        return HttpClientUtils.get(url, null, null, null);
    }

    public static String get(String url, String charset) {
        return HttpClientUtils.get(url, null, null, charset);
    }

    public static String get(String url, Map<String, Object> params) {
        return HttpClientUtils.get(url, null, params, null);
    }

    public static String get(String url, Map<String, Object> headers, Map<String, Object> params) {
        return HttpClientUtils.get(url, headers, params, null);
    }

    public static String get(String url, Map<String, Object> headers, Map<String, Object> params, String encoded) {
        CloseableHttpResponse response = null;
        BufferedReader in = null;
        String result = "";
        Charset charset = StringUtils.isEmpty(encoded) ? Charsets.UTF_8 : Charset.forName(encoded);
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            if (!CollectionUtils.isEmpty(params)) {
                Set<Entry<String, Object>> entrySet = params.entrySet();
                for (Entry<String, Object> entry : entrySet) {
                    uriBuilder.setParameter(entry.getKey(), null != entry.getValue() ? entry.getValue().toString() : "");
                }
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            HttpClientUtils.packageConfig(httpGet);
            HttpClientUtils.packageHeader(headers, httpGet);
            response = httpClient.execute(httpGet);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            result = sb.toString();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            log.error("HttpClientUtils do get faild:{}", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("HttpClientUtils do get faild:{}", e.getMessage());
        } catch (URISyntaxException e) {
            log.error("HttpClientUtils do get faild:{}", e.getMessage());
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (null != response) {
                try {
                    response.close();
                } catch (Exception e2) {
                }
            }
        }
        return result;
    }

    public static void packageConfig(HttpRequestBase httpMethod) {
        httpMethod.setConfig(requestConfig);
    }

    protected static void packageHeader(Map<String, Object> headers, HttpRequestBase httpMethod) {
        // ???????????????
        if (!CollectionUtils.isEmpty(headers)) {
            Set<Entry<String, Object>> entrySet = headers.entrySet();
            for (Entry<String, Object> entry : entrySet) {
                // ?????????????????????HttpRequestBase?????????
                httpMethod.setHeader(entry.getKey(), entry.getValue().toString());
            }
        } else {
            httpMethod.addHeader("Content-type", "application/json; charset=utf-8");
            httpMethod.setHeader("Accept", "application/json");
        }
    }

    public static String post(String url) {
        return HttpClientUtils.post(url, null, null, null);
    }

    public static String post(String url, String encoded) {
        return HttpClientUtils.post(url, null, null, encoded);
    }

    public static String post(String url, Map<String, Object> params) {
        return HttpClientUtils.post(url, null, params, null);
    }

    public static String post(String url, Map<String, Object> headers, Map<String, Object> params) {
        return HttpClientUtils.post(url, headers, params, null);
    }

    public static String post(String url, Map<String, Object> headers, Map<String, Object> params, String encoded) {
        CloseableHttpResponse response = null;
        BufferedReader in = null;
        String result = "";
        Charset charset = StringUtils.isEmpty(encoded) ? Charsets.UTF_8 : Charset.forName(encoded);
        try {
            HttpPost httpPost = new HttpPost(url);
            if (!CollectionUtils.isEmpty(params)) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                Set<Entry<String, Object>> entrySet = params.entrySet();
                for (Entry<String, Object> entry : entrySet) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), null != entry.getValue() ? entry.getValue().toString() : ""));
                }
                // ??????????????????http?????????
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, charset));
            }
            HttpClientUtils.packageConfig(httpPost);
            HttpClientUtils.packageHeader(headers, httpPost);
            response = httpClient.execute(httpPost);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            result = sb.toString();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            log.error("HttpClientUtils do post faild:{}", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("HttpClientUtils do post faild:{}", e.getMessage());
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (null != response) {
                try {
                    response.close();
                } catch (Exception e2) {
                }
            }
        }
        return result;
    }

    public static String post(String url, String json, Map<String, Object> headers) {
        CloseableHttpResponse response = null;
        BufferedReader in = null;
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            if (!StringUtils.isEmpty(json)) {
                StringEntity strEntity = new StringEntity(json);
                strEntity.setContentEncoding("UTF-8");
                strEntity.setContentType("application/json");
                httpPost.setEntity(strEntity);
            }
            HttpClientUtils.packageConfig(httpPost);
            HttpClientUtils.packageHeader(headers, httpPost);
            response = httpClient.execute(httpPost);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), Charsets.UTF_8));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            result = sb.toString();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            log.error("HttpClientUtils do post faild:{}", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("HttpClientUtils do post faild:{}", e.getMessage());
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (null != response) {
                try {
                    response.close();
                } catch (Exception e2) {
                }
            }
        }
        return result;
    }

    public static String post(String url, Map<String, Object> headers, Map<String, Object> params, String encoded, InputStream keyStore, String keyStorepass) {
        CloseableHttpClient httpClientSSl = HttpClients.custom()
                .setSSLContext(HttpClientUtils.getSSLContext(keyStore, keyStorepass))
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
        CloseableHttpResponse response = null;
        BufferedReader in = null;
        String result = "";
        Charset charset = StringUtils.isEmpty(encoded) ? Charsets.UTF_8 : Charset.forName(encoded);
        try {
            HttpPost httpPost = new HttpPost(url);
            if (!CollectionUtils.isEmpty(params)) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                Set<Entry<String, Object>> entrySet = params.entrySet();
                for (Entry<String, Object> entry : entrySet) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), null != entry.getValue() ? entry.getValue().toString() : ""));
                }
                // ??????????????????http?????????
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, charset));
            }
            HttpClientUtils.packageConfig(httpPost);
            HttpClientUtils.packageHeader(headers, httpPost);
            response = httpClientSSl.execute(httpPost);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            result = sb.toString();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            log.error("HttpClientUtils do post faild:{}", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("HttpClientUtils do post faild:{}", e.getMessage());
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (null != response) {
                try {
                    response.close();
                } catch (Exception e2) {
                }
            }
        }
        return result;
    }

    protected static KeyStore getkeyStore(InputStream in, String password) {
        KeyStore keySotre = null;
        try {
            keySotre = KeyStore.getInstance("PKCS12");
            keySotre.load(in, password.toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (Exception e2) {
            }
        }
        return keySotre;
    }

    protected static SSLContext getSSLContext(InputStream keyStorePath, String keyStorepass) {
        try {
            KeyStore keyStroe = HttpClientUtils.getkeyStore(keyStorePath, keyStorepass);
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStroe, keyStorepass.toCharArray());
            KeyManager[] kms = kmf.getKeyManagers();
            ctx.init(kms, new TrustManager[]{tm}, new SecureRandom());
            return ctx;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ????????????
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static String asyncGet(String url, String encoded) {
        BufferedReader in = null;
        HttpResponse response = null;
        String result = "";
        Charset charset = StringUtils.isEmpty(encoded) ? Charsets.UTF_8 : Charset.forName(encoded);
        try {
            asyncClient.start();
            HttpGet request = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT)
                    .setConnectionRequestTimeout(REQUEST_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
            request.setConfig(requestConfig);
            Future<HttpResponse> future = asyncClient.execute(request, null);
            // ????????????
            response = future.get();
            // result = EntityUtils.toString(response.getEntity().getContent(),
            // Charsets.UTF_8) ????????????????????????????????????????????????,??????????????????
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            result = sb.toString();
        } catch (Exception e) {
            logger.error("HttpClientUtils", e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("HttpClientUtils do get fail:{}", e.getMessage());
                }
            }
        }
        return result;
    }
}