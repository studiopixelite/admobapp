package com.pixelite.magenta

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import github.com.st235.lib_expandablebottombar.ExpandableBottomBar
import github.com.st235.lib_expandablebottombar.ExpandableBottomBarMenuItem
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

import java.lang.Exception
import java.lang.reflect.Parameter
import java.text.SimpleDateFormat
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private val RQ_SPEECH: Int = 10
    var appIntent: Intent? = null
    lateinit var wifiMng: WifiManager
    val btAdapter = BluetoothAdapter.getDefaultAdapter()
    private var tts: TextToSpeech? = null

    var cal: Date? = null
    var simpleDate: SimpleDateFormat? = null
    var date: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tts = TextToSpeech(this, this)


        button.setOnClickListener {
            checkAudioPermission()
            button.setColorFilter(ContextCompat.getColor(this, R.color.mic_enabled))
            getSpeechInput()
        }
    }

    private fun checkAudioPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") != PackageManager.PERMISSION_GRANTED)
            {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("com.pixelite.magenta"))
                startActivity(intent)
                Toast.makeText(this, "Allow Microphone Permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getSpeechInput(){
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle?){}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray?) {}
            override fun onEndOfSpeech(){
                button.setColorFilter(ContextCompat.getColor(applicationContext, R.color.mic_disabled))
            }

            override fun onError(i: Int) {}

            override fun onResults(bundle: Bundle) {
                val result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if(result != null) {
                    //textView.text = result[0]
                    textView.text = result?.get(0).toString()
                    openApp()
                    openWebsite()
                    manageSettings()
                    exitApp()
                    googleSearch()
                }
            }
            override fun onPartialResults(bundle: Bundle) {}

            override fun onEvent(i: Int, bundle: Bundle?) {}

        })
        speechRecognizer.startListening(speechRecognizerIntent)
    }

    private fun openApp()
    {
        if(textView.text.contains("open WhatsApp")){
            speakOut("Opening WhatsApp")
        appIntent = packageManager.getLaunchIntentForPackage("com.whatsapp")
        startActivity(appIntent)}

        if(textView.text.contains("open Facebook")){
            speakOut("Opening Facebook")
            appIntent = packageManager.getLaunchIntentForPackage("com.facebook.katana")
            startActivity(appIntent)}

        if(textView.text.contains("open Twitter")){
            speakOut("Opening Twitter")
            appIntent = packageManager.getLaunchIntentForPackage("com.twitter.android")
            startActivity(appIntent)}

        if(textView.text.contains("open Telegram") || textView.text.contains("open telegram")){
            speakOut("Opening Telegram")
            appIntent = packageManager.getLaunchIntentForPackage("org.telegram.messen