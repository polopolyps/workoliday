package com.atex.ps.workoliday.http;

public interface HttpResourceManagerStatisticsMBean {

    long getLongestSuccessfulRequestTime();
    String getLongestSuccessfulRequestUrl();
    long getNumberOfSuccessfulRequests();
   
    long getAvgSuccessfulRequestTime();
    String getAvgLongestSuccessfulRequestUrl();
    long getAvgLongestSuccessfulRequestNumberOfRequests();
    long getAvgLongestSuccessfulRequestTime();
    
    long getNumberOfFailedRequests();
    
    /**
     * A list of AVGTIME:NUM_REQ:TOT_TIME: URL for the top time consuming URL's according to AVGTIME.
     * @return A list of AVGTIME:NUM_REQ:TOT_TIME: URL for the top time consuming URL's according to AVGTIME.
     */
    String[] getTopList();
    String[] getFailedTopList();
    
    void registerRequest(String url, long time);
    void registerFailedRequest(String url, long time);
    
    void resetStatistics();
    
    
}
