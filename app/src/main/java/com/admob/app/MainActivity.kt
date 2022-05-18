package com.admob.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var backPressedTime = 0L
    private var mInterstitialAd: InterstitialAd? = null
    private final var TAG = "MainActivity"

    val MYPREFRENCES = "Preferences"
    val KEY_HASNOTREVIEWED = "hasReviewed"
    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(MYPREFRENCES, Context.MODE_PRIVATE);

        onCreateWebView()
        refreshApp()
        showAds()


        bottom_nav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bottom_nav_home -> webView.loadUrl("https://support.microsoft.com/en-us/windows/keyboard-shortcuts-in-windows-dcc61a57-8ff0-cffe-9796-cb9706c75eec")
                R.id.bottom_nav_trending -> webView.loadUrl("https://support.apple.com/en-ng/HT201236")
                R.id.bottom_nav_diy -> webView.loadUrl("https://linuxhint.com/100_keyboard_shortcuts_linux/")
            }
            true
        }

        retryButton_error.setOnClickListener {
            errorLayout.visibility = View.GONE
            webView.loadUrl(webView.url.toString())
        }

    }

    private fun showAds(){

        MobileAds.initialize(this@MainActivity)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        adView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }

        InterstitialAd.load(this,"ca-app-pub-9774136169458958/1996693204", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError?.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })

        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.d(TAG, "Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.")
                mInterstitialAd = null;
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun onCreateWebView(){
        webView.loadUrl("https://support.microsoft.com/en-us/windows/keyboard-shortcuts-in-windows-dcc61a57-8ff0-cffe-9796-cb9706c75eec")
        //webView.loadUrl("flutterwave.com")
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        webView.settings.allowContentAccess = true
        webView.settings.enableSmoothTransition()

        webView.webViewClient = object: WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
               /* view!!.loadUrl(request!!.url.toString())*/
                if(!view!!.url!!.toString().contains("buildnode")){
                    val chromeUri = view!!.url.toString()
                    val chromeBuilder = CustomTabsIntent.Builder()

                    chromeBuilder.setToolbarColor(resources.getColor(R.color.colorPrimary))

                    chromeBuilder.setShareState(CustomTabsIntent.SHARE_STATE_OFF)

                    chromeBuilder.setShowTitle(true)

                    val chromeIntent = chromeBuilder.build()

                    chromeIntent.launchUrl(this@MainActivity, Uri.parse(chromeUri))
                    view.stopLoading()
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if(view!!.url!!.toString().contains("buildnode")){
                    view.loadUrl(url!!)
                } else{
                    /*val i = Intent(Intent.ACTION_VIEW, Uri.parse(view!!.url))
                    startActivity(i)*/
                    val chromeUri = view!!.url.toString()
                    val chromeBuilder = CustomTabsIntent.Builder()

                    chromeBuilder.setToolbarColor(resources.getColor(R.color.colorPrimaryDark))

                    chromeBuilder.setShareState(CustomTabsIntent.SHARE_STATE_OFF)

                    chromeBuilder.setShowTitle(true)

                    val chromeIntent = chromeBuilder.build()

                    chromeIntent.launchUrl(this@MainActivity, Uri.parse(chromeUri))
                    view.stopLoading()
                }
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressbar.visibility = View.VISIBLE
                adView.visibility = View.GONE
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressbar.visibility = View.GONE
                adView.visibility = View.VISIBLE
                super.onPageFinished(view, url)
            }
        }

        webView.webChromeClient = object: WebChromeClient(){
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                if (title!!.contains("404") || title.contains("500") || title.contains("Error") || title.contains("page not found")
                    || title.contains("Webpage not available")) {
                    Toast.makeText(this@MainActivity, "Error loading page, please refresh or check your internet connection", Toast.LENGTH_SHORT).show()
                    errorLayout.visibility = View.VISIBLE
                    view!!.loadUrl("about:blank");// Avoid the default error interface
                    //view.loadUrl(mErrorUrl);// Load a custom error page
                }

                /*if(title.toString().contains("enginexpace") || title.toString().contains("Enginexpace") || title.toString().contains("Store")
                    || title.toString().contains("Tutorials")){
                    view!!.loadUrl(view.url.toString())
                }else{
                    val chromeUri = view!!.url.toString()
                    val chromeBuilder = CustomTabsIntent.Builder()

                    chromeBuilder.setToolbarColor(resources.getColor(R.color.colorPrimaryDark))

                    chromeBuilder.setShareState(CustomTabsIntent.SHARE_STATE_OFF)

                    chromeBuilder.setShowTitle(true)

                    val chromeIntent = chromeBuilder.build()

                    chromeIntent.launchUrl(this@MainActivity, Uri.parse(chromeUri))
                    view.stopLoading()
                    view!!.loadUrl("about:blank")
                }*/

              /*  if (title.contains("EngineXpace") || !title.contains("http://enginexpace.site") || !title.startsWith("http://enginexpace")) {
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse(view!!.url))
                    startActivity(i)
                // Avoid the default error interface
                    //view.loadUrl(mErrorUrl);// Load a custom error page
                }*/

            }
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            webView.settings.safeBrowsingEnabled = true
        }
    }

    private fun refreshApp(){
        refresh.setOnRefreshListener {
            webView.loadUrl(webView.url.toString())

            refresh.isRefreshing = false
        }
    }

    private fun reviewApp(){
        val manager = ReviewManagerFactory.create(this.applicationContext)

        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = task.result
                hasReviewed(true)

                val flow = manager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    hasReviewed(true)
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                }
            } else {
                // There was some problem, log or handle the error code.
                Log.d("Error: ", task.exception.toString())
            }
        }
    }

    private fun hasReviewed(boolean: Boolean){
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        editor.putBoolean(KEY_HASNOTREVIEWED, boolean)
        editor.apply()
    }

    private fun exitApp(){
        if(sharedPreferences!!.getBoolean(KEY_HASNOTREVIEWED, true)){
            reviewApp()
            Toast.makeText(applicationContext, "Press back again to exit", Toast.LENGTH_SHORT).show()
        } else{
            Toast.makeText(applicationContext, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        if(webView.canGoBack()){ webView.goBack()}

        if (backPressedTime + 2000 > System.currentTimeMillis()){
            super.onBackPressed()
        }

        else {
            exitApp()
        }

        backPressedTime = System.currentTimeMillis()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}