package com.example.comicreader



import android.view.View
import androidx.test.rule.ActivityTestRule
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import androidx.test.espresso.action.ViewActions.typeText
import android.R
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId


class AddChapterActivityTest {

    @get:Rule
    var activityTestRule = ActivityTestRule(UploadComicActivity::class.java)
    var mActivity = activityTestRule.activity

    @Before
    fun setUp() {
    }
    @Test
    fun UploadTest()
    {

    }

    @After
    fun tearDown() {
    }
}