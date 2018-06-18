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
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.cog.justdeploy.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class WelcomeActivityTest {


    @Rule
    @JvmField
    val activity = ActivityTestRule<WelcomeActivity>(WelcomeActivity::class.java)


    @Mock
    //var context: Context? = null
    @InjectMocks
    var  Welcome: WelcomeActivity=Mockito.mock(WelcomeActivity::class.java)


    @Before
    fun setUp()  {

    }

    @Test
    @Throws(Exception::class)
    fun viewSampleTest() {
        // Welcome = Mockito.mock(WelcomeActivity::class.java)
        onView(withId(R.id.ltGoogle)).perform(ViewActions.click())
    }
}
