package se.umu.nien1121.thirtygame

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import se.umu.nien1121.thirtygame.databinding.ActivityMainBinding

/**
 * Primary activity for game application. Lets user roll die and choose calculation method.
 */
class MainActivity : AppCompatActivity() {

    //Views handled via binding, dice buttons cached in arraylist for ease of operation
    private lateinit var binding: ActivityMainBinding
    private lateinit var diceButtons: ArrayList<ImageButton>

    /**
     * ViewModel used for storing of state data
     */
    private val model: GameViewModel by lazy { ViewModelProvider(this).get(GameViewModel::class.java) }

    /**
     * Initiates new game and resets [View]s on finished [ResultActivity]
     */
    private val resultListener =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                model.startNewGame()
                binding.throwButton.isEnabled = true
                updateViews()
            }
        }

    /**
     * Initiates and inflates binding and views, sets listeners
     * @param savedInstanceState priorly saved state, only used in super constructor
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findDiceButtons()
        setListeners()
        updateViews()
    }

    /**
     * Collects [ImageButton] for each die into ArrayList for iteration purposes
     */
    private fun findDiceButtons() {
        diceButtons = arrayListOf(
            binding.dieOne,
            binding.dieTwo,
            binding.dieThree,
            binding.dieFour,
            binding.dieFive,
            binding.dieSix
        )
    }

    /**
     * Sets listeners for dice buttons and spinner
     */
    private fun setListeners() {
        //Correlate each button to Die in model
        diceButtons.forEachIndexed { index, element ->
            element.setOnClickListener {
                model.selectDie(index)
                updateDiceButtons()
            }
        }

        //Make spinner write to model in order to persist selected item across lifecycles
        binding.choiceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //Do nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //Update position
                model.spinnerChoice = position
            }
        }
    }

    /**
     * Updates all views depending on model state
     */
    private fun updateViews() {
        updateDiceButtons()
        updateTextViews()
        updateThrowButton()
        updateSpinner()
    }

    /**
     * Updates dice buttons, changing images and enabling/disabling depending on model state
     */
    private fun updateDiceButtons() {
        diceButtons.forEachIndexed { index, element ->
            //Get Die object to access enabled property
            val die = model.getDie(index)

            //Update image
            element.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    model.getDieImage(die)
                )
            )
            element.isEnabled = die.enabled
        }
    }

    /**
     * Updates [TextView]s presenting current round and amount of rolls left
     */
    private fun updateTextViews() {
        binding.roundTextView.text = getString(R.string.round, model.round)
        binding.rollsLeftTextView.text = getString(R.string.rolls_left, model.rollsLeft)
    }

    /**
     * Updates [Spinner] depending on remaining available choices
     * and currently selected item
     */
    private fun updateSpinner() {
        if (model.round <= 10) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item, model.getScoreChoices()
            )
            binding.choiceSpinner.adapter = adapter
            binding.choiceSpinner.setSelection(model.spinnerChoice)
        }
    }

    /**
     * Updates whether throw button is used for throwing or calculating
     */
    private fun updateThrowButton() {
        if (model.rollsLeft == 0 || model.countEnabledDice() == 0) {
            binding.throwButton.setText(R.string.score_button)
            binding.throwButton.setOnClickListener {
                endRound()
                updateViews()
            }
        } else {
            binding.throwButton.setText(R.string.throw_button)
            binding.throwButton.setBackgroundResource(android.R.drawable.btn_default)
            binding.throwButton.setOnClickListener {
                model.rollDice()
                updateViews()
            }
        }
    }

    /**
     * Ends current round and dependent on game state, launches [Intent] for [ResultActivity]
     */
    private fun endRound() {
        model.noteScore(binding.choiceSpinner.selectedItem.toString())
        if (model.round < 10) {
            model.startNewRound()
        } else {
            binding.throwButton.isEnabled = false
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra(RESULTS_KEY, model.getResults())
            resultListener.launch(intent)
        }
    }

    /**
     * Saves model state when activity is placed in background
     */
    override fun onStop() {
        super.onStop()
        model.saveState()
    }

    companion object {
        /**
         * Bundle key used by [ResultActivity] and [MainActivity]
         */
        const val RESULTS_KEY = "se.umu.nien1121.results"
    }
}