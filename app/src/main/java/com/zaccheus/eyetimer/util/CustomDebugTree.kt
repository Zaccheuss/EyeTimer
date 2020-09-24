package com.zaccheus.eyetimer.util

import timber.log.Timber

class CustomDebugTree : Timber.DebugTree() {
    // Add a universal tag to quickly find all Timber logs in logcat
    // https://www.reddit.com/r/androiddev/comments/4zfkup/an_easy_way_to_add_a_universal_tag_to_all_timber/
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, "UniversalTag_" + tag, message, t)
    }

    // Add a hyperlink on to each Timber log
    // https://medium.com/@sembozdemir/using-timber-with-crash-reporting-tools-931eafd1296c
    // Not sure if I want to use this or not.
//    override fun createStackElementTag(element: StackTraceElement): String? {
//        with (element) {
//            return "($fileName:$lineNumber)$methodName()"
//        }
//    }
}