package uk.ac.tees.w9312536.PodMe.ui.downloads;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import uk.ac.tees.w9312536.PodMe.data.DownloadEntry;
import uk.ac.tees.w9312536.PodMe.model.rss.Enclosure;
import uk.ac.tees.w9312536.PodMe.model.rss.Item;
import uk.ac.tees.w9312536.PodMe.model.rss.ItemImage;
import uk.ac.tees.w9312536.PodMe.ui.nowplaying.NowPlayingActivity;
import uk.ac.tees.w9312536.PodMe.utilities.Constants;
import uk.ac.tees.w9312536.PodMe.utilities.InjectorUtils;
import uk.ac.tees.w9312536.PodMe.utilities.PodCastUtils;
import uk.ac.tees.w9312536.bukolafatunde.R;
import uk.ac.tees.w9312536.bukolafatunde.databinding.FragmentDownloadsBinding;
import uk.ac.tees.w9312536.PodMe.service.PodcastService;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadsFragment extends Fragment implements DownloadsAdapter.DownloadsAdapterOnClickHandler {

    /** This field is used for data binding */
    private FragmentDownloadsBinding mDownloadsBinding;

    /** Member variable for DownloadsAdapter */
    private DownloadsAdapter mDownloadsAdapter;

    /** Member variable for DownloadsViewModel */
    private DownloadsViewModel mDownloadsViewModel;

    /** Podcast Id, title, and image URL */
    private String mPodcastId;
    private String mPodcastTitle;
    private String mPodcastImage;

    public DownloadsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the data binding layout for this fragment
        mDownloadsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_downloads,
                container, false);
        View rootView = mDownloadsBinding.getRoot();

        // Change the title associated with this fragment
        getActivity().setTitle(getString(R.string.downloads));

        // Create and set the adapter to the RecyclerView
        initAdapter();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Setup ViewModel
        setupViewModel(this.getActivity());
    }

    /**
     * Creates a LinearLayoutManager and DownloadsAdapter, and set them to the RecyclerView.
     */
    private void initAdapter() {
        // A LinearLayoutManager is responsible for measuring and positioning item views within a
        // RecyclerView into a linear list.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        // Set the layout for the RecyclerView to be a linear layout
        mDownloadsBinding.rvDownloads.setLayoutManager(layoutManager);
        // Use this setting to improve performance if you know that changes in content do not
        // change the child layout size in the RecyclerView
        mDownloadsBinding.rvDownloads.setHasFixedSize(true);

        // Initialize the adapter and attach it to the RecyclerView
        mDownloadsAdapter = new DownloadsAdapter(getContext(), this);
        mDownloadsBinding.rvDownloads.setAdapter(mDownloadsAdapter);
    }

    /**
     * Every time the downloaded episode data is updated, updates the UI.
     */
    private void setupViewModel(Context context) {
        // Get the ViewModel from the factory
        DownloadsViewModelFactory downloadsFactory =
                InjectorUtils.provideDownloadsViewModelFactory(context);
        mDownloadsViewModel = ViewModelProviders.of(this, downloadsFactory)
                .get(DownloadsViewModel.class);

        // Observe changes in the list of DownloadEntries
        mDownloadsViewModel.getDownloads().observe(getViewLifecycleOwner(), new Observer<List<DownloadEntry>>() {
            @Override
            public void onChanged(@Nullable List<DownloadEntry> downloadEntries) {
                if (downloadEntries != null && downloadEntries.size() != 0) {
                    // Make the view for downloads visible
                    mDownloadsBinding.setHasDownloads(true);

                    mDownloadsAdapter.setDownloadEntries(downloadEntries);
                } else {
                    // When the download list is empty, show an empty view.
                    showEmptyView();
                }
            }
        });
    }

    /**
     * This is where we receive our callback from {@link DownloadsAdapter.DownloadsAdapterOnClickHandler}.
     *
     * This callback is invoked when you click on an item in the list.
     * Once the Intent has been created, starts the NowPlayingActivity and the PodcastService.
     *
     * @param downloadEntry DownloadEntry the user clicked
     * @param imageView The shared element
     */
    @Override
    public void onItemClick(DownloadEntry downloadEntry, ImageView imageView) {
        Item item = getItem(downloadEntry);
        // Update the episode data using SharedPreferences each time the user selects the episode
        PodCastUtils.updateSharedPreference(this.getContext(), item,
                mPodcastTitle, PodCastUtils.getItemImageUrl(item, mPodcastImage));
        // Send an update broadcast message to the app widget
        PodCastUtils.sendBroadcastToWidget(this.getContext());

        Intent intent = new Intent(this.getActivity(), NowPlayingActivity.class);
        // Wrap the parcelable into a bundle
        Bundle b = new Bundle();
        b.putParcelable(Constants.EXTRA_ITEM, item);
        // Pass the bundle through intent
        intent.putExtra(Constants.EXTRA_ITEM, b);
        // Pass podcast id
        intent.putExtra(Constants.EXTRA_RESULT_ID, mPodcastId);
        // Pass podcast title
        intent.putExtra(Constants.EXTRA_RESULT_NAME, mPodcastTitle);
        // Pass the podcast image URL. If there is no item image, use this podcast image.
        intent.putExtra(Constants.EXTRA_PODCAST_IMAGE, mPodcastImage);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Apply the shared element transition to the podcast image
            String transitionName = imageView.getTransitionName();
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this.getActivity(),
                    imageView,
                    transitionName
            ).toBundle();
            startActivity(intent, bundle);
        } else {
            // Once the Intent has been created, start the NowPlayingActivity
            startActivity(intent);
        }

        Intent serviceIntent = new Intent(this.getActivity(), PodcastService.class);
        // Set the action to check if the old player should be released in PodcastService
        serviceIntent.setAction(Constants.ACTION_RELEASE_OLD_PLAYER);
        serviceIntent.putExtra(Constants.EXTRA_ITEM, b);
        // Pass podcast title and podcast image
        serviceIntent.putExtra(Constants.EXTRA_RESULT_NAME, mPodcastTitle);
        serviceIntent.putExtra(Constants.EXTRA_PODCAST_IMAGE, mPodcastImage);
        getActivity().startService(serviceIntent);
    }

    /**
     * Returns item used to intent extra.
     * @param downloadEntry DownloadEntry that the user clicks
     */
    private Item getItem(DownloadEntry downloadEntry) {
        mPodcastId = downloadEntry.getPodcastId();
        mPodcastTitle = downloadEntry.getTitle();
        mPodcastImage = downloadEntry.getArtworkImageUrl();

        String itemTitle = downloadEntry.getItemTitle();
        String itemDescription = downloadEntry.getItemDescription();
        String iTunesSummary = downloadEntry.getItemDescription();
        String pubDate = downloadEntry.getItemPubDate();
        String duration = downloadEntry.getItemDuration();

        String enclosureUrl = downloadEntry.getItemEnclosureUrl();
        String enclosureType = downloadEntry.getItemEnclosureType();
        String enclosureLength = downloadEntry.getItemEnclosureLength();
        Enclosure enclosure = new Enclosure(enclosureUrl, enclosureType, enclosureLength);
        List<Enclosure> enclosures = new ArrayList<>();
        enclosures.add(enclosure);

        String itemImageUrl = downloadEntry.getItemImageUrl();
        ItemImage itemImage = new ItemImage(itemImageUrl);
        List<ItemImage> itemImages = new ArrayList<>();
        itemImages.add(itemImage);

        return new Item(itemTitle, itemDescription, iTunesSummary, pubDate, duration, enclosures, itemImages);
    }

    /**
     * When the download list is empty, shows an empty view.
     */
    private void showEmptyView() {
        // Show an empty view
        mDownloadsBinding.setHasDownloads(false);
        // Set text programmatically in order to make text invisible when the user changes the menu
        // items in the navigation drawer
        mDownloadsBinding.tvEmptyDownloads.setText(getString(R.string.empty_downloads));
        // Set the download icon above the text
        setDrawable();
    }

    /**
     * Sets the download icon above the text.
     * Reference: @see "https://stackoverflow.com/questions/4919703/how-to-set-property-androiddrawabletop-of-a-button-at-runtime"
     */
    private void setDrawable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // API level 17
            mDownloadsBinding.tvEmptyDownloads.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    Constants.NO_DRAWABLES, R.drawable.ic_menu_download, Constants.NO_DRAWABLES, Constants.NO_DRAWABLES);
        } else {
            mDownloadsBinding.tvEmptyDownloads.setCompoundDrawablesWithIntrinsicBounds(
                    Constants.NO_DRAWABLES, R.drawable.ic_menu_download, Constants.NO_DRAWABLES, Constants.NO_DRAWABLES);
        }
    }
}


