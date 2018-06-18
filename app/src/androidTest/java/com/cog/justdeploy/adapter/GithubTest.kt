package com.cog.justdeploy.adapter

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.UiThreadTestRule
import com.cog.justdeploy.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GithubTest {

    @get:Rule
    var uiThreadTestRule = UiThreadTestRule()

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun valuesTest() {
     Espresso.onView(ViewMatchers.withId(R.id.btnDeply)).perform(ViewActions.click())
    }
}