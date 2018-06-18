package com.cog.justdeploy.activity

import android.support.test.InstrumentationRegistry
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito

class GmailActivityinstruTest {

    /*@Rule
    @JvmField
    public var gmailActivity = ActivityTestRule<GmailActivity>(GmailActivity::class.java)*/

    @Mock
    //var context: Context? = null
    @InjectMocks
    public var gmailActivity: GmailActivity? = null

    @Before
    fun setUp() {
        gmailActivity = Mockito.mock(GmailActivity::class.java)

    }

    @Test
    fun urlDetailsTest() {
        val email = "gokulapriya@cogzidel.com"
        val uid = "0aA0PWbd2QY5TXaHtjK4UOY9r6c2"
        val fname = "Gokula"
        val lname = "Priya"
        val profile = "https://lh6.googleusercontent.com/-h8jw5BnO4us/AAAAAAAAAAI/AAAAAAAAADQ/X-bxS0dalX4/s96-c/photo.jpg"
        /*val actual = gmailActivity?.runOnUiThread { GmailActivity().googleLogin(email,uid,fname,lname,profile) }*/
        val targetContext = InstrumentationRegistry.getTargetContext()
        val actual = gmailActivity?.googleLogin(email,uid,fname,lname,profile)
        println("actual values $actual")
    }

    @Test
    fun updateResultTest() {
        val response = "{\n \"status\": \"300\",\n " +
                "\"message\": \"Logged In Successfully\",\n " +
                "\"data\": {" +
                "\"_id\": \"5af97377ec4fb90014aa58c3\",\n " +
                "\"email\": \"gokulapriya@cogzidel.com\",\n " +
                "\"firstName\": \"Gokula\",\n " +
                "\"lastName\": \"Priya\",\n " +
                "\"profileImage\": \"https://lh6.googleusercontent.com/-h8jw5BnO4us/AAAAAAAAAAI/AAAAAAAAADQ/X-bxS0dalX4/s96-c/photo.jpg\",\n " +
                "\"googleId\": \"0aA0PWbd2QY5TXaHtjK4UOY9r6c2\",\n " +
                "\"createdAt\": \"1526297463640\",\n " +
                "\"lastUpdated\": \"1526297463640\",\n " +
                "\"__v\": \"0\",\n " +
                "}}"
        val checkResponse = gmailActivity?.updateResult(response)
        println("actual updateResult values $checkResponse")
    }

}