package com.example.elbaeat.ui.login;

import android.app.Activity;
//import android.arch.lifecycle.Observer;
import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
//import android.arch.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.Nullable;
import androidx.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elbaeat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    public static final String EXTRA_USER = "com.example.elbaeat.USER";
    public static final String EXTRA_CODE = "com.example.elbaeat.CODE";
    public static final String EXTRA_KEY = "com.example.elbaeat.KEY";
    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;
    ProgressBar loadingProgressBar;
    public String FCMToken;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FragmentActivity", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        FCMToken = task.getResult();
                        // Log and toast
                        // FCMToken = getString(R.string.msg_token_fmt, token);

                        Log.d("FragmentActivity", FCMToken);
                        Toast.makeText(getApplicationContext(), FCMToken, Toast.LENGTH_SHORT).show();
                    }
                });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);

                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());

                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }



                //Complete and destroy login activity once successful
                //finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
    }



    private void updateUiWithUser(LoggedInUserView model) {
        String[] AccountInfo = new String[]{model.getDisplayName(),model.getSessionKey()};
        String welcome = getString(R.string.welcome) + AccountInfo[0] + " key: " + AccountInfo[1];
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        if (AccountInfo.length >=1){
            setResult(Activity.RESULT_OK);
            Intent intent = new Intent(LoginActivity.this, com.example.elbaeat.ui.main.MainActivity.class);
            intent.putExtra(EXTRA_USER, model);
            if(FCMToken != null ) {
                intent.putExtra(EXTRA_CODE, FCMToken);
            }
            startActivity(intent);
        }

    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
    }
}