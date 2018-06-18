package com.cog.justdeploy.activity

import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.UiThreadTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.cog.justdeploy.R
import com.cog.justdeploy.adapter.BitBucket
import com.cog.justdeploy.adapter.Github
import com.cog.justdeploy.model.BitBucketRepo
import com.cog.justdeploy.model.GithubRepo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
/*@FixMethodOrder(MethodSorters.NAME_ASCENDING)*/
class MainActivityTest/*(activityClass: Class<MainActivity>?) : ActivityInstrumentationTestCase2<MainActivity>(activityClass)*/ {

    /*fun EntryPointTest() {
        super("com.cog.justdeploy.activity",
                com.cog.justdeploy.activity.MainActivity::class.java)
    }*/

    private var githubAdapter: Github? = null
    private var bitbucketAdapter: BitBucket? = null
    private var recyclerView: RecyclerView? = null

    @get:Rule
    var activityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule(
            MainActivity::class.java, true, false)

    @get:Rule
    var uiThreadTestRule = UiThreadTestRule()

    @Before
    @Throws(Throwable::class)
    fun setup() {

    }

    /*@Mock
    //var context: Context? = null
    @InjectMocks
    public var mainActivity: MainActivity? = null
    public var recyclerView: RecyclerView? = null

    val context = Context::class.java

    *//*@get:Rule*//*
    @Rule @JvmField
    public var activityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Before
    *//*override*//* fun setUp() {
        *//*super.setUp()*//*
        *//*MockitoAnnotations.initMocks(this)*//*
        recyclerView = Mockito.mock(RecyclerView::class.java)
        *//*MockitoAnnotations.initMocks(this)*//*
        mainActivity = Mockito.mock(MainActivity::class.java)
    }*/

   /* @Test
    fun adapterTest() {
        *//*onView(withId(R.id.scroll_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(3, click()));*//*
        val items = ArrayList<GithubRepo>()
        items.add(GithubRepo("ob", "12:32", "0", "1234"))
        *//*items.add(GithubRepo("nr", "02:00", "1", "3210"))
        items.add(GithubRepo("sd", "08:45", "2", "1324"))
        items.add(GithubRepo("jd", "04:45", "3", "4454"))*//*
        val con = InstrumentationRegistry.getContext()
        val prefs = PreferenceHelper.defaultPrefs(con)
        prefs[Constants.MONGOID] = "5afbc972f4e73a00140301d3"
        val githubAdapter = *//*Github(items, con)*//* Mockito.mock(Github::class.java)
        recyclerView?.layoutManager = LinearLayoutManager(con)
*//*        githubAdapter.notifyItemChanged(0)
        githubAdapter.notifyDataSetChanged()*//*
        recyclerView?.adapter = githubAdapter
        recyclerView?.getChildAt(0)?.performClick()
//        Mockito.verify( githubAdapter).itemCount

    }

    @Test
    fun githubUrlTest() {
        val con = InstrumentationRegistry.getContext()
        mainActivity = Mockito.mock(MainActivity::class.java)
        val prefs = PreferenceHelper.defaultPrefs(con)
        prefs[Constants.MONGOID] = "5afbc972f4e73a00140301d3"
        val gitUrl = mainActivity?.gitUrl()
        println("gitUrl ==> $gitUrl")
    }*/

    /*onView(withId(R.id.ivArrow)).perform(click())
        onView(withId(R.id.ltGithub)).perform(click())
        val formatter = Formatter()
        val lv = onData(anything()).inAdapterView(withId(R.id.reporecycleView))
        val items = arrayOf(
                GithubRepo("ob", "12:32", "0", "1234"),
                GithubRepo("ob", "12:32", "0", "1234"),
                GithubRepo("ob", "12:32", "0", "1234")
        )

        items.map { pair ->

            addItemToList(pair.first)


        }
        items.mapIndexed { index, pair ->
            val expected = pair.second
            lv.atPosition(index).onChildView(withId(R.id.tv_list_item)).check(
                    matches(withText(equalToIgnoringCase(expected)))
            )
        }*/

    @Test
    fun gitHubTest() {

        val activity = activityTestRule.launchActivity(null)

        uiThreadTestRule.runOnUiThread {
            val context = InstrumentationRegistry.getTargetContext()
            val items = arrayListOf<GithubRepo>(
                    GithubRepo("ORMBOT", "12:32", "0", "1234"),
                    GithubRepo("NEXKAR", "12:32", "0", "1234"),
                    GithubRepo("NUVO", "12:32", "0", "1234")
            )
            githubAdapter = Github(items,context)
            recyclerView = RecyclerView(activity)
            recyclerView?.id = R.id.reporecycleView
            activity.setContentView(recyclerView)
            recyclerView?.layoutManager = LinearLayoutManager(activity)
            recyclerView?.adapter = githubAdapter
        }

    }

    @Test
    fun bitbucketTest() {
        val context = InstrumentationRegistry.getTargetContext()
        val activity = activityTestRule.launchActivity(null)
        /*onView(ViewMatchers.withId(R.id.ivArrow)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ltBitbucket)).perform(ViewActions.click())*/
        uiThreadTestRule.runOnUiThread {
            val context = InstrumentationRegistry.getTargetContext()

            val items = arrayListOf<BitBucketRepo>(
                    BitBucketRepo("NUVO", "12:32", "0", "1234"),
                    BitBucketRepo("NEXKAR", "12:32", "0", "1234"),
                    BitBucketRepo("NEXKAR", "12:32", "0", "1234"),
                    BitBucketRepo("NEXKAR", "12:32", "0", "1234"),
                    BitBucketRepo("NEXKAR", "12:32", "0", "1234"),
                    BitBucketRepo("NEXKAR", "12:32", "0", "1234"),
                    BitBucketRepo("ORMBOT", "12:32", "0", "1234")
            )
            bitbucketAdapter = BitBucket(items,context)
            recyclerView = RecyclerView(activity)
            recyclerView?.id = R.id.reporecycleView
            activity.setContentView(recyclerView)
            recyclerView?.layoutManager = LinearLayoutManager(activity)
            recyclerView?.adapter = bitbucketAdapter
        }

    }

    /*@Rule @JvmField
    public var activityTestRule1 = ActivityTestRule<MainActivity>(MainActivity::class.java)*/

    /*@Test
    fun clickTest() {
        onView(withId(R.id.ivArrow)).perform(click())
        onView(withId(R.id.ltBitbucket)).perform(click())
    }*/

    /*@After
    override fun tearDown() {
    }*/

}