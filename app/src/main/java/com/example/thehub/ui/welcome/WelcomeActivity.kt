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
        // 1. Instalar Splash Screen nativo (Siempre va primero)
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // 2. Verificar si ya tiene sesión iniciada
        if (TokenStore.isLoggedIn(this)) {
            goToHomeAndFinish()
            return
        }

        // 3. Cargar el diseño
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimations()
        setupListeners()
    }

    private fun setupAnimations() {
        // Animación del Logo (Efecto Rebote)
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

        // Animación de la Tarjeta Principal (Sube suavemente)
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

        // Animación del Botón (Entra al final)
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

        // Animación sutil de los círculos de fondo (si existen)
        animateDecorativeCircles()
    }

    private fun animateDecorativeCircles() {
        try {
            val circleDecor1 = binding.root.findViewById<android.view.View>(
                resources.getIdentifier("circleDecor1", "id", packageName)
            )
            circleDecor1?.let {
                val animator1 = ObjectAnimator.ofFloat(it, "rotation", 0f, 360f).apply {
                    duration = 25000 // Rotación muy lenta
                    repeatCount = ObjectAnimator.INFINITE
                    interpolator = LinearInterpolator()
                }
                animator1.start()
            }
        } catch (e: Exception) {
            // Ignoramos si no encuentra las vistas decorativas
        }
    }

    private fun setupListeners() {
        // BOTÓN "COMENZAR" - RESPUESTA INSTANTÁNEA
        binding.btnGetStarted.setOnClickListener { view ->

            // 1. Lanzamos la animación visual (Solo para que se vea bonito)
            view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(70) // Muy rápido
                .withEndAction {
                    // Restauramos el tamaño visualmente
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(70)
                        .start()
                }
                .start()

            // 2. ¡NAVEGAMOS YA!
            // No esperamos a que termine la animación. El cambio de pantalla es inmediato.
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        // Transición de deslizamiento (Slide)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        // Opcional: finish() si no quieres que puedan volver atrás a la bienvenida
        // finish()
    }

    private fun goToHomeAndFinish() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        // Transición suave también para el auto-login
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
}