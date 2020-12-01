package uk.ac.tees.w9312536.bukolafatunde.analytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * This class contains the general events that apply to the app.
 * Reference: @see "https://support.google.com/firebase/answer/6317498?hl=en&ref_topic=6317484"
 */
public class Analytics {

    private static FirebaseAnalytics sFirebaseAnalytics;

    /**
     * Creates analytics instance.
     */
    public static FirebaseAnalytics getInstance(Context context) {
        if (sFirebaseAnalytics == null) {
            sFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }
        return sFirebaseAnalytics;
    }
    /**
     * This method is triggered when a user has shared content in an app.
     * @param podcastTitle The podcast title
     * @param itemTitle The episode title
     */
    public static void logEventShare(String podcastTitle, String itemTitle) {
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, podcastTitle);
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, itemTitle);

        sFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, params);
    }

    /**
     * This method is triggered when a user searches in the app.
     * @param searchTerm The search query a user entered
     */
    public static void logEventSearch(String searchTerm) {
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SEARCH_TERM, searchTerm);

        sFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, params);
    }

    /**
     * This method is triggered when a user has selected content in an app.
     * @param podcastTitle The podcast title
     * @param itemTitle The episode title
     */
    public static void logEventSelectContent(String podcastTitle, String itemTitle) {
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, podcastTitle);
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, itemTitle);

        sFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);
    }

}
