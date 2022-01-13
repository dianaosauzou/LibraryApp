package com.example.libraryapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import model.Book;
import model.Review;

public class BestSellersActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private Cache cache;
    private Network network;
    private String url = "https://api.nytimes.com/svc/books/v3/reviews.json?title=";
    private String apiKey = "&api-key=0Y5vBSCKmMPm1X8oMjxRwaOYAlHFudco";
    private String query = "";
    ObjectMapper objectMapper = new ObjectMapper();
    WebView webView;
    TextView textview;
    TextView textview1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review);
        cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();

        textview = findViewById(R.id.textView4);
        textview1 = findViewById(R.id.textView5);


        webView =(WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());

        webView.isPrivateBrowsingEnabled();

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://www.nytimes.com/books/best-sellers/");


    }

    private void extracted(String query) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url+query+apiKey, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray itemsArray = response.getJSONArray("results");
                    if(itemsArray.length()<=0){
                        Snackbar mySnackbar = Snackbar.make(webView.getRootView(),"Sorry that article hasn't been written yet!",Snackbar.LENGTH_LONG);
                        mySnackbar.show();
                    }
                    JSONObject bookObj1 = new JSONObject();
                    for (int x = 0; x < itemsArray.length(); x++) {
                        bookObj1 = itemsArray.getJSONObject(x);
                        String bookObj = bookObj1.toString();
                        Review review =  objectMapper.readValue(bookObj, Review.class);
                        webView.loadUrl(review.getUrl());
                        textview.setText(new StringBuilder().append(getString(R.string.byline)).append(review.getByline()).toString());
                        textview1.setText(new StringBuilder().append(getString(R.string.details)).append(review.getBook_author()).append(", ").append(review.getBook_title()).toString());

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("unavaila");
            }
        });
        requestQueue.add(jsonObjectRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.app_bar_search12:
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        extracted(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }

                });
                return true;
            case R.id.logout:
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                return true;
            case R.id.back:
                Intent a = new Intent(this, HomeActivity.class);
                startActivity(a);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
