package com.example.libraryapp.repo;

import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Book;
import model.User;

public class BookRepo {

    private List <Book> userBooks;

    public boolean manageLibrary(Book book, String userIds, View v) {

        DatabaseReference fireDBUser =
                FirebaseDatabase.getInstance("https://library-app-336618-default-rtdb.europe-west1.firebasedatabase.app/").getReference("LibraryApp").child("users");
        fireDBUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    System.out.println(user.toString());

                    if (userIds.equalsIgnoreCase(user.getUserId())) {
                        userBooks = user.getMyLibrary();
                        System.out.println(user.getMyLibrary());
                        if (userBooks == null) {
                            userBooks = new ArrayList<>();
                        }

                        Boolean name = false;
                        for (Book bookD : userBooks) {
                            if (bookD.getTitle().equalsIgnoreCase(book.getTitle())) {
                                name = true;
                                Snackbar mySnack =  Snackbar.make(v, "That is already in your library!", Snackbar.LENGTH_LONG);
                                mySnack.show();
                            }
                        }
                        if (name != true) {
                            userBooks.add(book);
                            if (!userBooks.isEmpty()) {
                                DatabaseReference fireDB = FirebaseDatabase.getInstance("https://library-app-336618-default-rtdb.europe-west1.firebasedatabase.app/").getReference("LibraryApp").child("users").child(userIds);
                                Map<String, Object> ListUpdates = new HashMap<>();
                                ListUpdates.put("myLibrary", userBooks);
                                System.out.println(userBooks);
                                fireDB.updateChildren(ListUpdates);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
        return true;
    }

    public boolean remove(Book book, String uid) {

        DatabaseReference fireDBUser =
                FirebaseDatabase.getInstance("https://library-app-336618-default-rtdb.europe-west1.firebasedatabase.app/").getReference("LibraryApp").child("users");
        fireDBUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);

                    if (uid.equalsIgnoreCase(user.getUserId())) {
                        if(user.getMyLibrary()!=null){
                            userBooks = user.getMyLibrary();
                        if(!userBooks.isEmpty()) {

                            userBooks.removeIf(i -> i.getTitle().equalsIgnoreCase(book.getTitle()));
                            System.out.println(userBooks.size());
                            DatabaseReference fireDB = FirebaseDatabase.getInstance("https://library-app-336618-default-rtdb.europe-west1.firebasedatabase.app/").getReference("LibraryApp").child("users").child(uid);
                            Map<String, Object> ListUpdates = new HashMap<>();
                            ListUpdates.put("myLibrary", userBooks);
                            fireDB.updateChildren(ListUpdates);
                        }
                    }
                }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

        return true;
    }

    public void updateBookStatus(Book book1, Boolean status, String uid) {
                DatabaseReference fireDB = FirebaseDatabase.getInstance("https://library-app-336618-default-rtdb.europe-west1.firebasedatabase.app/").getReference("LibraryApp").child("users").child(uid).child("myLibrary");
        fireDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Book book = userSnapshot.getValue(Book.class);
                    if(book.getTitle().equalsIgnoreCase(book1.getTitle())) {
                        DatabaseReference ref = fireDB.child(userSnapshot.getKey());
                        Map<String, Object> ListUpdates = new HashMap<>();
                        ListUpdates.put("status", status);
                        ref.updateChildren(ListUpdates);

                    }

                }
//

        }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }
}
