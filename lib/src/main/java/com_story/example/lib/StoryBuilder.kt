package com_story.example.lib

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com_story.example.Const.DURATION_KEY
import com_story.example.Const.HEADER_INFO_KEY
import com_story.example.Const.IMAGES_KEY
import com_story.example.Const.IS_RTL_TAG
import com_story.example.Const.STARTING_INDEX_TAG
import com_story.example.lib.lib_interface.OnStoryChangedListener
import com_story.example.lib.lib_interface.StoryClickListeners
import com_story.example.lib.lib_model.StoryLocal
import com_story.example.lib.lib_model.StoryViewHeaderInfoLocal
import java.util.ArrayList

class StoryBuilder(private val fragmentManager: FragmentManager) {

    private val TAG = StoryBuilder::class.java.simpleName

    private var storyView: StoryView? = null
    private val bundle: Bundle = Bundle()
    private val storyViewHeaderInfoLocal: StoryViewHeaderInfoLocal = StoryViewHeaderInfoLocal()
    private var headingInfoList: ArrayList<StoryViewHeaderInfoLocal>? = null
    private var storyClickListeners: StoryClickListeners? = null
    private var onStoryChangedCallback: OnStoryChangedListener? = null

    fun setStoriesList(storiesList: ArrayList<StoryLocal?>?): StoryBuilder {
        bundle.putSerializable(IMAGES_KEY, storiesList)
        return this
    }

    fun setTitleText(title: String?): StoryBuilder {
        storyViewHeaderInfoLocal.title = title
        return this
    }

    fun setSubtitleText(subtitle: String?): StoryBuilder {
        storyViewHeaderInfoLocal.subtitle = subtitle
        return this
    }

    fun setTitleLogoUrl(url: String?): StoryBuilder {
        storyViewHeaderInfoLocal.titleIconUrl = url
        return this
    }

    fun setStoryDuration(duration: Long): StoryBuilder {
        bundle.putLong(DURATION_KEY, duration)
        return this
    }

    fun setStartingIndex(index: Int): StoryBuilder {
        bundle.putInt(STARTING_INDEX_TAG, index)
        return this
    }

    fun build(): StoryBuilder {
        if (storyView != null) {
            Log.e(TAG, "The StoryView has already been built!")
            return this
        }
        storyView = StoryView()
        bundle.putSerializable(
            HEADER_INFO_KEY,
            if (headingInfoList != null) headingInfoList else storyViewHeaderInfoLocal
        )
        storyView!!.arguments = bundle
        if (storyClickListeners != null) {
            storyView!!.setStoryClickListeners(storyClickListeners)
        }
        if (onStoryChangedCallback != null) {
            storyView!!.setOnStoryChangedCallback(onStoryChangedCallback)
        }
        return this
    }

    fun setOnStoryChangedCallback(onStoryChangedCallback: OnStoryChangedListener?): StoryBuilder {
        this.onStoryChangedCallback = onStoryChangedCallback
        return this
    }

    fun setRtl(isRtl: Boolean): StoryBuilder {
        bundle.putBoolean(IS_RTL_TAG, isRtl)
        return this
    }

    fun setHeadingInfoList(headingInfoList: ArrayList<StoryViewHeaderInfoLocal>?): StoryBuilder {
        this.headingInfoList = headingInfoList
        return this
    }

    fun setStoryClickListeners(storyClickListeners: StoryClickListeners?): StoryBuilder {
        this.storyClickListeners = storyClickListeners
        return this
    }

    fun show() {
        storyView!!.show(fragmentManager, TAG)
    }

    fun dismiss() {
        storyView!!.dismiss()
    }

    val fragment: Fragment?
        get() = storyView

}