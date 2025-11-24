package com.example.thehub.ui.checkout

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.thehub.databinding.ActivityOrderSuccessBinding
import com.example.thehub.ui.home.HomeActivity

class OrderSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val orderNumber = intent.getIntExtra("ORDER_NUMBER", 0)
        binding.tvOrderNumber.text = "#$orderNumber"

        binding.btnGoToOrders.setOnClickListener {

            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("NAVIGATE_TO", "PROFILE_ORDERS")
            startActivity(intent)
        }
    }
}