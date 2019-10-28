package ch.beerpro.data.repositories;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.FirebaseFirestore;

import ch.beerpro.domain.models.Image;
import ch.beerpro.domain.utils.FirestoreQueryLiveData;


public class ImagesRepository {

    public LiveData<Image> getImageById(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return new FirestoreQueryLiveData<>(db.collection(Image.COLLECTION).document(id), Image.class);
    }
}
