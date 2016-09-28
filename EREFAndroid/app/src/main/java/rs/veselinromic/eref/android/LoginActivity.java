package rs.veselinromic.eref.android;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.io.IOException;

import rs.veselinromic.eref.wrapper.SessionManager;

public class LoginActivity extends AppCompatActivity
{
    private class LoginTask extends AsyncTask<Void, Void, Void>
    {
        String username;
        String password;

        boolean loginSuccess = false;

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                SessionManager.authenticate(username, password);
                this.loginSuccess = SessionManager.isAuthenticated();
            }
            catch (IOException e)
            {
                Log.e("Login", "exception", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if (loginSuccess)
            {
//                progressDialog.dismiss();

                Log.i("Login", "Login success!");
                if (!storedLoginDetailsExist) persistLoginInfo(username, password);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else
            {
                Log.e("Login", "Login error!");

                if (!storedLoginDetailsExist)
                {
//                    progressDialog.dismiss();

                    AnimatorSet loadingAnimation = new AnimatorSet();
                    ValueAnimator formFade = ObjectAnimator.ofFloat(formLayout, "alpha", 1f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                    ValueAnimator loadingScreenFade = ObjectAnimator.ofFloat(loadingScreenLayout, "alpha", 0f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                    loadingAnimation.play(formFade).after(loadingScreenFade);
                    loadingAnimation.start();

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Prijava neuspešna. Proverite korisničko ime i lozinku.");
                    builder.setTitle("Greška");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // dialog.dismiss();
                        }
                    });

                    builder.create().show();
                }
                else
                {
                    Log.i("Login", "Retrying login...");
                    new LoginTask(username, password).execute();
                }
            }
        }

        public LoginTask(String username, String password)
        {
            this.username = username;
            this.password = password;
        }
    }

    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;

    RelativeLayout formLayout;
    RelativeLayout loadingScreenLayout;

//    ProgressDialog progressDialog;
    boolean storedLoginDetailsExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);

        this.formLayout = (RelativeLayout) findViewById(R.id.form);
        this.loadingScreenLayout = (RelativeLayout) findViewById(R.id.loadingScreen);

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);

        SharedPreferences existingLoginPreferences = getSharedPreferences("loginDetails", 0);
        String username = existingLoginPreferences.getString("username", null);
        String password = existingLoginPreferences.getString("password", null);

        if (username != null && password != null)
        {
            storedLoginDetailsExist = true;

            usernameEditText.setVisibility(View.INVISIBLE);
            passwordEditText.setVisibility(View.INVISIBLE);
            loginButton.setVisibility(View.INVISIBLE);
            findViewById(R.id.textView).setVisibility(View.INVISIBLE);

            login(username, password);
        }

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(usernameEditText.getWindowToken(), 0);

                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();

                login(username, password);
            }
        });

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(usernameEditText.getWindowToken(), 0);

                    String username = usernameEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString();

                    login(username, password);

                    return true;
                }

                return false;
            }
        });
    }

    void login(String username, String password)
    {
        new LoginTask(username, password).execute();

//        this.progressDialog = new ProgressDialog(this);
//        this.progressDialog.setTitle("Prijava");
//        this.progressDialog.setMessage("Prijava u toku...");
//        this.progressDialog.setCancelable(false);
//        this.progressDialog.show();

        AnimatorSet loadingAnimation = new AnimatorSet();
        ValueAnimator formFade = ObjectAnimator.ofFloat(formLayout, "alpha", 0f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
        ValueAnimator loadingScreenFade = ObjectAnimator.ofFloat(loadingScreenLayout, "alpha", 1f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
        loadingAnimation.play(formFade).before(loadingScreenFade);
        loadingAnimation.start();
    }

    void persistLoginInfo(String username, String password)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetails", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }
}
