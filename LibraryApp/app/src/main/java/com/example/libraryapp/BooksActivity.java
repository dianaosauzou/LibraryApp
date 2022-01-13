package com.example.libraryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.Book;
import model.BookAdapter;

public class BooksActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    //this is my api key
//    private String apiKey = getString(R.string.google_maps_key);
    //arraylist to store books that will be adapted to my recycler view
    private ArrayList<Book> bookList;
    private List<Book> userBooks;


    //Volley variables
    private RequestQueue requestQueue;
    private Cache cache;
    private Network network;
    static BookAdapter mAdapter;
    private String url = "https://www.googleapis.com/books/v1/volumes?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    public void getBooksByQuery(String query) {
        bookList = new ArrayList<>();
        cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        //Get books works
        requestQueue.start();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url + query, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray itemsArray = response.getJSONArray("items");
                            JSONObject bookObj = new JSONObject();
                            Iterator<?> keys;
                            for (int x = 0; x < itemsArray.length(); x++) {
                                bookObj = itemsArray.getJSONObject(x);
                                keys = bookObj.keys();
                                for (Iterator<?> it = keys; it.hasNext(); ) {
                                    String l = (String) it.next();
                                    //Ive used the jackson object mapper to map the json object to my pojo class
                                    if (l.equalsIgnoreCase("volumeinfo")) {
                                        JSONObject volumeObj = bookObj.getJSONObject(l);
                                        String newBook = volumeObj.toString();
                                        JSONObject imageLinks = volumeObj.optJSONObject("imageLinks");
                                        String thumbnail = "";
                                        if(imageLinks!=null) {
                                            thumbnail = imageLinks.optString("thumbnail");
                                        }
                                        Book book = objectMapper.readValue(newBook, Book.class);
                                        book.setThumbnail(thumbnail);
                                        book.setStatus(false);
                                        bookList.add(book);
                                    }

                                }

                            }

                            mAdapter = new BookAdapter(bookList);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BooksActivity.this, RecyclerView.VERTICAL, false);
                            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler);

                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);


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
                        // TODO: Handle error
                        Toast.makeText(BooksActivity.this, "Error found is " + error, Toast.LENGTH_SHORT).show();


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
                        getBooksByQuery(query);

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