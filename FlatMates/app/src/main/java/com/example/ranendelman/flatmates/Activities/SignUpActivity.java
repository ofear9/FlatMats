package com.example.ranendelman.flatmates.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ranendelman.flatmates.Holders.UsersListHolder;
import com.example.ranendelman.flatmates.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by RanEndelman on 03/03/2017.
 */

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private UsersListHolder listHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        setupSignUpButton();

    }

    protected void signUp(final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, getString(R.string.sign_up_succeeded),
                                    Toast.LENGTH_SHORT).show();
                            insertNewUserToDB(email);
                            Log.v("", "SIGN UP SUCCEEDED");
                        }

                        else {
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    protected void setupSignUpButton() {

        Button b = (Button) findViewById(R.id.doneSignUpButton);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText userNameEditText = (EditText) findViewById(R.id.newUserNameEditText);

                String userName = userNameEditText.getText().toString();

                EditText userNamePassword = (EditText) findViewById(R.id.signUpPasswordEditText);

                EditText userNameVerPassword = (EditText) findViewById(R.id.signUpVerPasswordEditText);

                String userPassword = userNamePassword.getText().toString();

                String userVerPassword = userNameVerPassword.getText().toString();

                if (userPassword.equals(userVerPassword)) {
                    Log.d(" user name: " + userName, "  Password: " + userPassword);
                    if (userName != null && userPassword != null && userVerPassword != null
                            && !userName.equals("") && !userPassword.equals("") && !userVerPassword.equals(""))
                        signUp(userName, userPassword);
                    else
                        Toast.makeText(SignUpActivity.this, getString(R.string.invalid_email_or_password),
                                Toast.LENGTH_SHORT).show();
                }

                else
                    Toast.makeText(SignUpActivity.this, getString(R.string.unmatched_password),
                            Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void insertNewUserToDB(final String email) {
        final String ID = mAuth.getCurrentUser().getUid();

        listHolder = new UsersListHolder(null);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                HashMap hashMap = new HashMap();
                hashMap.put(ID, email);
                if (snapshot.hasChild("users"))
                    listHolder.list = (ArrayList<HashMap>) snapshot.child("users").getValue();
                else
                    listHolder.list = new ArrayList<>();
                listHolder.list.add(hashMap);
                mDatabase.child("users").setValue(listHolder.list);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
}
