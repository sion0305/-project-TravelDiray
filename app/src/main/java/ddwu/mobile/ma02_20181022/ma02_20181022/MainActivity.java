package ddwu.mobile.finalproject.ma02_20181022;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import ddwu.mobile.finalproject.R;

public class MainActivity extends AppCompatActivity {

    private GoogleMap map;
    final static int MY_PERMISSIONS_REQ_LOC = 100;
    private LocationManager locManager;

    ArrayList<Marker> markers = new ArrayList<Marker>();
    double latitude;//위도
    double longitude;//경도

    Location lastLocation;

    Spinner areaSpinner;
    Spinner typeSpinner;
    Spinner radiusSpinner;
    String area;
    String type;
    String radius;
    int cArea, cType;
    public static final String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        areaSpinner = (Spinner) findViewById(R.id.spArea);
        typeSpinner = (Spinner) findViewById(R.id.spType);
        radiusSpinner = (Spinner) findViewById(R.id.spRadius);

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallBack);

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_search:
                area = areaSpinner.getSelectedItem().toString();
                type = typeSpinner.getSelectedItem().toString();
                changeAreaToCode(area);
                changeTypeToCode(type);

                Intent recommendIntent = new Intent(MainActivity.this, RecommendList.class);
                recommendIntent.putExtra("area", cArea);
                recommendIntent.putExtra("type", cType);
                recommendIntent.putExtra("select", area);
                startActivity(recommendIntent);
                break;
            case R.id.btn_heart:
                Intent myTravelIntent = new Intent(MainActivity.this, MyTravel.class);
                startActivity(myTravelIntent);
                break;
        }
    }

    private void locationUdpate(){
        if(checkPermission())
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private void getLastLocation() {
        if (checkPermission()) {
            lastLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng currentLoc;
            map = googleMap;

            if(checkPermission()) {
                map.setMyLocationEnabled(true);
            }
            getLastLocation();

            if(lastLocation != null){
                latitude = lastLocation.getLatitude();//위도
                longitude = lastLocation.getLongitude();//경도

            }else {
                latitude = 37.606320;
                longitude = 127.041808;
            }

            currentLoc = new LatLng(latitude, longitude);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));

            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    Log.d(TAG, latitude + "," + longitude);
                    return false;
                }
            });

            map.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {
                    Toast.makeText(MainActivity.this,
                            String.format("현재 위치: (%f, %f)", location.getLatitude(),location.getLongitude()), Toast.LENGTH_SHORT).show();
                    type = typeSpinner.getSelectedItem().toString();
                    changeTypeToCode(type);
                    radius = radiusSpinner.getSelectedItem().toString();
                    Intent detailIntent = new Intent(MainActivity.this, ListByLocation.class);
                    detailIntent.putExtra("latitude", location.getLatitude());
                    detailIntent.putExtra("longitude", location.getLongitude());
                    detailIntent.putExtra("radius", radius);
                    detailIntent.putExtra("type", cType);

                    startActivity(detailIntent);
                }
            });

            map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    Marker marker;
                    MarkerOptions poiMarkerOptions = new MarkerOptions();
                    poiMarkerOptions.position(latLng);
                    poiMarkerOptions.title("위치 지정");
                    poiMarkerOptions.snippet("위도: " + String.format("%.6f", latLng.latitude) + ", 경도: " + String.format("%.6f", latLng.longitude));
                    poiMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;

                    marker = map.addMarker(poiMarkerOptions);
                    markers.add(marker);
                }
            });

            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Toast.makeText(MainActivity.this,
                            String.format("지정 위치: (%f, %f)", latitude,longitude), Toast.LENGTH_SHORT).show();
                    type = typeSpinner.getSelectedItem().toString();
                    changeTypeToCode(type);
                    radius = radiusSpinner.getSelectedItem().toString();
                    Intent detailIntent = new Intent(MainActivity.this, ListByLocation.class);
                    detailIntent.putExtra("latitude", latitude);
                    detailIntent.putExtra("longitude", longitude);
                    detailIntent.putExtra("radius", radius);
                    detailIntent.putExtra("type", cType);

                    startActivity(detailIntent);
                }
            });
        }
    };

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQ_LOC);
                return false;
            } else
                return true;
        }
        return false;
    }


    /*권한승인 요청에 대한 사용자의 응답 결과에 따른 수행*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case MY_PERMISSIONS_REQ_LOC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*권한을 승인받았을 때 수행하여야 하는 동작 지정*/
                    locationUdpate();
                } else {
                    /*사용자에게 권한 제약에 따른 안내*/
                    Toast.makeText(this, "Permissions are not granted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    public void changeAreaToCode(String a) {
        switch (a) {
            case "서울":
                cArea = 1;
                break;
            case "인천":
                cArea = 2;
                break;
            case "대전":
                cArea = 3;
                break;
            case "대구":
                cArea = 4;
                break;
            case "광주":
                cArea = 5;
                break;
            case "부산":
                cArea = 6;
                break;
            case "울산":
                cArea = 7;
                break;
            case "세종시":
                cArea = 8;
                break;
            case "경기도":
                cArea = 31;
                break;
            case "강원도":
                cArea = 32;
                break;
            case "충청북도":
                cArea = 33;
                break;
            case "충청남도":
                cArea = 34;
                break;
            case "경상북도":
                cArea = 35;
                break;
            case "경상남도":
                cArea = 36;
                break;
            case "전라북도":
                cArea = 37;
                break;
            case "전라남도":
                cArea = 38;
                break;
            case "제주도":
                cArea = 39;
                break;
        }
    }

    public void changeTypeToCode(String t) {
        switch (t) {
            case "관광지":
                cType = 12;
                break;
            case "문화시설":
                cType = 14;
                break;
            case "축제/공연/행사":
                cType = 15;
                break;
            case "여행코스":
                cType = 25;
                break;
            case "레포츠":
                cType = 28;
                break;
            case "숙박":
                cType = 32;
                break;
            case "쇼핑":
                cType = 38;
                break;
            case "음식":
                cType = 39;
                break;
        }
    }

}