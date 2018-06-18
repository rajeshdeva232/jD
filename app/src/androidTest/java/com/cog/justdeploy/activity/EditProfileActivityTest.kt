package com.cog.justdeploy.activity

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ReplaceTextAction
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.cog.justdeploy.R
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import android.support.test.espresso.intent.rule.IntentsTestRule;
import org.junit.Before
import org.mockito.junit.MockitoJUnit.rule




@RunWith(AndroidJUnit4::class)
class EditProfileActivityTest {

    @Rule
    @JvmField
    val activity = ActivityTestRule<EditProfileActivity>(EditProfileActivity::class.java)



    @Mock
    //var context: Context? = null
    @InjectMocks
    lateinit var editActivity: EditProfileActivity

    @Before
    fun setUp() {
        editActivity = Mockito.mock(EditProfileActivity::class.java)
    }

    @Test
    @Throws(Exception::class)
    fun updateResultTest() {
        val response = "{\n \"status\": \"300\",\n " +
                "\"message\": \"User Updated Successfully\"}"
        val checkResponse = editActivity.updateResult(response)
        println("actual updateResult values $checkResponse")
    }

    @Test
    @Throws(Exception::class)
    fun urlDetailsTest() {
        val id = "5af97377ec4fb90014aa58c3"
        val fname = "Gokula"
        val lname = "Priya"
        val email = "Priya@cogzidel.com"
        val profile = "https://lh6.googleusercontent.com/-h8jw5BnO4us/AAAAAAAAAAI/AAAAAAAAADQ/X-bxS0dalX4/s96-c/photo.jpg"
        val actual = editActivity.editProfile(id, email, fname, lname, profile)
        println("actual values $actual")
    }

    @Test
    @Throws(Exception::class)
    fun validateFirstNameTest() {
        val firstNameEmpty = ""
        val emptyfirst = editActivity.validateFirstName(firstNameEmpty)
        emptyfirst.let { assertTrue(it) }
        println("actual updateResult values $emptyfirst")
        val firstName = "Priya"
        val validfirst = editActivity.validateFirstName(firstName)
        validfirst.let { assertTrue(it) }
        println("actual updateResult values $validfirst")
    }

    @Test
    @Throws(Exception::class)
    fun validateLastNameTest() {
        val LastNameEmpty = ""
        val empty = editActivity.validateLastName(LastNameEmpty)
        empty.let { assertFalse(it) }
        println("actual updateResult values $empty")
        val lastName = "Priya"
        val valid = editActivity.validateLastName(lastName)
        valid.let { assertTrue(it) }
        println("actual updateResult values $valid")
    }

    @Test
    @Throws(Exception::class)
    fun editSampleTest() {
        val firstName = "Priya"
        val lastName = "Dharshini"
        val email = "riya@cog.com"
        onView(withId(R.id.etFirstName)).perform(ReplaceTextAction(firstName), closeSoftKeyboard())
        onView(withId(R.id.etLastName)).perform(ReplaceTextAction(lastName), closeSoftKeyboard())
        onView(withId(R.id.etEmail)).perform(ReplaceTextAction(email), closeSoftKeyboard())
        onView(withId(R.id.btnSave)).perform(click())
    }


}
