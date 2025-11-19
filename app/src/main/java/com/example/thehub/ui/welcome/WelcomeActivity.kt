package com.example.thehub.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.thehub.databinding.ActivityWelcomeBinding
import com.example.thehub.ui.home.HomeActivity
import com.example.thehub.ui.login.LoginActivity
import com.example.thehub.utils.TokenStore

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. INSTALAR SPLASH SCREEN
        // Debe ser lo primero, antes de super.onCreate
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // 2. VERIFICACIÓN DE SESIÓN
        // Comprobamos si ya existe un token guardado
        if (TokenStore.isLoggedIn(this)) {
            // Si está logueado, saltamos la bienvenida y el login,
            // y vamos directo al Home.
            goToHomeAndFinish()
            return // Importante: Detenemos la ejecución para no cargar la UI de bienvenida
        }

        // 3. CARGAR UI DE BIENVENIDA (Si no estaba logueado)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Botón principal "Comenzar"
        binding.btnGetStarted.setOnClickListener {
            // Navegar al Login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            // No llamamos a finish() aquí si quieres que el usuario pueda
            // volver atrás a ver la bienvenida de nuevo.
            // Si prefieres que no vuelva, descomenta la línea de abajo:
            // finish()
        }
    }

    private fun goToHomeAndFinish() {
        val intent = Intent(this, HomeActivity::class.java)
        // Limpiamos el back stack para que al dar "atrás" desde el Home
        // no vuelva al Splash/Bienvenida y salga de la app.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}