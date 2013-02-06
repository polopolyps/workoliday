package com.atex.ps.workoliday.http;

public class HttpResourceManagerSettings implements HttpResourceManagerSettingsMBean {

    private int connectionTimeout = HttpResourceManager.CONNECTION_TIMEOUT_DEFAULT;
    private int socketTimeout = HttpResourceManager.SOCKET_TIMEOUT_DEFAULT;
    private int maxConnections = HttpResourceManager.MAX_CONNECTIONS_DEFAULT;
        
    @Override
    public int getConnectionTimeout() {
       return connectionTimeout;
    }

    @Override
    public int getSocketTimeout() {
        return socketTimeout;
    }

    @Override
    public int getMaxConnections() {
        return maxConnections;
    }

    @Override
    public void setConnectionTimeout(int timeout) {
        this.connectionTimeout = timeout;
    }

    @Override
    public void setSocketTimeout(int timeout) {
        this.socketTimeout = timeout;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

}
