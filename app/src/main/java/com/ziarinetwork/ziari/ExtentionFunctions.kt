package com.ziarinetwork.ziari

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Canvas
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

fun Marker.loadCustomIconWithTheme(
    context: Context,
    imageUrl: String?,
    width: Int,
    height: Int,
    backgroundColor: Int = Color.WHITE,
    textColor: Int = Color.BLACK,
    label: String? = null
) {
    Glide.with(context)
        .asBitmap()
        .load(imageUrl)
        .error(R.drawable.default_marker_photo_24)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val themedBitmap = createThemedMarkerIcon(resource, width, height, backgroundColor, textColor, label)
                setIcon(BitmapDescriptorFactory.fromBitmap(themedBitmap))
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                setIcon(BitmapDescriptorFactory.fromResource(R.drawable.default_marker_photo_24))
            }
        })
}

private fun createThemedMarkerIcon(
    image: Bitmap,
    width: Int,
    height: Int,
    backgroundColor: Int,
    textColor: Int,
    label: String? = null
): Bitmap {
    // Create a Bitmap for the marker icon
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    // Draw the background (e.g., a circle)
    val paint = Paint()
    paint.color = backgroundColor
    paint.isAntiAlias = true
    canvas.drawCircle(width / 2f, height / 2f, width.coerceAtMost(height) / 2f, paint)

    // Draw the resized image on top of the background
    val resizedImage = Bitmap.createScaledBitmap(image, width * 3 / 4, height * 3 / 4, false)
    val imageLeft = (width - resizedImage.width) / 2f
    val imageTop = (height - resizedImage.height) / 2f
    canvas.drawBitmap(resizedImage, imageLeft, imageTop, null)

    // Optionally draw the label below the image
    label?.let {
        paint.color = textColor
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = height / 8f
        canvas.drawText(it, width / 2f, height.toFloat() - 10f, paint)
    }

    return bitmap
}

fun getCircularBitmapWithBorder(bitmap: Bitmap, borderWidth: Float, borderColor: Int): Bitmap {
    val diameter = Math.min(bitmap.width, bitmap.height)
    val output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)

    val canvas = android.graphics.Canvas(output)
    val paint = Paint()
    paint.isAntiAlias = true

    // Draw the circular bitmap
    val radius = diameter / 2f
    canvas.drawCircle(radius, radius, radius, paint)

    // Draw the bitmap in the circle
    val bitmapRect = android.graphics.Rect(0, 0, diameter, diameter)
    canvas.drawBitmap(bitmap, null, bitmapRect, paint)

    // Draw the border
    paint.color = borderColor
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = borderWidth
    canvas.drawCircle(radius, radius, radius - borderWidth / 2, paint)

    return output
}