package com.example.libraryapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import model.Book;
import model.User;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private static final String TAG = "EmailPassword";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);
        mAuth = FirebaseAuth.getInstance();
    }

    private void createAccount(String email, String password, String phoneNumber, String username) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            mUser = mAuth.getCurrentUser();
                            writeNewUser(email,password,phoneNumber,username);
                            Intent i = new Intent(MainActivity.this, LoginActivity.class);

                            startActivity(i);


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                });
        // [END create_user_with_email]
    }

    public void writeNewUser(String email, String password, String phoneNumber, String username) {
        String userId=  mUser.getUid();
        List <Book> myBooks = new ArrayList<>();
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://library-app-336618-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference rf = db.getReference("LibraryApp");
        User user = new User(email, username, password, phoneNumber, userId, myBooks );
        rf.child("users").child(userId).setValue(user).addOnSuccessListener(aVoid -> {
            Toast.makeText(MainActivity.this, "Write is successful", Toast.LENGTH_LONG).show();

        })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Write is failed", Toast.LENGTH_LONG).show();

                });
    }


    public void userDetails(View view) {
        EditText email = findViewById(R.id.email);
        EditText userName = findViewById(R.id.name);
        EditText password = findViewById(R.id.password);
        EditText phoneNo = findViewById(R.id.phoneNumber);

        String userNameString = userName.getText().toString();
        String passwordString = password.getText().toString();
        String emailString = email.getText().toString().trim();
        System.out.println(emailString);
        String phoneNoString = phoneNo.getText().toString();

        if (passwordString.length() >= 5){
            createAccount(emailString,passwordString, phoneNoString, userNameString);
        }
        else
            Toast.makeText(this, "Invalid password!", Toast.LENGTH_SHORT).show();



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.login:
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}