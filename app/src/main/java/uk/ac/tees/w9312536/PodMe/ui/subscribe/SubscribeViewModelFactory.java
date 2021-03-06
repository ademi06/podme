package uk.ac.tees.w9312536.PodMe.ui.subscribe;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import uk.ac.tees.w9312536.PodMe.data.PodMeRepository;


/**
 * Factory method that allows us to create a ViewModel with a constructor that takes a
 * {@link PodMeRepository}, lookupUrl, and id.
 */
public class SubscribeViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final PodMeRepository mRepository;
    private final String mLookupUrl;
    private final String mId;

    public SubscribeViewModelFactory(PodMeRepository repository, String lookupUrl, String id) {
        mRepository = repository;
        mLookupUrl = lookupUrl;
        mId = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new SubscribeViewModel(mRepository, mLookupUrl, mId);
    }
}

