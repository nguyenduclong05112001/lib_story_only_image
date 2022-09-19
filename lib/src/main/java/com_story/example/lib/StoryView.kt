package com_story.example.lib

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.lib.R
import com_story.example.Const.HEADER_INFO_KEY
import com_story.example.Const.IMAGES_KEY
import com_story.example.Const.IS_RTL_TAG
import com_story.example.Const.STARTING_INDEX_TAG
import com_story.example.lib.lib_interface.*
import com_story.example.lib.lib_model.StoryLocal
import com_story.example.lib.lib_model.StoryViewHeaderInfoLocal
import com_story.example.lib.lib_progress.StoriesProgressView
import com_story.example.lib.lib_ui.PullDismissLayout
import com_story.example.lib.lib_ui.ViewPagerAdapter
import java.util.*
import kotlin.collections.ArrayList

class StoryView : DialogFragment(),
    StoriesListener,
    StoryListener,
    Listener,
    TouchListener {

    private val TAG = StoryView::class.java.simpleName

    private var storiesList: ArrayList<StoryLocal>? = ArrayList()
    private lateinit var storiesProgressView: StoriesProgressView
    private lateinit var mViewPager: ViewPager
    private var counter = 0
    private var startingIndex = 0
    private var isHeadlessLogoMode = false

    //Heading
    private lateinit var titleTextView: TextView
    private lateinit var subtitleTextView: TextView
    private lateinit var titleCardView: CardView
    private lateinit var titleIconImageView: ImageView
    private lateinit var closeImageButton: ImageButton

    //Touch Events
    private var isDownClick = false
    private var elapsedTime: Long = 0
    private var timerThread: Thread? = null
    private var isPaused = false
    private var width = 0
    private var height = 0
    private var xValue = 0f
    private var yValue = 0f
    private lateinit var storyClickListeners: StoryClickListeners
    private lateinit var onStoryChangedCallback: OnStoryChangedListener
    private var isRtl = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.dialog_stories, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val displaymetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displaymetrics)
        width = displaymetrics.widthPixels
        height = displaymetrics.heightPixels
        // Get field from view
        readArguments()
        setupViews(view)
        setupStories()
    }

    private fun setupStories() {
        storiesProgressView.setStoriesCount(storiesList!!.size)
        storiesProgressView.setStoryDuration(storiesList!!.get(counter).duration)
        updateHeading()
        mViewPager.offscreenPageLimit = 0
        mViewPager.adapter = context?.let { ViewPagerAdapter(storiesList!!, it, this) }
    }

    private fun readArguments() {
        assert(arguments != null)
        storiesList =
            ArrayList<StoryLocal>(arguments?.getSerializable(IMAGES_KEY) as ArrayList<StoryLocal?>?)
        startingIndex = requireArguments().getInt(STARTING_INDEX_TAG, 0)
        isRtl = requireArguments().getBoolean(IS_RTL_TAG, false)
    }

    private fun setupViews(view: View) {
        (view.findViewById<View>(R.id.pull_dismiss_layout) as PullDismissLayout).setListener(this)
        (view.findViewById<View>(R.id.pull_dismiss_layout) as PullDismissLayout).setmTouchCallbacks(
            this
        )
        storiesProgressView = view.findViewById(R.id.storiesProgressView)
        mViewPager = view.findViewById(R.id.storiesViewPager)
        titleTextView = view.findViewById(R.id.title_textView)
        subtitleTextView = view.findViewById(R.id.subtitle_textView)
        titleIconImageView = view.findViewById(R.id.title_imageView)
        titleCardView = view.findViewById(R.id.titleCardView)
        closeImageButton = view.findViewById(R.id.imageButton)
        storiesProgressView.setStoriesListener(this)
//        mViewPager.setOnTouchListener { v: View?, event: MotionEvent? -> true }
        closeImageButton.setOnClickListener(View.OnClickListener { v: View? -> dismissAllowingStateLoss() })
        if (storyClickListeners != null) {
            titleCardView.setOnClickListener(View.OnClickListener { v: View? ->
                storyClickListeners.onTitleIconClickListener(
                    counter
                )
            })
        }
        if (onStoryChangedCallback != null) {
            mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    onStoryChangedCallback.storyChanged(position)
                }

                override fun onPageSelected(position: Int) {
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }
        if (isRtl) {
            storiesProgressView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR)
            storiesProgressView.setRotation(180f)
        }
    }

    override fun onResume() {
        super.onResume()
        val params = dialog!!.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.attributes = params
    }

    override fun onNext() {
        mViewPager!!.setCurrentItem(++counter, false)
        storiesProgressView.setStoryDuration(storiesList!!.get(counter).duration)
        storiesProgressView.startStories(counter)
        updateHeading()
    }

    override fun onPrev() {
        if (counter <= 0) return
        mViewPager.setCurrentItem(--counter, false)
        storiesProgressView.setStoryDuration(storiesList!!.get(counter).duration)
        storiesProgressView.startStories(counter)
        updateHeading()
    }

    override fun onComplete() {
        dismissAllowingStateLoss()
    }

    override fun startStories() {
        counter = startingIndex
        storiesProgressView.startStories(startingIndex)
        mViewPager.setCurrentItem(startingIndex, false)
        storiesProgressView.setStoryDuration(storiesList!!.get(counter).duration)
        updateHeading()
    }

    override fun pauseStories() {
        storiesProgressView.pause()
    }

    private fun previousStory() {
        if (counter - 1 < 0) return
        mViewPager.setCurrentItem(--counter, false)
        storiesProgressView.setStoriesCount(storiesList!!.size)
        storiesProgressView.setStoryDuration(storiesList!!.get(counter).duration)
        storiesProgressView.startStories(counter)
        updateHeading()
    }

    override fun nextStory() {
        if (counter + 1 >= storiesList!!.size) {
            dismissAllowingStateLoss()
            return
        }
        mViewPager!!.setCurrentItem(++counter, false)
        Log.d(TAG, "nextStory: ${storiesList!!.get(counter).duration}")
        storiesProgressView.setStoryDuration(storiesList!!.get(counter).duration)
        storiesProgressView.startStories(counter)
        updateHeading()
    }

    override fun onDescriptionClickListener(position: Int) {
        storyClickListeners.onDescriptionClickListener(position)
    }

    override fun onDestroy() {
        timerThread = null
        storiesList = null
        storiesProgressView.destroy()
        super.onDestroy()
    }

    private fun updateHeading() {
        val `object`: Any? = requireArguments().getSerializable(HEADER_INFO_KEY)
        var storyHeaderInfo: StoryViewHeaderInfoLocal? = null
        if (`object` is StoryViewHeaderInfoLocal) {
            storyHeaderInfo = `object` as StoryViewHeaderInfoLocal?
        } else if (`object` is ArrayList<*>) {
            storyHeaderInfo = `object`[counter] as StoryViewHeaderInfoLocal?
        }
        if (storyHeaderInfo == null) return
        if (storyHeaderInfo.titleIconUrl != null) {
            titleCardView!!.visibility = View.VISIBLE
            if (context == null) return
            Glide.with(requireContext())
                .load(storyHeaderInfo.titleIconUrl)
                .into(titleIconImageView)
        } else {
            titleCardView.visibility = View.GONE
            isHeadlessLogoMode = true
        }
        if (storyHeaderInfo.title != null) {
            titleTextView.visibility = View.VISIBLE
            titleTextView.setText(storyHeaderInfo.title)
        } else {
            titleTextView!!.visibility = View.GONE
        }
        if (storyHeaderInfo.subtitle != null) {
            subtitleTextView.visibility = View.VISIBLE
            subtitleTextView.text = storyHeaderInfo.subtitle
        } else {
            subtitleTextView.visibility = View.GONE
        }
//        if (storiesList!![counter].date != null) {
//            titleTextView!!.text = (titleTextView!!.text
//                .toString() + " "
//                    + getDurationBetweenDates(
//                storiesList!![counter].date,
//                Calendar.getInstance().time
//            ))
//        }
    }

    private fun setHeadingVisibility(visibility: Int) {
        if (isHeadlessLogoMode && visibility == View.VISIBLE) {
            titleTextView.visibility = View.GONE
            titleCardView.visibility = View.GONE
            subtitleTextView.visibility = View.GONE
        } else {
            titleTextView.visibility = visibility
            titleCardView.visibility = visibility
            subtitleTextView.visibility = visibility
        }
        closeImageButton.visibility = visibility
        storiesProgressView.setVisibility(visibility)
    }

    private fun createTimer() {
        timerThread = Thread(label@ Runnable {
            while (isDownClick) {
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                elapsedTime += 100
                if (elapsedTime >= 500 && !isPaused) {
                    isPaused = true
                    if (activity == null) return@Runnable
                    requireActivity().runOnUiThread {
                        storiesProgressView.pause()
                        setHeadingVisibility(View.GONE)
                    }
                }
            }
            isPaused = false
            if (activity == null) return@Runnable
            if (elapsedTime < 500) return@Runnable
            requireActivity().runOnUiThread {
                setHeadingVisibility(View.VISIBLE)
                storiesProgressView.resume()
            }
        })
    }

    private fun runTimer() {
        isDownClick = true
        createTimer()
        timerThread!!.start()
    }

    private fun stopTimer() {
        isDownClick = false
    }

    override fun onDismissed() {
        dismissAllowingStateLoss()
    }

    override fun onShouldInterceptTouchEvent(): Boolean {
        return false
    }

    override fun touchPull() {
        elapsedTime = 0
        stopTimer()
        storiesProgressView.pause()
    }

    override fun touchDown(xValue: Float, yValue: Float) {
        this.xValue = xValue
        this.yValue = yValue
        if (!isDownClick) {
            runTimer()
        }
    }

    override fun touchUp() {
        if (isDownClick && elapsedTime < 500) {
            stopTimer()
            if ((height - yValue).toInt() <= 0.8 * height) {
                if (!TextUtils.isEmpty(storiesList!![counter].description)
                    && (height - yValue).toInt() >= 0.2 * height
                    || TextUtils.isEmpty(storiesList!![counter].description)
                ) {
                    if (xValue.toInt() <= width / 2) {
                        //Left
                        if (isRtl) {
                            nextStory()
                        } else {
                            previousStory()
                        }
                    } else {
                        //Right
                        if (isRtl) {
                            previousStory()
                        } else {
                            nextStory()
                        }
                    }
                }
            }
        } else {
            stopTimer()
            setHeadingVisibility(View.VISIBLE)
            storiesProgressView.resume()
        }
        elapsedTime = 0
    }

    fun setStoryClickListeners(storyClickListeners: StoryClickListeners?) {
        if (storyClickListeners != null) {
            this.storyClickListeners = storyClickListeners
        }
    }

    fun setOnStoryChangedCallback(onStoryChangedCallback: OnStoryChangedListener?) {
        if (onStoryChangedCallback != null) {
            this.onStoryChangedCallback = onStoryChangedCallback
        }
    }
}
