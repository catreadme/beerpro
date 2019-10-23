package ch.beerpro.presentation.details.createrating;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.gms.maps.model.Marker;
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

    private ListView places;
    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocProvClient;
    private Location lastKnownLoc;

    private final LatLng hsr = new LatLng(47.2233607, 8.815174);
    private static final int DEFAULT_ZOOM = 17;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locPermissionGranted;

    private static final int MAX_LIST_ENTRIES = 5;
    private String[] placeNames;
    private String[] placeAddresses;
    private String[] placeAttributions;
    private LatLng[] placeCoordinates;
    private GoogleMap googleMap;

    private Marker mapMarker;
    private String selectedPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_dialog);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.location_dialog_map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.places = findViewById(R.id.listPlaces);

        String apiKey = getString(R.string.google_maps_key);
        Places.initialize(getApplicationContext(), apiKey);
        this.placesClient = Places.createClient(this);
        this.fusedLocProvClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_location, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_geolocate) {
            this.pickCurrentPlace();
            return true;
        }

        if (item.getItemId() == R.id.action_done) {
            Intent result = new Intent();
            result.putExtra("location", this.selectedPlace);

            this.setResult(Activity.RESULT_OK, result);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void pickCurrentPlace() {
        if (this.googleMap == null) {
            return;
        }

        if (this.locPermissionGranted) {
            this.getDeviceLocation();
        } else {
            Log.i(TAG, "The user did not grant location permission.");
            this.googleMap.addMarker(new MarkerOptions()
                    .title("Default Location")
                    .position(hsr)
                    .snippet("No places found, because location permission is disabled."));
            this.getLocationPermission();
        }
    }

    private void getLocationPermission() {
        this.locPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.locPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getDeviceLocation() {
        try {
            if (locPermissionGranted) {
                Task<Location> locationResult = fusedLocProvClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        this.lastKnownLoc = task.getResult();
                        Log.d(TAG, "Latitude: " + this.lastKnownLoc.getLatitude());
                        Log.d(TAG, "Longitude: " + this.lastKnownLoc.getLongitude());

                        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLoc.getLatitude(), lastKnownLoc.getLongitude()), DEFAULT_ZOOM));
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(this.hsr, DEFAULT_ZOOM));
                    }
                    this.getCurrentPlaceLikelihoods();
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getCurrentPlaceLikelihoods() {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        FindCurrentPlaceRequest req = FindCurrentPlaceRequest.builder(placeFields).build();
        Task<FindCurrentPlaceResponse> res = placesClient.findCurrentPlace(req);
        res.addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FindCurrentPlaceResponse response = task.getResult();
                int placesCount;
                if (response.getPlaceLikelihoods().size() < MAX_LIST_ENTRIES) {
                    placesCount = response.getPlaceLikelihoods().size();
                } else {
                    placesCount = MAX_LIST_ENTRIES;
                }

                int i = 0;
                this.placeNames = new String[placesCount];
                this.placeAddresses = new String[placesCount];
                this.placeAttributions = new String[placesCount];
                this.placeCoordinates = new LatLng[placesCount];

                for (PlaceLikelihood likelihood : response.getPlaceLikelihoods()) {
                    Place place = likelihood.getPlace();

                    this.placeNames[i] = place.getName();
                    this.placeAddresses[i] = place.getAddress();
                    this.placeAttributions[i] = (place.getAttributions() == null) ? null : TextUtils.join(" ", place.getAttributions());
                    this.placeCoordinates[i] = place.getLatLng();

                    String currLatLng = (placeCoordinates[i] == null) ? "" : placeCoordinates[i].toString();
                    Log.i(TAG, "Place " + place.getName() + " has likelihood: " + likelihood.getLikelihood() + " at " + currLatLng);

                    i++;
                    if (i > (placesCount - 1)) {
                        break;
                    }
                }
                this.fillPlacesList();
            } else {
                Exception exception = task.getException();
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                }
            }
        });
    }

    private void fillPlacesList() {
        ArrayAdapter<String> placesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.placeNames);
        this.places.setAdapter(placesAdapter);
        this.places.setOnItemClickListener(listClickedHandler);
    }

    private AdapterView.OnItemClickListener listClickedHandler = (parent, v, position, id) -> {
        LatLng markerLatLng = this.placeCoordinates[position];
        String markerSnippet = this.placeAddresses[position];

        if (this.placeAttributions[position] != null) {
            markerSnippet = markerSnippet + "\n" + this.placeAttributions[position];
        }

        if (this.mapMarker != null) {
            this.mapMarker.remove();
        }

        this.mapMarker = this.googleMap.addMarker(new MarkerOptions()
                .title(this.placeNames[position])
                .position(markerLatLng)
                .snippet(markerSnippet));

        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(markerLatLng));
        this.selectedPlace = this.placeNames[position] + ", " + placeAddresses[position];
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        this.locPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.locPermissionGranted = true;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng hsr = new LatLng(47.2233607, 8.815174);
        this.googleMap.addMarker(new MarkerOptions().position(hsr).title("HSR"));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(hsr));

        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.getLocationPermission();
    }
}
