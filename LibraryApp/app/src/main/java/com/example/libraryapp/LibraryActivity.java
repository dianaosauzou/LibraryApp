package com.example.libraryapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libraryapp.repo.BookRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import model.Book;
import model.LibraryAdapter;
import model.User;

public class LibraryActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    List<Book> userBooks = new ArrayList<>();
    List<Book> userBooksCopy;


    public static LibraryAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_activity);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        createLibrary();


    }

    public void createLibrary() {
        userBooks.clear();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(LibraryActivity.this, RecyclerView.VERTICAL, false);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        DatabaseReference fireDBUser =
                FirebaseDatabase.getInstance("https://library-app-336618-default-rtdb.europe-west1.firebasedatabase.app/").getReference("LibraryApp").child("users");
        fireDBUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (mUser.getUid().equalsIgnoreCase(user.getUserId()))
                        if (user.getMyLibrary() != null) {
                            userBooks.addAll(user.getMyLibrary());
                        }

                }
                userBooksCopy = new ArrayList<>(userBooks);
                mAdapter = new LibraryAdapter(userBooks, userBooksCopy);
                mRecyclerView.setAdapter(mAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.library, menu);
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
                Intent a = new Intent(this, HomeActivity.class);
                startActivity(a);
                return true;
            default:

                return super.onOptionsItemSelected(item);
        }
    }


}
