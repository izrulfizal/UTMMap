package com.example.utmmap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Bundle;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.util.LongSparseArray;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private MapView mapView;
    Handler handler = new Handler();
    int delay = 1000; //milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }





        Mapbox.getInstance(this, "pk.eyJ1IjoiaXpydWxmaXphbCIsImEiOiJjanl4c3N4bXIwNXEyM25tcDI0Y3ZpbGk1In0.1jrAAqPJCyUihJthcX7ltQ");
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                final List<Feature> symbolLayerIconFeatureList = new ArrayList<>();

                final HttpURLConnection[] connection = {null};
                final BufferedReader[] reader = {null};

//                handler.postDelayed(new Runnable(){
//                    public void run(){
//                        Log.d("timer","timerRun");
//
//                        handler.postDelayed(this, delay);
//                    }
//                }, delay);
                handler.postDelayed(new Runnable(){
                    public void run(){
                        Log.d("timer","timerRun");
                try {
                    URL url = new URL("http://103.75.188.69/api/test.php"   );
                    connection[0] = (HttpURLConnection) url.openConnection();
                    connection[0].connect();


                    InputStream stream = connection[0].getInputStream();

                    reader[0] = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();
                    String line = "";

                    while ((line = reader[0].readLine()) != null) {
                        buffer.append(line+"\n");
                        Log.d("Response: ", "> " + line);

                        try {

                            JSONArray obj = new JSONArray(line);

                            Log.d("JsonObj", obj.toString());
                            symbolLayerIconFeatureList.clear();
                            for (int i=0;i<obj.length();i++)
                            {
                                JSONObject jsonObject=obj.getJSONObject(i);

                                String bus=jsonObject.getString("bus");
                                Double latitude=jsonObject.getDouble("latitude");
                                Double longitude=jsonObject.getDouble("longitude");

//                                Log.d("busData", bus+","+longitude+","+latitude);

                                symbolLayerIconFeatureList.add(Feature.fromGeometry(
                                        Point.fromLngLat(longitude,latitude)));



                            }

                            mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")


                                    // Add the SymbolLayer icon image to the map style
                                    .withImage(ICON_ID, BitmapFactory.decodeResource(
                                            MainActivity.this.getResources(), R.drawable.red_marker))
                                    // Adding a GeoJson source for the SymbolLayer icons.
                                    .withSource(new GeoJsonSource(SOURCE_ID,
                                            FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
                                    .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                                            .withProperties(PropertyFactory.iconImage(ICON_ID),
                                                    iconAllowOverlap(true),
                                                    iconOffset(new Float[] {0f, -9f}))
                                    ), new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {

                                    // Map is set up and the style has loaded. Now you can add data or make other map adjustments.


                                }
                            });



                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + line + "\"");
                        }

                    }






                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection[0] != null) {
                        connection[0].disconnect();
                    }
                    try {
                        if (reader[0] != null) {
                            reader[0].close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                handler.postDelayed(this, delay);
                    }
                }, delay);
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(1.5619,103.6556))
                        .zoom(10)
                        .build());


            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}

