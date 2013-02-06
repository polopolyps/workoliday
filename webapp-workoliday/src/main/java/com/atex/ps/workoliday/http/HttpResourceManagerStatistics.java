package com.atex.ps.workoliday.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



public class HttpResourceManagerStatistics implements HttpResourceManagerStatisticsMBean {

    private long numberOfRequests = 0L;
    private long numberOfFailedRequests = 0L;
    private long longestRequest = 0L;
    private long sumRequestTime = 0L;
    private String longestRequestUrl;
    private TheLock mutex = new TheLock();
    private Map<String, RequestTime> requestTimeMap = Collections.synchronizedMap(new HashMap<String, RequestTime>());
    private Map<String, RequestTime> failedRequestTimeMap = Collections.synchronizedMap(new HashMap<String, RequestTime>());
    
    class TheLock extends Object {
    }
  
    @Override
    public long getAvgSuccessfulRequestTime() {
        if(numberOfRequests > 0)
            return sumRequestTime/numberOfRequests;
        else
            return -1;
    }

    @Override
    public long getLongestSuccessfulRequestTime() {
        return longestRequest;
    }

    @Override
    public String getLongestSuccessfulRequestUrl() {
        return longestRequestUrl;
    }

    public void registerRequest(String url, long time) {
        numberOfRequests++;
        sumRequestTime += time;
        if(requestTimeMap.containsKey(url)) {
            RequestTime requestTime = requestTimeMap.get(url);
            requestTime.addTime(time);
            requestTimeMap.put(url, requestTime);
        }
        else {
            requestTimeMap.put(url, new RequestTime(url, time));
        }
        
        if(time > longestRequest) {
            synchronized(mutex) {
                longestRequest = time;
                longestRequestUrl = url;
            }
        }
    }


    @Override
    public void registerFailedRequest(String url, long time) {
        numberOfFailedRequests++;
        if(failedRequestTimeMap.containsKey(url)) {
            RequestTime requestTime = failedRequestTimeMap.get(url);
            requestTime.addTime(time);
            failedRequestTimeMap.put(url, requestTime);
        }
        else {
            failedRequestTimeMap.put(url, new RequestTime(url, time));
        }
    }
        
    @Override
    public long getNumberOfSuccessfulRequests() {
        return numberOfRequests;
    }

    @Override
    public long getNumberOfFailedRequests() {
        return numberOfFailedRequests;
    }
    
    @Override
    public String getAvgLongestSuccessfulRequestUrl() {
        RequestTime longest = getLongestRequestTimeOverTime();
        if(longest == null)
            return null;
        else
            return longest.getUrl();
    }


    @Override
    public long getAvgLongestSuccessfulRequestTime() {
        RequestTime longestRequestTimeOverTime = getLongestRequestTimeOverTime();
        if(longestRequestTimeOverTime != null)
            return longestRequestTimeOverTime.getAvgTime();
        else
            return -1;
    }

    @Override
    public long getAvgLongestSuccessfulRequestNumberOfRequests() {
        RequestTime longestRequestTimeOverTime = getLongestRequestTimeOverTime();
        if(longestRequestTimeOverTime != null)
            return longestRequestTimeOverTime.getNumReq();
        else
            return 0;
    }

    private RequestTime getLongestRequestTimeOverTime() {
        Collection<RequestTime> values = requestTimeMap.values();
        if(values.size() > 0) {
            ArrayList<RequestTime> sortedList = new ArrayList<RequestTime>(values);
            Collections.sort(sortedList);
            RequestTime longestAvgRequest = sortedList.get(0);
            return longestAvgRequest;
        }
        return null;
    }

    @Override
    public String[] getTopList() {
        String[] topList = new String[0];
        Collection<RequestTime> values = requestTimeMap.values();
        if(values.size() > 0) {
            ArrayList<RequestTime> sortedList = new ArrayList<RequestTime>(values);
            Collections.sort(sortedList);
            topList = new String[sortedList.size()];
            for (int i=0; i<sortedList.size(); i++) {
                RequestTime reqTime = sortedList.get(i);
                topList[i] = reqTime.getAvgTime() + ":" + reqTime.getNumReq() + ":" + reqTime.getTime() + ": " + reqTime.getUrl();
            }
        }
        return topList;
    }
    
    @Override
    public String[] getFailedTopList() {
        String[] topList = new String[0];
        Collection<RequestTime> values = failedRequestTimeMap.values();
        if(values.size() > 0) {
            ArrayList<RequestTime> sortedList = new ArrayList<RequestTime>(values);
            Collections.sort(sortedList);
            topList = new String[sortedList.size()];
            for (int i=0; i<sortedList.size(); i++) {
                RequestTime reqTime = sortedList.get(i);
                topList[i] = reqTime.getAvgTime() + ":" + reqTime.getNumReq() + ":" + reqTime.getTime() + ": " + reqTime.getUrl();
            }
        }
        return topList;
    }

    @Override
    public void resetStatistics() {
        failedRequestTimeMap.clear();
        requestTimeMap.clear();
    }
}
