package com.example.weatherforecastapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTv,temperatureTv;
    private Button moreinfoTv;
    private RecyclerView weatherRV, weatherFC;
    private TextInputEditText cityEdit;
    private ImageView searchIv;
    private com.airbnb.lottie.LottieAnimationView iconIV;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList, weatherFCArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private WeatherFCAdapter weatherFCAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE =1;
    private String cityName;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        loadingPB = findViewById(R.id.idPBLoading);
        cityNameTv = findViewById(R.id.idTVCityName);
        temperatureTv = findViewById(R.id.idTVTemperature);
        moreinfoTv = (Button) findViewById(R.id.idTVMoreInfo);
        weatherRV = findViewById(R.id.idRvWeather);
        weatherFC = findViewById(R.id.idFcWeather);
        cityEdit = findViewById(R.id.idEdtCity);
        iconIV = findViewById(R.id.idIVIcon);
        homeRL = findViewById(R.id.idRLHome);
        searchIv = findViewById(R.id.idIVSearch);

        weatherRVModelArrayList = new ArrayList<>();
        weatherFCArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this,weatherRVModelArrayList);
        weatherFCAdapter = new WeatherFCAdapter(this,weatherFCArrayList);
        weatherRV.setAdapter(weatherRVAdapter);
        weatherFC.setAdapter(weatherFCAdapter);

        locationManager =(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{ Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location!= null){
            cityName = getCityName(location.getLongitude(), location.getLatitude());
            getNextWeather(cityName);
            getWeatherInfo(cityName);
        }

        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city= Objects.requireNonNull(cityEdit.getText()).toString();
                if (city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter city name", Toast.LENGTH_SHORT).show();
                }else{
                    cityNameTv.setText(cityName);
                    getNextWeather(city);
                    getWeatherInfo(city);
                }
            }
        });

        moreinfoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city= Objects.requireNonNull(cityEdit.getText()).toString();
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("name", city);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude){
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);
            for (Address adr : addresses){
                if (adr!=null){
                    String city= adr.getLocality();
                    System.out.println(city);
                    if (city!=null && !city.equals("")){
                        return city;
                    }else {
                        Log.d("TAG","CITY NOT FOUND");
                        Toast.makeText(this, "User City Not Found..", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName){
        String url= "https://api.weatherapi.com/v1/forecast.json?key=8f33d4f56a734894b19144239232711&q=" + cityName + "&days=1&aqi=yes&alerts=yes";
        System.out.println(url);
        cityNameTv.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRVModelArrayList.clear();
                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureTv.setText(temperature + "°C");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("https:".concat(conditionIcon)).into(iconIV);

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecast0=forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecast0.getJSONArray("hour");

                    for (int i = 0; i<hourArray.length(); i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time= hourObj.getString("time");
                        String temper= hourObj.getString("temp_c");
                        String img= hourObj.getJSONObject("condition").getString("icon");
                        String wind= hourObj.getString("wind_kph");
                        weatherRVModelArrayList.add(new WeatherRVModel(time,temper,img,wind));
                    }
                    weatherRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void getNextWeather(String cityName){
        String url= "https://api.weatherapi.com/v1/forecast.json?key=8f33d4f56a734894b19144239232711&q=" + cityName + "&days=7&aqi=yes&alerts=yes";
        System.out.println(url);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                weatherFCArrayList.clear();

                try {
                    JSONArray forecastday = response.getJSONObject("forecast").getJSONArray("forecastday");
                    for (int i = 0; i < forecastday.length(); i++) {
                        JSONObject listObj = forecastday.getJSONObject(i);
                        String time = listObj.getString("date");
                        JSONObject dayObj = listObj.getJSONObject("day");
                        String temp = dayObj.getString("mintemp_c") +"°C/ "+ dayObj.getString("maxtemp_c") +"°C";
                        String wind = dayObj.getString("maxwind_kph");
                        String img = dayObj.getJSONObject("condition").getString("icon");
                        weatherFCArrayList.add(new WeatherRVModel(time, temp, img, wind));
                    }
                    weatherFCAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

}