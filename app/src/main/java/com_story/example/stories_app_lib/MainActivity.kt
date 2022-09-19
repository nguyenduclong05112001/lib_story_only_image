package com_story.example.stories_app_lib

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com_story.example.lib.StoryBuilder
import com_story.example.lib.api.APIServer
import com_story.example.lib.lib_interface.OnStoryChangedListener
import com_story.example.lib.lib_interface.StoryClickListeners
import com_story.example.lib.lib_model.StoryAPI
import com_story.example.lib.lib_model.StoryDocsAPI
import com_story.example.lib.lib_model.StoryLocal
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var story: StoryAPI
    private val urlInfo =
        "data:image/jpeg;base64,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"
    private val btnShow by lazy { findViewById<Button>(com.example.stories_app_lib.R.id.btnShowStory) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.stories_app_lib.R.layout.activity_main)
        btnShow.setOnClickListener {
            showStories()
        }
        btnShow.visibility = View.GONE
        getDatafromAPI()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getDatafromAPI() {
        GlobalScope.launch(Dispatchers.IO) {
            val result = APIServer.api.getnewStory()
            withContext(Dispatchers.Main) {
                if (result.isSuccessful) {
                    story = result.body()!!
                    Log.d("NEWTAG", "getDatafromAPI: $result")
                    btnShow.visibility = View.VISIBLE
                } else {
                    btnShow.visibility = View.GONE
                }
            }
        }
    }

    fun showStories() {
        val myStories: ArrayList<StoryLocal?> = ArrayList()

        val urls: ArrayList<StoryDocsAPI> = story.data.news.docs

        Log.d("ádasdasd", "showStories: ${urls.size}")

        urls.forEach {
            Log.d("ádasdasd", "showStories: ${it.video}")
        }

        for (item in urls) {
            if (item.video.isEmpty()) {
                myStories.add(StoryLocal(item.cover, item.video, item.text))
            } else {
                myStories.add(StoryLocal(item.cover, item.video, item.text, 15000L))
            }
        }

        StoryBuilder(supportFragmentManager)
            .setStoriesList(myStories)
            .setRtl(false)
            .setStoryClickListeners(object : StoryClickListeners {
                override fun onDescriptionClickListener(position: Int) {
                }

                override fun onTitleIconClickListener(position: Int) {
                    // do some thing when avatar user
                }
            })
            .setOnStoryChangedCallback(object : OnStoryChangedListener {
                override fun storyChanged(position: Int) {
                }
            })
            .setStartingIndex(0)
            .build()
            .show()
    }

}