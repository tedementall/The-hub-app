package com.example.thehub.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.thehub.R
import com.example.thehub.data.model.News
import com.example.thehub.databinding.BottomSheetBlogDetailBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.wasabeef.blurry.Blurry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewsDetailBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetBlogDetailBinding? = null
    private val binding get() = _binding!!

    private var news: News? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetBlogDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomSheetBehavior()
        news?.let { setupNewsDetails(it) }
    }

    override fun onStart() {
        super.onStart()
        applyBlur()
    }

    override fun onStop() {
        removeBlur()
        super.onStop()
    }

    private fun applyBlur() {
        try {
            activity?.window?.decorView?.let { decorView ->
                val contentView = decorView.findViewById<ViewGroup>(android.R.id.content)
                Blurry.with(requireContext())
                    .radius(25)
                    .sampling(2)
                    .color(0x33FFFFFF.toInt())
                    .async()
                    .animate(200)
                    .onto(contentView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            dialog?.window?.setDimAmount(0.75f)
        }
    }

    private fun removeBlur() {
        try {
            activity?.window?.decorView?.let { decorView ->
                val contentView = decorView.findViewById<ViewGroup>(android.R.id.content)
                Blurry.delete(contentView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupBottomSheetBehavior() {
        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as? BottomSheetDialog
            val bottomSheet = bottomSheetDialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }

    private fun setupNewsDetails(item: News) {
        binding.apply {

            tvBlogTitle.text = item.title
            tvBlogContent.text = item.body


            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            tvBlogDate.text = if (item.createdAt != null) {
                sdf.format(Date(item.createdAt))
            } else {
                "Fecha no disponible"
            }


            item.cover?.firstOrNull()?.url?.let { url ->
                Glide.with(requireContext()).load(url).into(ivBlogImage)
            }

            ivClose.setOnClickListener { dismiss() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(news: News): NewsDetailBottomSheet {
            return NewsDetailBottomSheet().apply {
                this.news = news
            }
        }
    }
}