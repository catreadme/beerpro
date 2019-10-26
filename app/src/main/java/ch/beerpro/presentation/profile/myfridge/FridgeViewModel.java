package ch.beerpro.presentation.profile.myfridge;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;

import java.util.List;

import ch.beerpro.data.repositories.BeersRepository;
import ch.beerpro.data.repositories.CurrentUser;
import ch.beerpro.data.repositories.WishlistRepository;
import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.Wish;

public class FridgeViewModel extends ViewModel implements CurrentUser {

    private static final String TAG = "FridgeViewModel";

    private final MutableLiveData<String> currentUserId = new MutableLiveData<>();
    private final WishlistRepository wishlistRepository;
    private final BeersRepository beersRepository;

    public FridgeViewModel() {
        wishlistRepository = new WishlistRepository();
        beersRepository = new BeersRepository();

        currentUserId.setValue(getCurrentUser().getUid());
    }

    public LiveData<List<Pair<Wish, Beer>>> getMyFridgeWithBeers() {
        return wishlistRepository.getMyWishlistWithBeers(currentUserId, beersRepository.getAllBeers());
    }

    public Task<Void> toggleItemInFridge(String itemId) {
        return wishlistRepository.toggleUserWishlistItem(getCurrentUser().getUid(), itemId);
    }

}