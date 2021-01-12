package uk.ac.tees.w9312536.PodMe.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import uk.ac.tees.w9312536.PodMe.AppExecutors;
import uk.ac.tees.w9312536.PodMe.model.ITunesResponse;
import uk.ac.tees.w9312536.PodMe.model.LookupResponse;
import uk.ac.tees.w9312536.PodMe.model.SearchResponse;
import uk.ac.tees.w9312536.PodMe.model.rss.RssFeed;
import uk.ac.tees.w9312536.PodMe.utilities.ITunesSearchApi;

/**
 * Handles data operations in CandyPod. Acts as a mediator between {@link ITunesSearchApi} and
 * {@link PodcastDao}.
 */
public class PodMeRepository {

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static PodMeRepository sInstance;
    private final PodcastDao mPodcastDao;
    private final ITunesSearchApi mITunesSearchApi;
    private final AppExecutors mExecutors;

    private PodMeRepository(PodcastDao podcastDao,
                            ITunesSearchApi iTunesSearchApi,
                            AppExecutors executors) {
        mPodcastDao = podcastDao;
        mITunesSearchApi = iTunesSearchApi;
        mExecutors = executors;
    }

    public synchronized static PodMeRepository getInstance(
            PodcastDao podcastDao, ITunesSearchApi iTunesSearchApi, AppExecutors executors) {
        Timber.d("Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                Timber.d("Making new repository");
                sInstance = new PodMeRepository(podcastDao, iTunesSearchApi, executors);
            }
        }
        return sInstance;
    }

    /**
     * Make a network request by calling enqueue
     * @param country The two-letter country code
     * @return {@link LiveData} ITunesResponse object
     */
    public LiveData<ITunesResponse> getITunesResponse(String country) {
        final MutableLiveData<ITunesResponse> iTunesResponseData = new MutableLiveData<>();

        mITunesSearchApi.getTopPodcasts(country)
                .enqueue(new Callback<ITunesResponse>() {
                    @Override
                    public void onResponse(Call<ITunesResponse> call, Response<ITunesResponse> response) {
                        if (response.isSuccessful()) {
                            ITunesResponse iTunesResponse = response.body();
                            iTunesResponseData.setValue(iTunesResponse);
                        }
                    }

                    @Override
                    public void onFailure(Call<ITunesResponse> call, Throwable t) {
                        iTunesResponseData.setValue(null);
                        Timber.e("Failed getting iTunesResponse data. " + t.getMessage());
                    }
                });
        return iTunesResponseData;
    }

    /**
     * Make a network request by calling enqueue
     * @param lookupUrl URL for a lookup request
     * @param id The podcast ID
     * @return {@link LiveData} LookupResponse object
     */
    public LiveData<LookupResponse> getLookupResponse(String lookupUrl, String id) {
        final MutableLiveData<LookupResponse> lookupResponseData = new MutableLiveData<>();

        mITunesSearchApi.getLookupResponse(lookupUrl, id)
                .enqueue(new Callback<LookupResponse>() {
                    @Override
                    public void onResponse(Call<LookupResponse> call, Response<LookupResponse> response) {
                        if (response.isSuccessful()) {
                            LookupResponse lookupResponse = response.body();
                            lookupResponseData.setValue(lookupResponse);
                        }
                    }

                    @Override
                    public void onFailure(Call<LookupResponse> call, Throwable t) {
                        lookupResponseData.setValue(null);
                        Timber.e("Failed getting LookupResponse data. " + t.getMessage());
                    }
                });
        return lookupResponseData;
    }

    /**
     * Get a search response data. When the user submits a search query, the SearchResultsActivity
     * will display this data.
     * @param searchUrl URL for a search request
     * @param country The country
     * @param media The media type to search
     * @param term The user's query from the SearchView
     */
    public LiveData<SearchResponse> getSearchResponse(String searchUrl,
                                                      String country, String media, String term) {
        final MutableLiveData<SearchResponse> searchResponseData = new MutableLiveData<>();

        mITunesSearchApi.getSearchResponse(searchUrl, country, media, term)
                .enqueue(new Callback<SearchResponse>() {
                    @Override
                    public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                        if (response.isSuccessful()) {
                            SearchResponse searchResponse = response.body();
                            searchResponseData.setValue(searchResponse);
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchResponse> call, Throwable t) {
                        searchResponseData.setValue(null);
                        Timber.e("Failed getting SearchResponse data. " + t.getMessage());
                    }
                });
        return searchResponseData;
    }

    /**
     * Returns {@link LiveData} RssFeed
     * @param feedUrl The feed URL which has the episode metadata and stream URLs for the audio file
     */
    public LiveData<RssFeed> getRssFeed(String feedUrl) {
        final MutableLiveData<RssFeed> rssFeedData = new MutableLiveData<>();

        mITunesSearchApi.getRssFeed(feedUrl)
                .enqueue(new Callback<RssFeed>() {
                    @Override
                    public void onResponse(Call<RssFeed> call, Response<RssFeed> response) {
                        if (response.isSuccessful()) {
                            RssFeed rssFeed = response.body();
                            rssFeedData.setValue(rssFeed);
                        }
                    }

                    @Override
                    public void onFailure(Call<RssFeed> call, Throwable t) {
                        rssFeedData.setValue(null);
                        Timber.e("Failed getting RssFeed data. " + t.getMessage());
                    }
                });
        return rssFeedData;
    }

    /**
     * Get the podcast by podcast ID from the podcast table.
     * @param podcastId The podcast ID
     * @return {@link LiveData} PodcastEntry from the database.
     */
    public LiveData<PodcastEntry> getPodcastByPodcastId(String podcastId) {
        return mPodcastDao.loadPodcastByPodcastId(podcastId);
    }

    /**
     * Get the list of the podcasts from the podcast table.
     * @return {@link LiveData} list of all {@link PodcastEntry} objects from the database.
     */
    public LiveData<List<PodcastEntry>> getPodcasts() {
        return mPodcastDao.loadPodcasts();
    }

    /**
     * Get the favorite episode by enclosure URL from the favorite_episodes table.
     * @param url The stream URL for the episode audio file
     * @return {@link LiveData} {@link FavoriteEntry} from the database.
     */
    public LiveData<FavoriteEntry> getFavoriteEpisodeByUrl(String url) {
        return mPodcastDao.loadFavoriteEpisodeByUrl(url);
    }

    /**
     * Get the list of the favorite episodes from the favorite_episodes table.
     * @return {@link LiveData} list of {@link FavoriteEntry} objects from the database.
     */
    public LiveData<List<FavoriteEntry>> getFavorites() {
        return mPodcastDao.loadFavorites();
    }

    /**
     * Returns the list of all downloads episodes from downloaded_episodes table.
     */
    public LiveData<List<DownloadEntry>> getDownloads() {
        return mPodcastDao.loadDownloads();
    }

    /**
     * Returns the downloaded episode by enclosure URL.
     * @param enclosureUrl The stream URL for the episode audio file
     */
    public LiveData<DownloadEntry> getDownloadedEpisodeByEnclosureUrl(String enclosureUrl) {
        return mPodcastDao.loadDownloadedEpisodeByEnclosureUrl(enclosureUrl);
    }
}
