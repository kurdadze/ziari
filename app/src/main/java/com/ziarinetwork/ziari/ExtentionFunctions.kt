package com.ziarinetwork.ziari

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker

fun Marker.loadCustomIcon(context: Context, imageUrl: String?, width: Int, height: Int) {
    Glide.with(context)
        .asBitmap()
        .load(imageUrl)
        .circleCrop()
        .error(R.drawable.default_marker_photo_24)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                val resizedBitmap = Bitmap.createScaledBitmap(resource, width, height, false)

                setIcon(BitmapDescriptorFactory.fromBitmap(resizedBitmap))
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                setIcon(BitmapDescriptorFactory.fromResource(R.drawable.default_marker_photo_24))
            }
        })
}

