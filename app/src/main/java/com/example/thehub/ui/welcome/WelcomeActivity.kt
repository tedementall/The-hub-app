package com.example.thehub.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.thehub.R
import com.example.thehub.databinding.ActivityWelcomeBinding
import com.example.thehub.ui.home.HomeActivity
import com.example.thehub.ui.login.LoginActivity
import com.example.thehub.utils.TokenStore

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)


        if (TokenStore.isLoggedIn(this)) {
            goToHomeAndFinish()
            return
        }


        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimations()
        setupListeners()
    }

    private fun setupAnimations() {

        binding.ivLogoWelcome.apply {
            alpha = 0f
            scaleX = 0.3f
            scaleY = 0.3f
            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(700)
                .setInterpolator(OvershootInterpolator(1.5f))
                .start()
        }


        binding.cardMain.apply {
            translationY = 100f
            alpha = 0f
            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(200)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }


        binding.btnGetStarted.apply {
            translationY = 100f
            alpha = 0f
            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(700)
                .setStartDelay(500)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }


        animateDecorativeCircles()
    }

    private fun animateDecorativeCircles() {
        try {
            val circleDecor1 = binding.root.findViewById<android.view.View>(
                resources.getIdentifier("circleDecor1", "id", packageName)
            )
            circleDecor1?.let {
                val animator1 = ObjectAnimator.ofFloat(it, "rotation", 0f, 360f).apply {
                    duration = 25000
                    repeatCount = ObjectAnimator.INFINITE
                    interpolator = LinearInterpolator()
                }
                animator1.start()
            }
        } catch (e: Exception) {

        }
    }

    private fun setupListeners() {

        binding.btnGetStarted.setOnClickListener { view ->


            view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(70)
                .withEndAction {

                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(70)
                        .start()
                }
                .start()


            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        // Transición de deslizamiento (Slide)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)


    }

    private fun goToHomeAndFinish() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
}