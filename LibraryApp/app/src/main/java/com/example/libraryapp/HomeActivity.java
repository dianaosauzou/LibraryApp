package com.example.libraryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        Button button = findViewById(R.id.button4);
        Button button2 = findViewById(R.id.button5);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, BooksActivity.class);
                startActivity(i);

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //button does Intent
                Intent i = new Intent(HomeActivity.this, LibraryActivity.class);
                startActivity(i);

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
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
            case R.id.article:
                Intent b = new Intent(this, BestSellersActivity.class);;
                startActivity(b);
                return true;
            case R.id.map:
                Intent a = new Intent(this, MapsActivity.class);
                startActivity(a);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

