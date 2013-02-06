package com.atex.ps.workoliday.http;

public interface HttpResourceManagerSettingsMBean {

    int getSocketTimeout();
    int getConnectionTimeout();
    int getMaxConnections();
    
    void setSocketTimeout(int timeout);
    void setConnectionTimeout(int timeout);
    void setMaxConnections(int maxConnections);
    
    
}
