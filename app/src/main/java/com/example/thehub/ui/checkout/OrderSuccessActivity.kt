package com.example.thehub.ui.checkout

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.thehub.databinding.ActivityOrderSuccessBinding
import com.example.thehub.ui.home.HomeActivity

class OrderSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val orderId = intent.getIntExtra("ORDER_NUMBER", 0)

        setupUI(orderId)
        animateSuccess()
    }

    private fun setupUI(orderId: Int) {

        binding.tvOrderNumber.text = "#$orderId"

        binding.btnGoToOrders.setOnClickListener {

            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }


    private fun animateSuccess() {
        binding.iconSuccess.scaleX = 0f
        binding.iconSuccess.scaleY = 0f

        binding.iconSuccess.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(500)
            .setInterpolator(OvershootInterpolator())
            .start()


        binding.tvOrderNumber.alpha = 0f
        binding.tvOrderNumber.translationY = 50f
        binding.tvOrderNumber.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(200)
            .setDuration(500)
            .start()
    }
}