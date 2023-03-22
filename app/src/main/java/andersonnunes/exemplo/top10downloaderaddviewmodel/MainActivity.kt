package andersonnunes.exemplo.top10downloaderaddviewmodel


import andersonnunes.exemplo.top10downloaderaddviewmodel.databinding.ActivityMainBinding
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

class FeedEntry {
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""
}

private const val TAG = "MainActivity"
private const val STATE_URL = "feedUrl"
private const val STATE_LIMIT = "feedLimit"

class MainActivity : AppCompatActivity() {

    private var feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedLimit = 10
    private val feedViewModel: FeedViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        Log.d(TAG, "onCreate called")

        val feedAdapter = FeedAdapter(this, R.layout.list_record, EMPTY_FEED_LIST)
        binding.xmlListView.adapter = feedAdapter

        if (savedInstanceState != null) {
            feedUrl = savedInstanceState.getString(STATE_URL).toString()
            feedLimit = savedInstanceState.getInt(STATE_LIMIT)
        }

//        feedViewModel.feedEntries.observe(this,
//            Observer<List<FeedEntry>> { feedEntries -> feedAdapter.setFeedList(feedEntries!!) })
//        feedViewModel.feedEntries.observe(this
//        ) { feedEntries -> feedAdapter.setFeedList(feedEntries ?: EMPTY_FEED_LIST) }
        feedViewModel.feedEntries.observe(
            this
        ) { feedEntries -> feedAdapter.setFeedList(feedEntries ?: EMPTY_FEED_LIST) }
        feedViewModel.downloadUrl(feedUrl.format(feedLimit))
        Log.d(TAG, "onCreate: done")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)

        if (feedLimit == 10) {
            menu?.findItem(R.id.mnu10)?.isChecked = true
        } else {
            menu?.findItem(R.id.mnu25)?.isChecked = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.mnuFree ->
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
            R.id.mnuPaid ->
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
            R.id.mnuSongs ->
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
            R.id.mnu10, R.id.mnu25 -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    feedLimit = 35 - feedLimit
                    Log.d(TAG, "onOptionsItemSelected: ${item.title} setting feedLimit to $feedLimit")
                } else {
                    Log.d(TAG, "onOptionsItemSelected: ${item.title} feedLimit unchanged")
                }
            }
            R.id.mnuRefresh -> feedViewModel.invalidate()
            else ->
                return super.onOptionsItemSelected(item)
        }
        feedViewModel.downloadUrl(feedUrl.format(feedLimit))
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_URL, feedUrl)
        outState.putInt(STATE_LIMIT, feedLimit)
    }
}