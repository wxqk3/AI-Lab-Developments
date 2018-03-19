package com.raywenderlich.camelot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;


/**
 * Created by kanbudong on 2/24/18.
 */

public class LoginActivity extends CloseKeyboardActivity {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private Button mSignInButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // This line is necessary, or it will crash
        FirebaseApp.initializeApp(this);
        // Get Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance();
        // Initialize UI
        initUI();
        // Initialize auth state listener
        initAuthStateListener();

//        mAuth.signOut();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        // Judge if user has login, if yes redirect to main activity
        if (mAuth.getCurrentUser() != null) {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        // Start Main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        // Close Login activity
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     *
     */
    private void initUI() {
        mUsernameEditText = (EditText) findViewById(R.id.usernameEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(loginButtonClickListener);
        mSignInButton = (Button) findViewById(R.id.signUpButton);

        mSignInButton.setOnClickListener(signInButtonClickListener);
    }

    private void initAuthStateListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    /**
     * OnClickListener to handle login button click event.
     */
    private View.OnClickListener loginButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String username = mUsernameEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();
            // Make sure the text is not empty String
            if ( username.length()==0 || password.length()==0)
            {
                // Show Toast information
                showToast(R.string.empty_username_password_prompt);
                return;
            }

            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    };

    private View.OnClickListener signInButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
    };

}

