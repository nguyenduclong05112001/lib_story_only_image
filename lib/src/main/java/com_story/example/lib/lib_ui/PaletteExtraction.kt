package com_story.example.lib.lib_ui

import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.palette.graphics.Palette
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

class PaletteExtraction(view: View, resource: Bitmap) {
    private lateinit var aPalette: Palette
    private val viewWeakReference: WeakReference<View>
    private val mBitmapWeakReference: WeakReference<Bitmap>

    init {
        viewWeakReference = WeakReference(view)
        mBitmapWeakReference = WeakReference(resource)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun PaletteExtractionExecute() {
        GlobalScope.launch(Dispatchers.IO) {
            if (mBitmapWeakReference.get() != null) {
                aPalette = Palette.from(mBitmapWeakReference.get()!!).generate()
            }
            withContext(Dispatchers.Main){
                val view = viewWeakReference.get()
                if (view != null) {
                    val drawable = GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        intArrayOf(aPalette.getDarkVibrantColor(0), aPalette.getLightMutedColor(0))
                    )
                    drawable.cornerRadius = 0f
                    view.background = drawable
                }
            }
        }
    }
}