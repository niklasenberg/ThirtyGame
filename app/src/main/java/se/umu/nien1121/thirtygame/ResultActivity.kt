package se.umu.nien1121.thirtygame

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import se.umu.nien1121.thirtygame.databinding.ActivityResultBinding

/**
 * Secondary activity for game application, presents user with results of their dice rolls
 */
class ResultActivity : AppCompatActivity() {

    //Views handled via binding
    private lateinit var binding: ActivityResultBinding

    /**
     * Cache of results to be presented
     */
    private lateinit var results: ArrayList<String>

    /**
     * Inflates views and handles intent or saved state
     * @param savedInstanceState: Bundle which is created on Activity destruction.
     * Initialized in [onSavedInstanceState()]
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Get results from intent or saved state
        results = if (savedInstanceState != null) {
            savedInstanceState.getStringArrayList(MainActivity.RESULTS_KEY)!!
        } else {
            intent.getStringArrayListExtra(MainActivity.RESULTS_KEY)!!
        }

        setViews()
        setResult(Activity.RESULT_OK)
    }

    /**
     * Sets text of textview and listview via [results], also sets listener for done button
     */
    private fun setViews() {
        //Total is first element
        binding.totalScoreTextView.text = getString(R.string.total_score, results[0])

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, results.slice(1..10)
        )
        binding.resultListView.adapter = adapter

        binding.doneButton.setOnClickListener { finish() }
    }

    /**
     * Stores activity state when activity is destroyed
     * @param outState Bundle of data to restore [onCreate] or [onRestoreInstanceState]
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList(MainActivity.RESULTS_KEY, results)
    }
}