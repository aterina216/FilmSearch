package com.example.filmsearch.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.core_impl.entity.ApiConstants
import com.example.filmsearch.databinding.MergePromoBinding

class PromoView(context: Context, attributeSet: AttributeSet?) : FrameLayout(context, attributeSet) {
    val binding = MergePromoBinding.inflate(LayoutInflater.from(context), this)
    val watchButton = binding.watchButton

    fun setLinkForPoster(link: String) {
        Glide.with(binding.root)
            .load(ApiConstants.IMAGES_URL  + link)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(55)))
            .into(binding.poster)
    }
}