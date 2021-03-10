package com.example.elbaeat.ui.login;

import android.app.ProgressDialog;
//import android.arch.lifecycle.LiveData;
//import android.arch.lifecycle.MutableLiveData;
//import android.arch.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.os.AsyncTask;
import android.util.Patterns;

import com.example.elbaeat.data.LoginRepository;
import com.example.elbaeat.data.Result;
import com.example.elbaeat.data.model.LoggedInUser;
import com.example.elbaeat.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    Result<LoggedInUser> finalResult;


    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        new LoginValidatorTask().execute(username, password);
        //Result<LoggedInUser> result = loginRepository.login(username, password);


    }

    private class LoginValidatorTask extends AsyncTask<String, Void, Result> {

        //private static final int RESULT_OK = 0;
        //private static final int RESULT_FAILED = 1;

        //private ProgressDialog loadingDialog;

        @Override
        protected Result doInBackground(String... params) {

            // Do your main login logic here.
            // Using RETURN_OK as a return value if the login succeeded
            // ...or RETURN_FAILED otherwise.
            Result<LoggedInUser> result = loginRepository.login(params[0], params[1]);
            return result;

        }

        @Override
        protected void onPostExecute(Result result) {
            finalResult = result;
            if (finalResult instanceof Result.Success) {
                LoggedInUser data = ((Result.Success<LoggedInUser>) finalResult).getData();
                loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName(), data.getSessionKey())));
            } else{
                loginResult.setValue(new LoginResult(finalResult.toString()));
            }

        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}