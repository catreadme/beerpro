package ch.beerpro.data.repositories;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.Manufacturer;
import ch.beerpro.domain.utils.FirestoreQueryLiveData;
import ch.beerpro.domain.utils.FirestoreQueryLiveDataArray;


public class ManufacturersRepository {

    public LiveData<Manufacturer> getManufacturerById(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return new FirestoreQueryLiveData<>(db.collection(Manufacturer.COLLECTION).document(id), Manufacturer.class);
    }

    public LiveData<List<Manufacturer>> getManufacturers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return new FirestoreQueryLiveDataArray<>(db.collection(Manufacturer.COLLECTION), Manufacturer.class);
    }
}
