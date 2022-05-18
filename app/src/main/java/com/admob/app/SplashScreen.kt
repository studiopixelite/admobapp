package com.admob.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity


class SplashScreen : AppCompatActivity() {

    val MYPREFRENCES = "Preferences"
    val KEY_ISFIRSTTIME = "isFirstTime"
    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(MYPREFRENCES, Context.MODE_PRIVATE)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash_screen)

        loadNextActivity()

    }

    private fun loadNextActivity() {
        if (sharedPreferences!!.getBoolean(KEY_ISFIRSTTIME, true)) {
            Handler().postDelayed(
                {
                    val intent = Intent(this, IntroActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 3000
            )
        } else {
            showActivity()
        }
    }

    private fun showActivity(){
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                val capabilities = connectivityManager!!.getNetworkCapabilities(connectivityManager.activeNetwork)
                if(capabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN))
                {
                    loadMainActivity()
                }

            }

            else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                if (connectivityManager!!.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
                    loadMainActivity()
                }
                else if (connectivityManager.activeNetworkInfo == null && !connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
                    loadNoInternetActivity()
                }
            }
        }
        catch (e: NullPointerException)
        {
            e.printStackTrace()
            loadNoInternetActivity()
        }


    }

    private fun loadMainActivity(){
        Handler().postDelayed(
            {
                val intent = Intent (this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            , 3000)
    }

    private fun loadNoInternetActivity(){
        Handler().postDelayed(
            {
                val intent = Intent (this, NoInternetActivity::class.java)
                startActivity(intent)
                finish()
            }
            , 3000)
    }
}