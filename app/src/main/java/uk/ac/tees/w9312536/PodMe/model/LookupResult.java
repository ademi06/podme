package uk.ac.tees.w9312536.PodMe.model;

import com.google.gson.annotations.SerializedName;

public class LookupResult {

    @SerializedName("feedUrl")
    private String mFeedUrl;

    public String getFeedUrl() {
        return mFeedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        mFeedUrl = feedUrl;
    }
}
