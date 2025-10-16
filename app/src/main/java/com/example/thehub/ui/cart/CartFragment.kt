package com.example.thehub.ui.cart

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.thehub.R

class CartFragment : Fragment(R.layout.fragment_placeholder) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.tvPlaceholderTitle).text =
            getString(R.string.cart_placeholder_title)
        view.findViewById<TextView>(R.id.tvPlaceholderSubtitle).text =
            getString(R.string.cart_placeholder_subtitle)
    }
}
