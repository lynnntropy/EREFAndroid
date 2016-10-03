package rs.veselinromic.eref.android

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import java.io.IOException

import rs.veselinromic.eref.wrapper.SessionManager

import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity()
{
    private inner class LoginTask(internal var username: String, internal var password: String) : AsyncTask<Void, Void, Void>()
    {
        internal var loginSuccess = false

        override fun doInBackground(vararg params: Void?): Void?
        {
            try
            {
                SessionManager.authenticate(username, password)
                this.loginSuccess = SessionManager.isAuthenticated()
            }
            catch (e: IOException)
            {
                Log.e("Login", "exception", e)
            }

            return null
        }

        override fun onPostExecute(aVoid: Void?)
        {
            if (loginSuccess)
            {
                Log.i("Login", "Login success!")
                if (!storedLoginDetailsExist) persistLoginInfo(username, password)

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            else
            {
                Log.e("Login", "Login error!")

                if (!storedLoginDetailsExist)
                {
                    try
                    {
                        val loadingAnimation = AnimatorSet()
                        val formFade = ObjectAnimator.ofFloat(form, "alpha", 1f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                        val loadingScreenFade = ObjectAnimator.ofFloat(loadingScreen, "alpha", 0f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                        loadingAnimation.play(formFade).after(loadingScreenFade)
                        loadingAnimation.start()

                        val builder = AlertDialog.Builder(this@LoginActivity)
                        builder.setMessage("Prijava neuspešna. Proverite korisničko ime i lozinku.")
                        builder.setTitle("Greška")
                        builder.setPositiveButton("OK") { dialog, which -> }
                        builder.create().show()
                    }
                    catch(e: WindowManager. BadTokenException)
                    {
                        Log.e("Login", "e", e)
                    }

                }
                else
                {
                    Log.i("Login", "Retrying login...")
                    LoginTask(username, password).execute()
                }
            }
        }
    }

    internal var storedLoginDetailsExist = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_login)

        val existingLoginPreferences = getSharedPreferences("loginDetails", 0)
        val storedUsername = existingLoginPreferences.getString("username", null)
        val storedPassword = existingLoginPreferences.getString("password", null)

        if (storedUsername != null && storedPassword != null)
        {
            storedLoginDetailsExist = true

            usernameEditText.visibility = View.INVISIBLE
            passwordEditText.visibility = View.INVISIBLE
            loginButton.visibility = View.INVISIBLE
            findViewById(R.id.textView)!!.visibility = View.INVISIBLE

            login(storedUsername, storedPassword)
        }

        loginButton.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(usernameEditText.windowToken, 0)

            val username = usernameEditText.text.toString().trim { it <= ' ' }
            val password = passwordEditText.text.toString()

            login(username, password)
        }

        passwordEditText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE)
            {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(usernameEditText.windowToken, 0)

                val username = usernameEditText.text.toString().trim { it <= ' ' }
                val password = passwordEditText.text.toString()

                login(username, password)

                return@OnEditorActionListener true
            }

            false
        })
    }

    internal fun login(username: String, password: String)
    {
        LoginTask(username, password).execute()

        val loadingAnimation = AnimatorSet()
        val formFade = ObjectAnimator.ofFloat(form, "alpha", 0f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
        val loadingScreenFade = ObjectAnimator.ofFloat(loadingScreen, "alpha", 1f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
        loadingAnimation.play(formFade).before(loadingScreenFade)
        loadingAnimation.start()
    }

    internal fun persistLoginInfo(username: String, password: String)
    {
        val sharedPreferences = getSharedPreferences("loginDetails", 0)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.apply()
    }
}
