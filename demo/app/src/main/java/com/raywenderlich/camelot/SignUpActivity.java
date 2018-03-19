package com.raywenderlich.camelot;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;



public class SignUpActivity extends CloseKeyboardActivity {

    private EditText mUsernameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    private Button mSignUpButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // Get Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance();
        initUI();
    }

    private void initUI() {
        mUsernameEditText = (EditText) findViewById(R.id.usernameEditText);
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        mSignUpButton = (Button) findViewById(R.id.signUpButton);

        mSignUpButton.setOnClickListener(signUpClickListenen);
    }

    View.OnClickListener signUpClickListenen = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String username = mUsernameEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();

            // Make sure the text is not empty String
            if (username.length() == 0 || password.length() == 0) {
                // Show Toast information
                showToast(R.string.empty_username_password_prompt);
                return;
            }

            mAuth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
//                                Toast.makeText(SignUpActivity.this, task.getResult().getUser().getProviderId(), Toast.LENGTH_SHORT).show();
                                showToast(R.string.sign_up_success);
                                finish();
                            } else {
                                showToast(R.string.sign_up_fail);
                            }
                        }
                    });
        }
    };

}
