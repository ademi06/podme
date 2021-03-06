package uk.ac.tees.w9312536.PodMe.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * {@link Dao} which provides an api for all data operations with the {@link PodMeDatabase}
 */
@Dao
public interface PodcastDao {

    @Query("SELECT * FROM podcast")
    LiveData<List<PodcastEntry>> loadPodcasts();

    @Query("SELECT * FROM podcast WHERE podcast_id = :podcastId")
    LiveData<PodcastEntry> loadPodcastByPodcastId(String podcastId);

    @Insert
    void insertPodcast(PodcastEntry podcastEntry);

    @Delete
    void deletePodcast(PodcastEntry podcastEntry);


    // FavoriteEntry

    /**
     * Selects all favorite episodes.
     */
    @Query("SELECT * FROM favorite_episodes")
    LiveData<List<FavoriteEntry>> loadFavorites();

    /**
     * Selects episode where the value in column item_enclosure_url is the given enclosure URL.
     * @param url The stream URL for the episode audio file
     */
    @Query("SELECT * FROM favorite_episodes WHERE item_enclosure_url = :url")
    LiveData<FavoriteEntry> loadFavoriteEpisodeByUrl(String url);

    /**
     * Inserts a {@link FavoriteEntry} into the favorite_episodes table.
     * @param favoriteEntry The podcast episode the user wants to add into the favorites.
     */
    @Insert
    void insertFavoriteEpisode(FavoriteEntry favoriteEntry);

    /**
     * Deletes a {@link FavoriteEntry} from the favorite_episodes table.
     * @param favoriteEntry The podcast episode the user wants to delete from the favorites
     */
    @Delete
    void deleteFavoriteEpisode(FavoriteEntry favoriteEntry);


    // DownloadEntry

    /**
     * Selects all downloaded episodes.
     */
    @Query("SELECT * FROM downloaded_episodes")
    LiveData<List<DownloadEntry>> loadDownloads();

    /**
     * Selects downloaded episode where the value in item_enclosure_url is the given enclosure URL.
     * @param enclosureUrl The stream URL for the episode audio file
     */
    @Query("SELECT * FROM downloaded_episodes WHERE item_enclosure_url = :enclosureUrl")
    LiveData<DownloadEntry> loadDownloadedEpisodeByEnclosureUrl(String enclosureUrl);

    /**
     * Inserts a {@link DownloadEntry} into the downloaded_episodes table.
     * @param downloadEntry Podcast episode to download
     */
    @Insert
    void insertDownloadedEpisode(DownloadEntry downloadEntry);

    /**
     * Deletes a {@link DownloadEntry} from the downloaded_episode table.
     * @param downloadEntry Downloaded episode the user wants to delete
     */
    @Delete
    void deleteDownloadedEpisode(DownloadEntry downloadEntry);

    /**
     * Selects downloaded episode where the value in item_enclosure_url is the given enclosure URL.
     * This method is used to avoid inserting the same episode twice.
     * @param url The stream URL for the episode audio file
     */
    @Query("SELECT * FROM downloaded_episodes WHERE item_enclosure_url = :url")
    DownloadEntry syncLoadDownload(String url);
}

