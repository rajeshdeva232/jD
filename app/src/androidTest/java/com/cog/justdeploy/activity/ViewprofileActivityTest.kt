package com.cog.justdeploy.activity

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import android.support.test.espresso.action.ReplaceTextAction
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.UiThreadTestRule
import android.support.test.runner.AndroidJUnit4
import com.cog.justdeploy.R
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class ViewprofileActivityTest {


    @Rule
    @JvmField
    val activity = ActivityTestRule<ViewprofileActivity>(ViewprofileActivity::class.java)


    @Mock
    //var context: Context? = null
    @InjectMocks
    lateinit var ViewActivity: ViewprofileActivity


    @Before
    fun setUp()  {
        ViewActivity = Mockito.mock(ViewprofileActivity::class.java)
    }
    @Test
    @Throws(Exception::class)
    fun urlDetailsTest() {
        val id = "5b1fbb01e4bae50014a51e93"
        val actual = ViewActivity.getProfileDetails(id)
        println("actual values $actual")
    }

    @Test
    @Throws(Exception::class)
    fun viewSampleTest() {
        val firstName = "Rajesh"
        val lastName = "Deva"
        val email = "rajesh@cogzidel.com"
        onView(withId(R.id.etFirstName)).perform(ReplaceTextAction(firstName)).check(matches(withText(firstName)));
        onView(withId(R.id.etLastName)).perform(ReplaceTextAction(lastName)).check(matches(withText(lastName)));
        onView(withId(R.id.etEmail)).perform(ReplaceTextAction(email)).check(matches(withText(email)));
        onView(withId(R.id.btnSignout)).perform(ViewActions.click())
        onView(withId(R.id.cancel_desc)).perform(ViewActions.click())
        onView(withId(R.id.btnSignout)).perform(ViewActions.click())
        onView(withId(R.id.ok)).perform(ViewActions.click())
    }
}
