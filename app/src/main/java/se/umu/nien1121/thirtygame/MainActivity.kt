package se.umu.nien1121.thirtygame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.content.ContextCompat

//Bundle keys
private const val ROUND_KEY = "se.umu.nien1121.round"
private const val ROLLS_LEFT_KEY = "se.umu.nien1121.rollsLeft"
private const val DICE_KEY = "se.umu.nien1121.dice"
private const val SCOREBOARD_KEY = "se.umu.nien1121.scoreboard"

class MainActivity : AppCompatActivity() {

    //Widgets
    private lateinit var throwButton: Button
    private lateinit var choiceSpinner: Spinner
    private lateinit var diceButtons: ArrayList<ImageButton>
    private lateinit var roundTextView: TextView
    private lateinit var rollsLeftTextView: TextView

    //Models and logic
    private lateinit var dice: ArrayList<Die>
    private lateinit var scoreboard: Scoreboard
    private var round = 1
    private var rollsLeft = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            round = savedInstanceState.getInt(ROUND_KEY)
            rollsLeft = savedInstanceState.getInt(ROLLS_LEFT_KEY)
            dice = savedInstanceState.getParcelableArrayList(DICE_KEY)!!
            scoreboard = savedInstanceState.getParcelable(SCOREBOARD_KEY)!!
        } else {
            dice = ArrayList()
            for (i in 1..6) {
                val die = Die(rollable = true, value = 1, selected = false, counted = false)
                dice.add(die)
                die.roll()
            }
            scoreboard = Scoreboard()
        }

        findViews()
        setListeners()
        updateViews()
    }

    private fun findViews() {
        throwButton = findViewById(R.id.throw_button)
        choiceSpinner = findViewById(R.id.choice_spinner)

        diceButtons = arrayListOf(
            findViewById(R.id.die_one), findViewById(R.id.die_two), findViewById(R.id.die_three),
            findViewById(R.id.die_four), findViewById(R.id.die_five), findViewById(R.id.die_six)
        )

        roundTextView = findViewById(R.id.round_text_view)
        rollsLeftTextView = findViewById(R.id.rolls_left_text_view)
    }

    private fun setListeners() {
        for (dieButton in diceButtons) {
            dieButton.setOnClickListener {
                dice[diceButtons.indexOf(dieButton)].select()
                updateViews()
            }
        }
    }

    private fun updateViews() {
        updateDiceButtons()
        updateTextViews()
        updateThrowButton()
        updateSpinner()
    }

    private fun updateSpinner() {
        if (round <= 10) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item, scoreboard.getScoreChoices()
            )
            choiceSpinner.adapter = adapter
        }
    }

    private fun updateThrowButton() {
        if (rollsLeft == 0 || getActiveDice() == 0) {
            throwButton.setText(R.string.score_button)
            throwButton.setOnClickListener { getScore() }
        } else {
            throwButton.setText(R.string.throw_button)
            throwButton.setOnClickListener {
                rollDice()
            }
        }
    }

    private fun getActiveDice(): Int {
        var sum = 0
        for (die in dice) {
            if (die.rollable) {
                sum++
            }
        }
        return sum
    }

    private fun updateDiceButtons() {
        if (rollsLeft == 0) {
            for (die in dice) {
                die.rollable = false
            }
        }

        for (die in dice) {
            diceButtons[dice.indexOf(die)].setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    die.getImageResId()
                )
            )
            diceButtons[dice.indexOf(die)].isEnabled = die.rollable
        }
    }

    private fun updateTextViews() {
        roundTextView.text = getString(R.string.round, round)
        rollsLeftTextView.text = getString(R.string.rolls_left, rollsLeft)
    }

    private fun getScore() {
        scoreboard.calculateScore(dice, choiceSpinner.selectedItem.toString())

        if (round < 10) {
            startNewRound()
        } else {
            //TODO: Intent to resultactivity
            startNewGame()
        }
    }

    private fun startNewGame() {
        scoreboard.reset()
        round = 1
        rollsLeft = 3
        for (die in dice) {
            die.reset()
        }
        updateViews()
    }

    private fun startNewRound() {
        round++
        rollsLeft = 3
        for (die in dice) {
            die.reset()
        }
        updateViews()
    }

    private fun rollDice() {
        for (die in dice) {
            die.roll()
        }
        rollsLeft--
        updateViews()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ROUND_KEY, round)
        outState.putInt(ROLLS_LEFT_KEY, rollsLeft)
        outState.putParcelableArrayList(DICE_KEY, dice)
        outState.putParcelable(SCOREBOARD_KEY, scoreboard)
    }
}