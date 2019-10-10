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
import ch.beerpro.domain.models.FridgeBeer;
import ch.beerpro.domain.utils.FirestoreQueryLiveData;
import ch.beerpro.domain.utils.FirestoreQueryLiveDataArray;

import static androidx.lifecycle.Transformations.map;
import static androidx.lifecycle.Transformations.switchMap;
import static ch.beerpro.domain.utils.LiveDataExtensions.combineLatest;

public class FridgeRepository {
    private static LiveData<List<FridgeBeer>> getFridgeBeersByUser(String userId) {
        return new FirestoreQueryLiveDataArray<>(
                FirebaseFirestore.getInstance().collection(FridgeBeer.COLLECTION).orderBy(
                        FridgeBeer.FIELD_ADDED_AT,
                        Query.Direction.ASCENDING).whereEqualTo(FridgeBeer.FIELD_USER_ID,
                        userId
                ),
                FridgeBeer.class
        );
    }

    private static LiveData<FridgeBeer> getUserFridgeFor(Pair<String, Beer> input) {
        String userId = input.first;
        Beer beer = input.second;
        DocumentReference document = FirebaseFirestore.getInstance().collection(FridgeBeer.COLLECTION)
                .document(FridgeBeer.generateId(userId, beer.getId()));

        return new FirestoreQueryLiveData<>(document, FridgeBeer.class);
    }

    public Task<Void> toggleUserFridgeItem(String userId, String itemId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String fridgeBeerId = FridgeBeer.generateId(userId, itemId);
        DocumentReference fridgeBeerEntryQuery = db.collection(FridgeBeer.COLLECTION).document(fridgeBeerId);

        return fridgeBeerEntryQuery.get().continueWithTask(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                return fridgeBeerEntryQuery.delete();
            } else if (task.isSuccessful()) {
                return fridgeBeerEntryQuery.set(new FridgeBeer(userId, itemId, 1, new Date()));
            } else {
                throw task.getException();
            }
        });
    }

    public LiveData<List<Pair<FridgeBeer, Beer>>> getMyFridgeWithBeers(
            LiveData<String> currentUserId,
            LiveData<List<Beer>> allBeers
    ) {
        return map(combineLatest(getMyFridge(currentUserId), map(allBeers, Entity::entitiesById)), input -> {
            List<FridgeBeer> fridgeBeers = input.first;
            HashMap<String, Beer> beersById = input.second;
            ArrayList<Pair<FridgeBeer, Beer>> result = new ArrayList<>();

            for (FridgeBeer fridgeBeer : fridgeBeers) {
                Beer beer = beersById.get(fridgeBeer.getBeerId());
                result.add(Pair.create(fridgeBeer, beer));
            }

            return result;
        });
    }

    public LiveData<List<FridgeBeer>> getMyFridge(LiveData<String> currentUserId) {
        return switchMap(currentUserId, FridgeRepository::getFridgeBeersByUser);
    }

    public LiveData<FridgeBeer> getMyFridgeBeerForBeer(LiveData<String> currentUserId, LiveData<Beer> beer) {
        return switchMap(combineLatest(currentUserId, beer), FridgeRepository::getUserFridgeFor);
    }
}
