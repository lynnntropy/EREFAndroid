package rs.veselinromic.eref.android;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
            progressDialog.dismiss();

            if (loginSuccess)
            {
                Log.i("Login", "Login success!");
                persistLoginInfo(username, password);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else
            {
                Log.e("Login", "Login error!");

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("Prijava neuspešna. Proverite korisničko ime i lozinku.");
                builder.setTitle("Greška");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                builder.create().show();
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

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);

        SharedPreferences existingLoginPreferences = getSharedPreferences("loginDetails", 0);
        String username = existingLoginPreferences.getString("username", null);
        String password = existingLoginPreferences.getString("password", null);

        if (username != null && password != null)
        {
            login(username, password);
        }

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();
                login(username, password);
            }
        });


    }

    void login(String username, String password)
    {
        new LoginTask(username, password).execute();

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setTitle("Prijava");
        this.progressDialog.setMessage("Prijava u toku...");
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    void persistLoginInfo(String username, String password)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetails", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }
}
