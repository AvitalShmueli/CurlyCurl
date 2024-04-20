package com.example.curlycurl.ui.explore;

import static android.content.ContentValues.TAG;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.curlycurl.App;
import com.example.curlycurl.Interfaces.Callback_CommunityPostSelected;
import com.example.curlycurl.Interfaces.Callback_ProductPostSelected;
import com.example.curlycurl.Models.CommunityPost;
import com.example.curlycurl.Models.Product;
import com.example.curlycurl.R;
import com.example.curlycurl.Utilities.FirebaseManager;
import com.example.curlycurl.Utilities.SignalManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MapsFragment extends Fragment {
    public enum MapFragmentMode {
        PRODUCTS,
        COMMUNITY
    }

    private GoogleMap mMap;
    private Marker selectedMarker;
    private FirebaseManager firebaseManager;
    private HashMap<Marker, Product> productsOnMap = new HashMap<>();
    private HashMap<Marker, CommunityPost> postsOnMap = new HashMap<>();

    private Callback_ProductPostSelected callbackProductPostSelected;
    private Callback_CommunityPostSelected callbackCommunityPostSelected;
    private MapFragmentMode mode;
    private String searchTerm = "";
    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            LatLng Afeka = new LatLng(App.DEFAULT_LAN, App.DEFAULT_LON);
            //selectedMarker = googleMap.addMarker(new MarkerOptions().position(Afeka).title("Marker in Afeka"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(Afeka));
            Log.d(TAG, "map - onMapReady");

            showSearchResult();
        }
    };

    private void showSearchResult() {
        if (mode == MapFragmentMode.PRODUCTS)
            showProductsSearchResults();
        else
            showCommunitySearchResults();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (callbackProductPostSelected != null)
                    callbackProductPostSelected.onProductPostSelected(productsOnMap.get(marker));
                else if (callbackCommunityPostSelected != null)
                    callbackCommunityPostSelected.onCommunityPostSelected_comment(postsOnMap.get(marker));
                else
                    SignalManager.getInstance().toast("Something went wrong");
                return false;
            }
        });

    }


    private void showProductsSearchResults() {
        Query query;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            query = firebaseManager.getRefProductsCollection().where(
                    Filter.or(
                            Filter.and(
                                    Filter.greaterThanOrEqualTo("productName", searchTerm),
                                    Filter.lessThan("productName", searchTerm + 'z')
                            ),
                            Filter.equalTo("productType", searchTerm.toUpperCase()),
                            Filter.and(
                                    Filter.greaterThanOrEqualTo("userName", searchTerm),
                                    Filter.lessThan("userName", searchTerm + 'z')
                            ),
                            Filter.arrayContains("tags",searchTerm)
                    )
            ).orderBy("created", Query.Direction.DESCENDING);
        } else {
            query = firebaseManager.getRefProductsCollection();
        }
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore error", error.getMessage());
                    return;
                }

                if (value.getDocumentChanges().size() == 0) {
                    SignalManager.getInstance().toast("No results");
                    mMap.clear();
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        //productList.add((dc.getDocument().toObject(Product.class)));
                        showMarkerByAddress((dc.getDocument().toObject(Product.class)));
                    }
                }
            }
        });
    }

    private void showCommunitySearchResults() {
        Query query;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            query = firebaseManager.getRefCommunityPostsCollection().where(
                    Filter.or(
                            Filter.and(
                                    Filter.greaterThanOrEqualTo("userName", searchTerm),
                                    Filter.lessThan("userName", searchTerm + 'z')
                            ),
                            Filter.arrayContains("tags",searchTerm)
                    )
            ).orderBy("created", Query.Direction.DESCENDING);
        } else {
            query = firebaseManager.getRefCommunityPostsCollection();
        }
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore error", error.getMessage());
                    return;
                }

                if (value.getDocumentChanges().size() == 0) {
                    SignalManager.getInstance().toast("No results");
                    mMap.clear();
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        showMarkerByAddress((dc.getDocument().toObject(CommunityPost.class)));
                    }
                }
            }
        });
    }

    private void showMarkerByAddress(Product product) {
        Geocoder geocoder = new Geocoder(getContext());
        if(!productsOnMap.containsValue(product)) {
            try {
                List<Address> addressList = geocoder.getFromLocationName(product.getCity(), 1);
                if (!addressList.isEmpty()) {
                    Address location = addressList.get(0);
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Marker newMarker = mMap.addMarker(
                            new MarkerOptions()
                                    .position(latLng)
                                    .title(product.getProductName())
                                    .snippet(product.getUserName())
                                    .contentDescription(product.getProductId())
                    );
                    productsOnMap.put(newMarker, product);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void showMarkerByAddress(CommunityPost post) {
        Geocoder geocoder = new Geocoder(getContext());
        if(!postsOnMap.containsValue(post)) {
            try {
                List<Address> addressList = geocoder.getFromLocationName(post.getCity(), 1);
                if (!addressList.isEmpty()) {
                    Address location = addressList.get(0);
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Marker newMarker = mMap.addMarker(
                            new MarkerOptions()
                                    .position(latLng)
                                    .title(post.getUserName())
                                    .snippet(post.getPost())
                                    .contentDescription(post.getPostId())
                    );
                    postsOnMap.put(newMarker, post);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public MapsFragment() {
        this.mode = MapFragmentMode.PRODUCTS;
    }

    public MapsFragment setMode(MapFragmentMode mode) {
        this.mode = mode;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        Log.d(TAG,"Map fragment - onViewCreated");
        firebaseManager = FirebaseManager.getInstance();

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
            setSearchTerm(searchTerm);
        }

    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        Log.d(TAG, "Search term has changed: " + searchTerm);
        if (mMap != null) {
            mMap.clear();
            showSearchResult();
        }
    }


    public void zoom(String title, double lat, double lon) {
        if (mMap != null) {
            selectedMarker.remove();
            LatLng marker = new LatLng(lat, lon);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 18));
            selectedMarker = mMap.addMarker(new MarkerOptions().title(title).position(marker));
        }
    }

    public void setCallbackProductPostSelected(Callback_ProductPostSelected callbackProductPostSelected) {
        this.callbackProductPostSelected = callbackProductPostSelected;
    }

    public void setCallbackCommunityPostSelected(Callback_CommunityPostSelected callbackCommunityPostSelected) {
        this.callbackCommunityPostSelected = callbackCommunityPostSelected;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}