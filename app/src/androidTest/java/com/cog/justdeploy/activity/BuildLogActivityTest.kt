package com.cog.justdeploy.activity

import android.app.Instrumentation
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
class BuildLogActivityTest {


    @Rule
    @JvmField
    val activity = ActivityTestRule<BuildLogActivity>(BuildLogActivity::class.java)

    @get:Rule
    var uiThreadTestRule = UiThreadTestRule()


    @Mock
    //var context: Context? = null
    @InjectMocks
    lateinit var Build: BuildLogActivity


    @Before
    fun setUp() {
        Build = Mockito.mock(BuildLogActivity::class.java)
    }

    @Test
    @Throws(Exception::class)
    fun urlDetailsTest() {

        val a = InstrumentationRegistry.getInstrumentation().runOnMainSync {


            activity.runOnUiThread {

                val response = "{\n" +
                        "\"status\": 200,\n" +
                        "\"message\": \"gitUpdate Updated Successfully\"\n" +
                        "}"
                //val con = InstrumentationRegistry.getTargetContext()

                val actual = Build.updateBuildResult(response)
            }
        }

    }
    @Test
    @Throws(Exception::class)
    fun deployGit() {


        val a = InstrumentationRegistry.getInstrumentation().runOnMainSync {

            activity.runOnUiThread {

                val token ="fdc0f5c9d6e5909361f5e355271e0e8d4b1ff7bb"
                val branch ="master"
                val repoName ="Demo_project"
                val appName ="sampleappnodejs"
                val herokuAuthKey ="42848620-0938-42c5-a624-533f828b249c"
                val repoUser ="rajeshdeva23"
                //val con = InstrumentationRegistry.getTargetContext()

                val actual = Build.deployGit(token, branch, repoName, appName, herokuAuthKey, repoUser)
            }
       }

    }
    @Test
    @Throws(Exception::class)
    fun buildCreate() {


        val a = InstrumentationRegistry.getInstrumentation().runOnMainSync {

            activity.runOnUiThread {

                val token ="fdc0f5c9d6e5909361f5e355271e0e8d4b1ff7bb"
                val branch ="master"
                val repoName ="Demo_project"
                val appName ="sampleappnodejs"
                val herokuAuthKey ="42848620-0938-42c5-a624-533f828b249c"
                val repoUser ="rajeshdeva23"
                //val con = InstrumentationRegistry.getTargetContext()

                val actual = Build.deployGit(token, branch, repoName, appName, herokuAuthKey, repoUser)
            }
        }

    }


    /* @Test
     @Throws(Exception::class)
     fun viewSampleTest() {
         val firstName = "Priya"
         val lastName = "Dharshini"
         val email = "riya@cog.com"
         onView(withId(R.id.etFirstName)).perform(ReplaceTextAction(firstName)).check(matches(withText(firstName)));
         onView(withId(R.id.etLastName)).perform(ReplaceTextAction(lastName)).check(matches(withText(lastName)));
         onView(withId(R.id.etEmail)).perform(ReplaceTextAction(email)).check(matches(withText(email)));
         onView(withId(R.id.btnSignout)).perform(ViewActions.click())
         onView(withId(R.id.cancel_desc)).perform(ViewActions.click())
         onView(withId(R.id.btnSignout)).perform(ViewActions.click())
         onView(withId(R.id.ok)).perform(ViewActions.click())
     }*/

   /* @Test
    @Throws(Exception::class)
    fun buildCreateTest() {


        val actual = build
        println("actual values $actual")
    }
*/
   /* @Test
    fun deployGitTest() {
        val gbAuthkey = "762fefa84f40debd6f66a47ff5c7f1da9e7b83c7"
        val repoName = "locallibrary"
        val appName = "jd-test-git"
        val repoBranchName = "master"
        val repoUser = "niti15"
        val herokuAuthKey = "d01a4446-aeec-4fe2-932c-c1c251ec77ff"


        val targetContext = InstrumentationRegistry.getTargetContext()
        val actual = build.deployGit(gbAuthkey, repoBranchName, repoName, appName, herokuAuthKey, repoUser)
        println("actual values $actual")
    }*/
/*
    @Test
    @Throws(Exception::class)
    fun updateGitBitResultTest() {
        val response = "{\n" +
                "\"status\": 200,\n" +
                "\"message\": \"Build Started\",\n" +
                "\"data\": {\n" +
                "\"app\": {\n" +
                "\"id\": \"deecd464-ec5d-48fa-8ded-186121d0312a\"\n" +
                "},\n" +
                "\"buildpacks\": [\n" +
                "{\n" +
                "\"url\": \"https://codon-buildpacks.s3.amazonaws.com/buildpacks/heroku/nodejs.tgz\"\n" +
                "}\n" +
                "],\n" +
                "\"created_at\": \"2018-06-11T07:10:32Z\",\n" +
                "\"id\": \"672e54f3-2a4b-4670-98f1-13b0cf8e3054\",\n" +
                "\"output_stream_url\": \"https://build-output.heroku.com/streams/de/deecd464-ec5d-48fa-8ded-186121d0312a/logs/67/672e54f3-2a4b-4670-98f1-13b0cf8e3054.log?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAIQI6BAUWXGR4S77Q%2F20180611%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20180611T071032Z&X-Amz-Expires=86400&X-Amz-SignedHeaders=host&X-Amz-Signature=7cf8e50ffdf78f23abecdc08c18925e2c8eb1ceedca98b8d915d0fde2ea5bcb0\",\n" +
                "\"release\": null,\n" +
                "\"slug\": null,\n" +
                "\"source_blob\": {\n" +
                "\"checksum\": null,\n" +
                "\"url\": \"https://codeload.github.com/niti15/locallibrary/legacy.tar.gz/master\",\n" +
                "\"version\": null,\n" +
                "\"version_description\": null\n" +
                "},\n" +
                "\"stack\": \"heroku-16\",\n" +
                "\"status\": \"pending\",\n" +
                "\"updated_at\": \"2018-06-11T07:10:32Z\",\n" +
                "\"user\": {\n" +
                "\"email\": \"gokulapriya@cogzidel.com\",\n" +
                "\"id\": \"912cef00-9f5d-45b6-8836-4d4fb025a99f\"\n" +
                "}\n" +
                "}\n" +
                "}"
        val checkResponse = Build.updateGitBitResult(response)
        println("actual updateResult values $checkResponse")
    }*/

  /*  @Test
    fun gitUpdateBuildcreateResultTest() {
        val response = "{\n" +
                "\"status\": \"200\",\n" +
                "\"message\": \"BuildHistory Created Successful\",\n" +
                "\"data\": [\n" +
                "{\n" +
                "\"_id\": \"5b1e1758b2ac750014f3bbd9\",\n" +
                "\"userId\": \"5b1a87e579792e00143c09d6\",\n" +
                "\"sourceId\": \"0\",\n" +
                "\"destinationId\": \"0\",\n" +
                "\"repoName\": \"locallibrary\",\n" +
                "\"repoUser\": \"niti15\",\n" +
                "\"repoBranchName\": \"master\",\n" +
                "\"appName\": \"jd-test-git\",\n" +
                "\"gitAuthKey\": \"762fefa84f40debd6f66a47ff5c7f1da9e7b83c7\",\n" +
                "\"gitRefreshKey\": \"762fefa84f40debd6f66a47ff5c7f1da9e7b83c7\",\n" +
                "\"herokuId\": \"912cef00-9f5d-45b6-8836-4d4fb025a99f\",\n" +
                "\"herokuAuthKey\": \"d01a4446-aeec-4fe2-932c-c1c251ec77ff\",\n" +
                "\"herokuRefreshKey\": \"b51ee974-c147-48fd-a1d1-0a6c3119dc7c\",\n" +
                "\"lastDeployStatus\": \"2\",\n" +
                "\"overAllBuildTime\": \"51000\",\n" +
                "\"noOfDeploy\": \"2\",\n" +
                "\"lastUpdated\": \"1528700226809\",\n" +
                "\"createdAt\": \"1528698712279\",\n" +
                "\"__v\": 0\n" +
                "}\n" +
                "],\n" +
                "\"buildHistoryId\": \"5b1e205ab2ac750014f3bd3d\"\n" +
                "}"
        val checkResponse = build.updateBuildcreateResult(response)
        println("actual updateResult values $checkResponse")
    }*/
}
