package com.atex.ps.workoliday.servlet;

public class EventStatistics implements Comparable<EventStatistics> {

    private String contentId;
    private String url;
    private long numReq = 0;

    public EventStatistics(String contentId, String url) {
        this.contentId = contentId;
        this.url = url;
        numReq = 1;
    }

    public long getNumReq() {
        return numReq;
    }

    public void addRequest() {
        this.numReq++;
    }

    @Override
    public int compareTo(EventStatistics o) {
        return (int) (o.numReq - this.numReq);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append(numReq).append(":").append(contentId).append(":")
                .append(url);
        return ret.toString();
    }
}
