package uk.ac.tees.w9312536.bukolafatunde.ui.add;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import uk.ac.tees.w9312536.bukolafatunde.R;
import uk.ac.tees.w9312536.bukolafatunde.databinding.AddPodListItemBinding;
import uk.ac.tees.w9312536.bukolafatunde.model.Result;

public class AddPodcastAdapter extends RecyclerView.Adapter<AddPodcastAdapter.AddPodcastViewHolder> {

    /** Member variable for the list of {@link Result}s */
    private List<Result> mResults;

    /** An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final AddPodcastAdapterOnClickHandler mOnClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface AddPodcastAdapterOnClickHandler {
        void onItemClick(Result result, ImageView imageView);
    }

    /**
     * Constructor for the AddPodcastAdapter that accepts a list of results to display
     *
     * @param results The list of {@link Result}s
     * @param onClickHandler The on-click handler for list item clicks
     */
    public AddPodcastAdapter(List<Result> results, AddPodcastAdapterOnClickHandler onClickHandler) {
        mResults = results;
        mOnClickHandler = onClickHandler;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param parent The ViewGroup that these ViewHolders are contained within.
     * @param viewType If your RecyclerView has more than one type of item (which ours doesn't) you
     *                    can use this viewType integer to provide a different layout.
     * @return A new AddPodcastViewHolder that holds the AddPodListItemBinding
     */
    @NonNull
    @Override
    public AddPodcastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        AddPodListItemBinding addPodListItemBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.add_pod_list_item, parent, false);
        return new AddPodcastViewHolder(addPodListItemBinding);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull AddPodcastViewHolder holder, int position) {
        Result result = mResults.get(position);
        holder.bind(result);
    }

    /**
     * Returns the number of items to display. It is used behind the scenes to help layout our
     * Views and for animations.
     *
     * @return The number of results
     */
    @Override
    public int getItemCount() {
        if (null == mResults) return 0;
        return mResults.size();
    }

    /**
     * This method is to add a list of {@link Result}s
     *
     * @param results The data source of the adapter
     */
    public void addAll(List<Result> results) {
        mResults.clear();
        mResults.addAll(results);
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views for a result list item.
     */
    public class AddPodcastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /** This field is used for data binding */
        AddPodListItemBinding mAddPodListItemBinding;

        /**
         * Constructor for AddPodcastViewHolder
         *
         * @param addPodListItemBinding Used to access the layout's variables and views
         */
        public AddPodcastViewHolder(AddPodListItemBinding addPodListItemBinding) {
            super(addPodListItemBinding.getRoot());
            mAddPodListItemBinding = addPodListItemBinding;

            // Set an onClickListener on the itemView to listen for clicks
            itemView.setOnClickListener(this);
        }

        /**
         * This method will take a Result object as input and use that result to display the
         * appropriate text and the image within a list item.
         *
         * @param result The result object
         */
        void bind(Result result) {
            // Get the artwork URL
            String artworkUrl = result.getArtworkUrl();
            // Use Glide library to upload the artwork image
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.ic_baseline_error);
            Glide.with(itemView.getContext())
                    .load(artworkUrl)
                    .apply(options)
                    .into(mAddPodListItemBinding.imageArtwork);

            // Set the name
            mAddPodListItemBinding.tvName.setText(result.getName());
        }

        /**
         * Called whenever a user clicks on a item in the list.
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Result result = mResults.get(adapterPosition);
            mOnClickHandler.onItemClick(result, mAddPodListItemBinding.imageArtwork);
        }
    }
}
