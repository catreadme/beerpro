package ch.beerpro.presentation.details.createrating;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import java.util.Arrays;
import java.util.List;
import ch.beerpro.R;

public class LocationDialog extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "LocationDialog";

    ListView places;
    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocProvClient;
    private Location lastKnownLoc;

    private final LatLng hsr = new LatLng(47.2233607,8.815174);
    private static final int DEFAULT_ZOOM = 17;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locPermissionGranted;

    private static final int MAX_LIST_ENTRIES = 5;
    private String[] placeNames;
    private String[] placeAddresses;
    private String[] placeAttributions;
    private LatLng[] placeCoordinates;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_dialog);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.location_dialog_map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        places = findViewById(R.id.listPlaces);

        String apiKey = getString(R.string.google_maps_key);
        Places.initialize(getApplicationContext(), apiKey);
        placesClient = Places.createClient(this);
        fusedLocProvClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_location, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_geolocate) {
            pickCurrentPlace();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getLocationPermission() {
        locPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locPermissionGranted = true;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        
        LatLng hsr = new LatLng(47.2233607,8.815174);
        this.googleMap.addMarker(new MarkerOptions().position(hsr).title("HSR"));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(hsr));

        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        getLocationPermission();
    }

    private void getCurrentPlaceLikelihoods() {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        final FindCurrentPlaceRequest req = FindCurrentPlaceRequest.builder(placeFields).build();
        Task<FindCurrentPlaceResponse> res = placesClient.findCurrentPlace(req);
        res.addOnCompleteListener(this,
                task -> {
                    if (task.isSuccessful()) {
                        FindCurrentPlaceResponse response = task.getResult();
                        int placesCount;
                        if (response.getPlaceLikelihoods().size() < MAX_LIST_ENTRIES) {
                            placesCount = response.getPlaceLikelihoods().size();
                        } else {
                            placesCount = MAX_LIST_ENTRIES;
                        }

                        int i = 0;
                        placeNames = new String[placesCount];
                        placeAddresses = new String[placesCount];
                        placeAttributions = new String[placesCount];
                        placeCoordinates = new LatLng[placesCount];

                        for (PlaceLikelihood likelihood : response.getPlaceLikelihoods()) {
                            Place place = likelihood.getPlace();

                            placeNames[i] = place.getName();
                            placeAddresses[i] = place.getAddress();
                            placeAttributions[i] = (place.getAttributions() == null) ? null : String.join(" ", place.getAttributions());
                            placeCoordinates[i] = place.getLatLng();

                            String currLatLng = (placeCoordinates[i] == null) ? "" : placeCoordinates[i].toString();

                            Log.i(TAG, String.format("Place " + place.getName() + " has likelihood: " + likelihood.getLikelihood() + " at " + currLatLng));

                            i++;
                            if (i > (placesCount - 1)) {
                                break;
                            }
                        }
                        fillPlacesList();
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        }
                    }
                });
    }

    private void getDeviceLocation() {
        try {
            if (locPermissionGranted) {
                Task<Location> locationResult = fusedLocProvClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        lastKnownLoc = task.getResult();
                        Log.d(TAG, "Latitude: " + lastKnownLoc.getLatitude());
                        Log.d(TAG, "Longitude: " + lastKnownLoc.getLongitude());

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLoc.getLatitude(), lastKnownLoc.getLongitude()), DEFAULT_ZOOM));
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hsr, DEFAULT_ZOOM));
                    }
                    getCurrentPlaceLikelihoods();
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void pickCurrentPlace() {
        if (googleMap == null) {
            return;
        }

        if (locPermissionGranted) {
            getDeviceLocation();
        } else {
            Log.i(TAG, "The user did not grant location permission.");
            googleMap.addMarker(new MarkerOptions()
                    .title("Default Location")
                    .position(hsr)
                    .snippet("No places found, because location permission is disabled."));
            getLocationPermission();
        }
    }

    private AdapterView.OnItemClickListener listClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            LatLng markerLatLng = placeCoordinates[position];
            String markerSnippet = placeAddresses[position];
            if (placeAttributions[position] != null) {
                markerSnippet = markerSnippet + "\n" + placeAttributions[position];
            }

            googleMap.addMarker(new MarkerOptions()
                    .title(placeNames[position])
                    .position(markerLatLng)
                    .snippet(markerSnippet));

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(markerLatLng));
        }
    };

    private void fillPlacesList() {
        ArrayAdapter<String> placesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, placeNames);
        places.setAdapter(placesAdapter);
        places.setOnItemClickListener(listClickedHandler);
    }
}
