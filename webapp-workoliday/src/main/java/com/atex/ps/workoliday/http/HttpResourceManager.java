package com.atex.ps.workoliday.http;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**
 * A Http Resource manager should be used to read content from a resource
 * located outside a web application context. The HttpResourceManager is
 * singleton object that should be used for all resource content reads. It uses
 * a internal thread safe Apache HttpClient to simplify all kinds of
 * communications management.
 * 
 * Socket timeout: 1000 ms Connection timeout: 2000ms Max total connections: 100
 * 
 */
public final class HttpResourceManager {

    private final HttpClient httpClient;
    private final ResponseHandler<byte[]> responseHandler;
    private static final Logger logger = Logger
            .getLogger(HttpResourceManager.class.getName());
    public static HttpResourceManagerStatisticsMBean statisticsMBean = new HttpResourceManagerStatistics();
    public static HttpResourceManagerSettingsMBean settingsMBean = new HttpResourceManagerSettings();

    public static final int CONNECTION_TIMEOUT_DEFAULT = 2000;
    public static final int SOCKET_TIMEOUT_DEFAULT = 2000;
    public static final int MAX_CONNECTIONS_DEFAULT = 100;

    private HttpResourceManager() {
        HttpParams httpParams = new BasicHttpParams();
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));

        httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, settingsMBean
                .getSocketTimeout());
        httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                settingsMBean.getConnectionTimeout());
        ConnManagerParams.setMaxTotalConnections(httpParams, settingsMBean
                .getMaxConnections());

        // --- old comment
        // ConnManagerParams.setMaxConnectionsPerRoute(httpParams, 2);
        ThreadSafeClientConnManager threadSafeClientConnManager = new ThreadSafeClientConnManager(
                httpParams, schemeRegistry);
        httpClient = new DefaultHttpClient(threadSafeClientConnManager,
                httpParams);
        responseHandler = createResponseHandler();
    }

    private static final HttpResourceManager instance = new HttpResourceManager();

    public static HttpResourceManager getInstance() {
        return instance;
    }

    /**
     * Gets the specified resource's content as a byte array.
     * 
     * @param uri
     *            the location of the resource
     * @return the content as a byte array
     * @throws HttpResourceManagerException
     *             if an unexpected error occur
     */
    public byte[] getResource(String uri) throws HttpResourceManagerException {

        HttpGet httpGet = null;

        long startTime = 0L;
        startTime = System.currentTimeMillis();

        try {

            httpGet = new HttpGet(uri);
            byte[] resource = null;
            try {
                resource = httpClient.execute(httpGet, responseHandler);
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "Succeeded to get resource from: ["
                            + uri + "], time spent: "
                            + (System.currentTimeMillis() - startTime) + "ms");
                }

                if (resource != null) {
                    statisticsMBean.registerRequest(uri, System
                            .currentTimeMillis()
                            - startTime);
                } else {
                    statisticsMBean.registerFailedRequest(uri, System
                            .currentTimeMillis()
                            - startTime);
                }

            } catch (InvalidStatusCodeException e) {
                logger.log(Level.WARNING, "Got an invalid status code "
                        + e.getCode() + " from remote request to: " + uri
                        + ". Aborting (returning a null resource).");
                statisticsMBean.registerFailedRequest(uri, System
                        .currentTimeMillis()
                        - startTime);
            }

            return resource;

        } catch (Exception e) {
            String logMessage = "Failed to get resource from: [" + uri
                    + "], time spent: "
                    + (System.currentTimeMillis() - startTime) + "ms";
            logger.log(Level.WARNING, logMessage, e);
            statisticsMBean.registerFailedRequest(uri, System
                    .currentTimeMillis()
                    - startTime);
            if (httpGet != null) {
                httpGet.abort();
            }
            throw new HttpResourceManager.HttpResourceManagerException(
                    logMessage, e);
        }
    }

    /**
     * Creates a response handler that read the response body into byte array.
     * 
     * @return a byte array representing the response body
     */
    protected ResponseHandler<byte[]> createResponseHandler() {

        ResponseHandler<byte[]> responseHandler = new ResponseHandler<byte[]>() {

            public byte[] handleResponse(HttpResponse httpResponse)
                    throws ClientProtocolException, IOException {

                if (200 != httpResponse.getStatusLine().getStatusCode()) {
                    throw new InvalidStatusCodeException(httpResponse
                            .getStatusLine().getStatusCode());
                }

                HttpEntity entity = httpResponse.getEntity();

                if (entity != null) {
                    return EntityUtils.toByteArray(entity);
                }

                return null;
            }
        };

        return responseHandler;
    }

    /**
     * This is exception signals that an exception occurred while processing a
     * http request through the {@link HttpResourceManager}
     */
    @SuppressWarnings("serial")
    public class HttpResourceManagerException extends Exception {

        public HttpResourceManagerException(String message, Throwable rootCause) {
            super(message, rootCause);
        }
    }
}
