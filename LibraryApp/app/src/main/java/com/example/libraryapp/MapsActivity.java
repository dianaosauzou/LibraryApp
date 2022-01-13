package com.example.libraryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.LocationRequest;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.libraryapp.databinding.ActivityMapsBinding;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    //need to fix the map to allow zooms at a level
    // creating a bundle object

    //Maps activ ity uses the google places api, and displays bookstores closets to the user

    private static final String TAG = "out";
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;

    private RequestQueue requestQueue;
    private Cache cache;
    private Network network;
    Bundle bundle = new Bundle();

    List<Place> markersCopy ;

    //Place Search, Details, Photos, Autocomplete

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings mapSettings;
        mapSettings = mMap.getUiSettings();
        mapSettings.setZoomControlsEnabled(true);
        mapSettings.setCompassEnabled(true);
        mapSettings.setMyLocationButtonEnabled(true);


//        mMap.setOnMarkerClickListener(this);


        final String apiKey = getString(R.string.google_maps_key);
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=book%20store&key=" + apiKey;


        Places.initialize(getApplicationContext(), apiKey);
        PlacesClient placesClient = Places.createClient(this);
        cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    JSONArray itemsArray = new JSONArray();
                    List <Place> markers =  new ArrayList<>();

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            itemsArray = response.getJSONArray("results");
                            for (int x = 0; x < itemsArray.length(); x++) {
                                JSONObject placeObj = itemsArray.getJSONObject(x);

                                    String l = "place_id";
                                    //I just need the place ids

                                        String placeId = String.valueOf(placeObj.get(l));

                                        List<Place.Field> placeFields = Arrays.asList(
                                                Place.Field.LAT_LNG,
                                                Place.Field.NAME,
                                                Place.Field.OPENING_HOURS,
                                                Place.Field.ADDRESS,
                                                Place.Field.RATING,
                                                Place.Field.PHONE_NUMBER,
                                                Place.Field.PHOTO_METADATAS,
                                                Place.Field.ID);
                                        FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.newInstance(placeId, placeFields);

                                        placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener((responses) -> {
                                            Place place = responses.getPlace();
                                            markers.add(place); //when i add markers here it is null everywhere else?
                                            setupMarkers(markers);


                                            //I've loaded the bookstores into the map using the places api


                                            //onClick it should use the TripAdvisor Api to search the name
                                            // and then bring up ratings of the bookstores


                                        }).addOnFailureListener((exception) -> {
                                                    if (exception instanceof ApiException) {
                                                        final ApiException apiException = (ApiException) exception;
                                                        final int statusCode = apiException.getStatusCode();
                                                        // TODO: Handle error with given status code.
                                                    }
                                                }

                                        );


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });


        requestQueue.add(jsonObjectRequest);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoContents(@NonNull Marker marker) {
                return null;
            }

            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {

                LayoutInflater layoutInflater = getLayoutInflater();

                View view = (layoutInflater
                        .inflate(R.layout.custom_info_window, null));

                TextView name_tv = view.findViewById(R.id.name);
                TextView details_tv = view.findViewById(R.id.details);
                TextView phoneNo = view.findViewById(R.id.phoneNo);
                TextView rating = view.findViewById(R.id.rating);
                TextView monday = view.findViewById(R.id.monday);
                TextView tuesday = view.findViewById(R.id.tueday);
                TextView wednesday = view.findViewById(R.id.wednesday);
                TextView thursday = view.findViewById(R.id.thurday);
                TextView friday = view.findViewById(R.id.friday);
                TextView saturday = view.findViewById(R.id.saturday);
                TextView sundau = view.findViewById(R.id.sunday);

                for(Parcelable x: bundle.getParcelableArrayList("List")){
                    Place place = (Place) x;
                    if(place.getId().equalsIgnoreCase(marker.getSnippet())) {

                        if(place.getName()!=null)
                            name_tv.setText(place.getName());
                        else name_tv.setText(" Name is unavailable");
                        if(place.getAddress()!=null)
                            details_tv.setText("Address: "+ place.getAddress());

                        if(place.getPhoneNumber()!=null)
                            phoneNo.setText("Phone number: "+ place.getPhoneNumber());

                        if(place.getRating()!=null)
                            rating.setText("Rating: "+place.getRating().toString());

                        if(place.getOpeningHours()!=null) {
                            monday.setText(place.getOpeningHours().getWeekdayText().get(0));
                            tuesday.setText(place.getOpeningHours().getWeekdayText().get(1));
                            wednesday.setText(place.getOpeningHours().getWeekdayText().get(2));
                            thursday.setText(place.getOpeningHours().getWeekdayText().get(3));
                            friday.setText(place.getOpeningHours().getWeekdayText().get(4));
                            saturday.setText(place.getOpeningHours().getWeekdayText().get(5));
                            sundau.setText(place.getOpeningHours().getWeekdayText().get(6));


                        }
//                        img.setImageURI(place.getPhotoMetadatas().get(0));

                    }
                }


                return view;
            }
        });
        //can I say mMap .add marker
        getLocation();

    }

    private void setupMarkers(List<Place> markers) {

        for (Place b : markers) {
//            System.out.println(detailsArray.toString());

            mMap.addMarker(new MarkerOptions().position(b.getLatLng()).title(b.getName()).snippet(b.getId()));

        }
        ArrayList<Place> detailsArray = new ArrayList<>(markers);

        bundle.putParcelableArrayList("List", detailsArray);



    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "No permission", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
        mMap.setMyLocationEnabled(true);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient = new FusedLocationProviderClient(getApplicationContext());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                double lat = locationResult.getLastLocation().getLatitude();
                double lng = locationResult.getLastLocation().getLongitude();
                LatLng currentlocation = new LatLng(lat, lng);
                displayLocation(currentlocation);
            }
        }, null);
    }

//53.7189 -6.34778

    public void displayLocation(LatLng latlng) {
        if (mMap != null) {
            Geocoder coder = new Geocoder(this);
            try {
                List<Address> locations = coder.getFromLocation(latlng.latitude,
                        latlng.longitude, 1);
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10));

                if (locations != null) {
                    String add1 = locations.get(0).getAddressLine(0);
                    mMap.addMarker(new MarkerOptions().position(latlng).title("Current location").snippet(add1));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Going to use the places api to get the lat/lng of bookstores in
        //current users radius, and onclick will call the tripadvisor api
    }


    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
//        List itemsList = Arrays.asList(marker.getSnippet());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.maps, menu);
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                return true;
            case R.id.back:
                Intent b = new Intent(this, HomeActivity.class);;
                startActivity(b);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
