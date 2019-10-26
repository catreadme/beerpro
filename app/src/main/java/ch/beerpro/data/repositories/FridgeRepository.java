package ch.beerpro.data.repositories;

import android.util.Pair;

import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.Entity;
import ch.beerpro.domain.models.Wish;
import ch.beerpro.domain.utils.FirestoreQueryLiveData;
import ch.beerpro.domain.utils.FirestoreQueryLiveDataArray;

import static androidx.lifecycle.Transformations.map;
import static androidx.lifecycle.Transformations.switchMap;
import static ch.beerpro.domain.utils.LiveDataExtensions.combineLatest;

public class FridgeRepository {

    private static LiveData<List<Wish>> getFridgeItemsByUser(String userId) {
        return new FirestoreQueryLiveDataArray<>(FirebaseFirestore.getInstance().collection(Wish.COLLECTION)
                .orderBy(Wish.FIELD_ADDED_AT, Query.Direction.DESCENDING).whereEqualTo(Wish.FIELD_USER_ID, userId),
                Wish.class);
    }

    private static LiveData<Wish> getUserFridgeFor(Pair<String, Beer> input) {
        String userId = input.first;
        Beer beer = input.second;
        DocumentReference document = FirebaseFirestore.getInstance().collection(Wish.COLLECTION)
                .document(Wish.generateId(userId, beer.getId()));
        return new FirestoreQueryLiveData<>(document, Wish.class);
    }

    public Task<Void> toggleUserFridgeItem(String userId, String itemId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String wishId = Wish.generateId(userId, itemId);

        DocumentReference wishEntryQuery = db.collection(Wish.COLLECTION).document(wishId);

        return wishEntryQuery.get().continueWithTask(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                return wishEntryQuery.delete();
            } else if (task.isSuccessful()) {
                return wishEntryQuery.set(new Wish(userId, itemId, new Date()));
            } else {
                throw task.getException();
            }
        });
    }

    public LiveData<List<Pair<Wish, Beer>>> getMyFridgeWithBeers(LiveData<String> currentUserId,
                                                                   LiveData<List<Beer>> allBeers) {
        return map(combineLatest(getMyFridge(currentUserId), map(allBeers, Entity::entitiesById)), input -> {
            List<Wish> wishes = input.first;
            HashMap<String, Beer> beersById = input.second;

            ArrayList<Pair<Wish, Beer>> result = new ArrayList<>();
            for (Wish wish : wishes) {
                Beer beer = beersById.get(wish.getBeerId());
                result.add(Pair.create(wish, beer));
            }
            return result;
        });
    }

    public LiveData<List<Wish>> getMyFridge(LiveData<String> currentUserId) {
        return switchMap(currentUserId, FridgeRepository::getFridgeItemsByUser);
    }


    public LiveData<Wish> getMyWishForBeer(LiveData<String> currentUserId, LiveData<Beer> beer) {
        return switchMap(combineLatest(currentUserId, beer), FridgeRepository::getUserFridgeFor);
    }

}
