package com.example.weatherforecastapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    private ImageView back_icon;
    private TextView cupl;
    private ListView listView;
    DetailsAdapter detailsAdapter;
    ArrayList<Details> details;
    private String ctyName = " ";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main_2);

        back_icon = findViewById(R.id.backbtn);
        cupl = findViewById(R.id.crw);
        listView = findViewById(R.id.list_item);

        details = new ArrayList<Details>();
        detailsAdapter = new DetailsAdapter(MainActivity2.this, details);
        listView.setAdapter(detailsAdapter);

        Intent intent =  getIntent();
        String cityName = intent.getStringExtra("name");
        Log.d("TAG", "MSG" + cityName);
        ctyName = cityName;
        cupl.setText(ctyName);
        getMoreInfo(ctyName);

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void getMoreInfo(String cityName) {
        String url= "https://api.weatherapi.com/v1/forecast.json?key=8f33d4f56a734894b19144239232711&q=" + cityName + "&days=1&aqi=yes&alerts=yes";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity2.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    details.clear();

                    String conditiontext = response.getJSONObject("current").getJSONObject("condition").getString("text");;
                    details.add(new Details("Condition", "128/3905/3905275", conditiontext));

                    String windsp = response.getJSONObject("current").getString("wind_kph") + "km/h";
                    details.add(new Details("Wind Speed", "128/2011/2011448", windsp));

                    String press = response.getJSONObject("current").getString("pressure_mb") + "mb";
                    details.add(new Details("Pressure", "128/2299/2299296", press));

                    String humd = response.getJSONObject("current").getString("humidity") + "%";
                    details.add(new Details("Humidity", "128/4148/4148460", humd));

                    String vis = response.getJSONObject("current").getString("vis_km") + "km";
                    details.add(new Details("Vision", "128/1078/1078327", vis));

                    String uv = response.getJSONObject("current").getString("uv");
                    Double uvn = Double.valueOf(uv);
                    if (uvn <= 2) {
                        uv = uv + ": Low";
                        details.add(new Details("UV", "128/7429/7429606", uv));
                    } else if (uvn <= 5 && uvn > 2) {
                        uv = uv + ": Moderate";
                        details.add(new Details("UV", "128/7429/7429606", uv));
                    } else if (uvn < 8 && uvn >= 6) {
                        uv = uv + ": High";
                        details.add(new Details("UV", "128/7429/7429606", uv));
                    } else if (uvn <= 10 && uvn >=8) {
                        uv = uv + ": Very High";
                        details.add(new Details("UV", "128/7429/7429606", uv));
                    } else {
                        uv = uv + ": Extreme";
                        details.add(new Details("UV", "128/7429/7429606", uv));
                    }

                    String airq = response.getJSONObject("current").getJSONObject("air_quality").getString("pm2_5");
                    Double airqn = Double.valueOf(airq);
                    if (airqn < 12) {
                        airq = airq + ": Good";
                        details.add(new Details("Air Quality", "128/10398/10398914", airq));
                    } else if (airqn < 35 && airqn >= 12) {
                        airq = airq + ": Moderate";
                        details.add(new Details("Air Quality", "128/10398/10398914", airq));
                    } else if (airqn < 55 && airqn >= 35) {
                        airq = airq + ": Unhealthy for Sensitive Individuals";
                        details.add(new Details("Air Quality", "128/10398/10398914", airq));
                    } else if (airqn < 150 && airqn >= 55) {
                        airq = airq + ": Unhealthy";
                        details.add(new Details("Air Quality", "128/10398/10398914", airq));
                    } else if (airqn < 250 && airqn >= 150) {
                        airq = airq + ": Very Unhealthy";
                        details.add(new Details("Air Quality", "128/10398/10398914", airq));
                    } else {
                        airq = airq + ": Hazardous";
                        details.add(new Details("Air Quality", "128/10398/10398914", airq));
                    }
                    detailsAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                Toast.makeText(MainActivity2.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

}
