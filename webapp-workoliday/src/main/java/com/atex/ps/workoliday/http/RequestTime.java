package com.atex.ps.workoliday.http;

public class RequestTime implements Comparable<RequestTime> {

    String url;
    long time;
    long numReq = 0;
   
    
    public RequestTime(String url, long time) {
        this.url = url;
        this.time = time;
        numReq = 1;
    }

    public String getUrl() {
        return url;
    }
    
    public long getTime() {
        return time;
    }
    
    public long getNumReq() {
        return numReq;
    }
                   
    public void addTime(long time) {
        this.time += time;
        this.numReq++;
    }
    
    public long getAvgTime() {
        return time/numReq;
    }

    @Override
    public int compareTo(RequestTime o) {
        return (int)(o.getTime() - this.getTime());
    }
}
