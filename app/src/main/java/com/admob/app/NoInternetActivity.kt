package com.admob.app

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_no_internet.*

@Suppress("DEPRECATION")
class NoInternetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)

        retryButton.setOnClickListener {

            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

            try{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    val capabilities = connectivityManager!!.getNetworkCapabilities(connectivityManager.activeNetwork)
                    if(capabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    {
                        val intent = Intent (this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else {
                        Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                    }

                }

                else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                    if (connectivityManager!!.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
                        val intent = Intent (this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if (connectivityManager.activeNetworkInfo == null && !connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
                        Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                    }

                }
            }
            catch(e:NullPointerException){
                e.printStackTrace()
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }
}