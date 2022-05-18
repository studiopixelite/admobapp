package com.admob.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroCustomLayoutFragment
import com.github.appintro.AppIntroPageTransformerType

class IntroActivity : AppIntro() {

    val MYPREFRENCES = "Preferences"
    val KEY_ISFIRSTTIME = "isFirstTime"
    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(MYPREFRENCES, Context.MODE_PRIVATE);

        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.fragment_intro_one))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.fragment_intro_two))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.fragment_intro_three))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setIndicatorColor(
                selectedIndicatorColor = getColor(R.color.colorPrimary),
                unselectedIndicatorColor = Color.GRAY
            )
            setColorDoneText(
                colorDoneText = getColor(R.color.colorPrimary)
            )

            setColorSkipButton(
                colorSkipButton = getColor(R.color.colorPrimary)
            )

        }

        setNextArrowColor(R.color.colorPrimary)

        setTransformer(AppIntroPageTransformerType.Fade)
    }

    @SuppressLint("CommitPrefEdits")
    override fun onSkipPressed(currentFragment: Fragment?) {
        isFirstTime(false)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        // splashScreen.isFirstTime(false)
        isFirstTime(false)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun isFirstTime(boolean: Boolean){
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        editor.putBoolean(KEY_ISFIRSTTIME, boolean);
        editor.apply();
    }
}