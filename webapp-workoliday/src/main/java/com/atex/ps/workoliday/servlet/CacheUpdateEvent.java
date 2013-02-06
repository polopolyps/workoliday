package com.atex.ps.workoliday.servlet;

public class CacheUpdateEvent {
    private String name;
    private String url;
    // Update interval in minutes
    private long updateInterval;
    private long timeUntilNextUpdate;
    private String contentId;
    private long hashValue;

    public CacheUpdateEvent(String contentId, String name, String url,
            int updateInterval, long hashValue) {
        this.name = name;
        this.url = url;
        this.updateInterval = updateInterval;
        this.timeUntilNextUpdate = 0;
        this.contentId = contentId;
        this.hashValue = hashValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public long getUpdateInterval() {
        return updateInterval;
    }

    public long getTimeUntilNextUpdate() {
        return timeUntilNextUpdate;
    }

    public String getContentId() {
        return contentId;
    }

    public void decreaseTimeUntilNextUpdate(int seconds) {
        timeUntilNextUpdate = timeUntilNextUpdate - seconds;
    }

    public void reset() {
        timeUntilNextUpdate = 0;
    }

    public void resetTimeUntilNextUpdate() {
        this.timeUntilNextUpdate = updateInterval * 60L;
    }

    @Override
    public String toString() {
        return "CacheUpdateEvent[" + contentId + " - " + getName() + " ("
                + updateInterval + ")]";
    }

    public long getHashValue() {
        return hashValue;
    }

    public void setHashValue(long hashValue) {
        this.hashValue = hashValue;
    }
}