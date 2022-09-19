package com_story.example.lib.lib_ui

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.lib.R
import com_story.example.lib.lib_interface.StoryListener
import com_story.example.lib.lib_model.StoryLocal

class ViewPagerAdapter(
    images: ArrayList<StoryLocal>,
    context: Context,
    storyCallbacks: StoryListener,
) :
    PagerAdapter() {
    private val images: ArrayList<StoryLocal>
    private val context: Context
    private val storyCallbacks: StoryListener
    private var storiesStarted = false
    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val currentStoryLocal: StoryLocal = images[position]
        val view: View = inflater.inflate(R.layout.layout_story_item, collection, false)
        val imageStory = view.findViewById<ImageView>(R.id.imageStory)

        Glide.with(context)
            .load(currentStoryLocal.image)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    storyCallbacks.nextStory()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource != null) {
                        val pe = PaletteExtraction(
                            view.findViewById(R.id.relativeLayout),
                            (resource as BitmapDrawable).bitmap
                        )
                        pe.PaletteExtractionExecute()
                    }
                    if (!storiesStarted) {
                        storiesStarted = true
                        storyCallbacks.startStories()
                    }
                    return false
                }
            })
            .into(imageStory)

        val btnLearn = view.findViewById<AppCompatButton>(R.id.btn_LearnMore)
        btnLearn.setOnClickListener { storyCallbacks.onDescriptionClickListener(position) }
        collection.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    init {
        this.images = images
        this.context = context
        this.storyCallbacks = storyCallbacks
    }
}
