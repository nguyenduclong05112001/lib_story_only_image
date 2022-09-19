package com_story.example.lib.lib_model

import java.io.Serializable

class StoryViewHeaderInfoLocal : Serializable {
    var title: String? = null
    var subtitle: String? = null
    var titleIconUrl: String? = null

    constructor() {}
    constructor(title: String?, subtitle: String?, titleIconUrl: String?) {
        this.title = title
        this.subtitle = subtitle
        this.titleIconUrl = titleIconUrl
    }
}
