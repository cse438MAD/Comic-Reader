package com.example.comicreader

import android.view.View
import androidx.test.rule.ActivityTestRule
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class Chapter {


    class ChapterActivityTest {
        @get:Rule
        var activityTestRule = ActivityTestRule(AddChapterActivity::class.java)
        var mActivity = activityTestRule.activity

        @Before
        fun setUp() {
        }
        @Test
        fun Chapter_Test()
        {
            var view : View
            view = mActivity.findViewById(R.id.image_list)
            TestCase.assertNotNull(view)

        }
        @After
        fun tearDown() {
        }
    }
}