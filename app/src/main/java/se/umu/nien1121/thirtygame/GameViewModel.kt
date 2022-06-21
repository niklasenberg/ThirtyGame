package se.umu.nien1121.thirtygame

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

//SavedStateHandle keys
private const val ROUND_KEY = "se.umu.nien1121.round"
private const val ROLLS_LEFT_KEY = "se.umu.nien1121.rollsLeft"
private const val SPINNER_CHOICE_KEY = "se.umu.nien1121.spinnerChoice"
private const val DICE_KEY = "se.umu.nien1121.dice"
private const val SCOREBOARD_KEY = "se.umu.nien1121.scoreboard"

/**
 * Map for [Die] state --> Drawable Id, used by [GameViewModel.getDieImage]
 */
private val imageMap = mapOf(
    1 to R.drawable.red1,
    2 to R.drawable.red2,
    3 to R.drawable.red3,
    4 to R.drawable.red4,
    5 to R.drawable.red5,
    6 to R.drawable.red6,
    7 to R.drawable.white1,
    8 to R.drawable.white2,
    9 to R.drawable.white3,
    10 to R.drawable.white4,
    11 to R.drawable.white5,
    12 to R.drawable.white6,
    13 to R.drawable.grey1,
    14 to R.drawable.grey2,
    15 to R.drawable.grey3,
    16 to R.drawable.grey4,
    17 to R.drawable.grey5,
    18 to R.drawable.grey6
)

/**
 * [ViewModel] used by [MainActivity] to persist game state over lifecycles.
 * @param handle [SavedStateHandle] used to rebuild [GameViewModel] on destruction. Initialized in [saveState].
 */
class GameViewModel(private val handle: SavedStateHandle) : ViewModel() {
    /**
     * Collection of [Die] used to play game
     */
    private var dice: ArrayList<Die>

    /**
     * [Scoreboard] used to note and calculate score for game
     */
    private var scoreboard: Scoreboard

    //Game logic
    var round = 1
    var rollsLeft = 2
    var spinnerChoice = 0

    init {
        //Init for object, depending on null saved state
        round = handle.get<Int>(ROUND_KEY) ?: 1
        rollsLeft = handle.get<Int>(ROLLS_LEFT_KEY) ?: 2
        spinnerChoice = handle.get<Int>(SPINNER_CHOICE_KEY) ?: 0
        dice = handle.get<ArrayList<Die>>(DICE_KEY) ?: newDice()
        scoreboard = handle.get<Scoreboard>(SCOREBOARD_KEY) ?: Scoreboard(
            resultKeys = intArrayOf(), resultValues = intArrayOf(), scoreChoices = intArrayOf(
                LOW, 4, 5, 6, 7, 8, 9, 10, 11, 12
            )
        )
    }

    /**
     * Helper method for initializing new dice
     * @return an [ArrayList] containing six new [Die] objects
     */
    private fun newDice(): ArrayList<Die> {
        val newDice = arrayListOf<Die>()
        for (i in 1..6) {
            val die = Die(enabled = true, value = 1, selected = false, counted = false)
            newDice.add(die)
            die.roll() //Roll die on creation, to get random values
        }
        return newDice
    }

    /**
     * Resets [Scoreboard], [Die] objects and game logic
     */
    fun startNewGame() {
        scoreboard.reset()
        round = 1
        rollsLeft = 2
        spinnerChoice = 0
        dice.forEach { it.reset() }
    }

    /**
     * Increments [round] and resets [rollsLeft]. Also resets [Die] objects to obtain random values.
     */
    fun startNewRound() {
        round++
        rollsLeft = 2
        spinnerChoice = 0
        dice.forEach { it.reset() }
    }

    /**
     * Toggles [Die.selected] at given position
     * @param index the index of the die to be selected
     */
    fun selectDie(index: Int) {
        dice[index].select()
    }

    /**
     * Returns [Die] object at given index
     * @param index the index of the die to be returned
     */
    fun getDie(index: Int): Die {
        return dice[index]
    }

    /**
     * Rolls [Die] objects and decrements [rollsLeft]. If no rolls are left, disables dice.
     */
    fun rollDice() {
        dice.forEach { it.roll() }
        rollsLeft--

        if (rollsLeft == 0) {
            dice.forEach { it.enabled = false }
        }
    }

    /**
     * Retrieves count of currently enabled [Die]
     */
    fun countEnabledDice(): Int {
        return dice.filter { it.enabled }.size
    }

    /**
     * Returns the Id for the [Die] objects corresponding drawable, using [imageMap]
     * @param d die to be retrieved image id for
     * @return id of resource
     */
    fun getDieImage(d: Die): Int {
        return when {
            d.selected -> imageMap[d.value]!!
            d.enabled -> imageMap[d.value + 6]!!
            else -> imageMap[d.value + 12]!!
        }
    }

    /**
     * Updates and returns all remaining score choices for game
     * @return updated [ArrayList] of score choices
     */
    fun getScoreChoices(): ArrayList<String> {
        return scoreboard.updateScoreChoices()
    }

    /**
     * Notes score for current state of [dice]
     * @param scoreMode mode to use for scoring (i.e low, 4, 5)
     */
    fun noteScore(scoreMode: String) {
        scoreboard.calculateScore(dice, scoreMode)
    }

    /**
     * Gets list of Strings representing state of [scoreboard] on finished game.
     * @return list of results for each round played, including total score
     */
    fun getResults(): ArrayList<String> {
        val results = arrayListOf<String>()
        results.add(scoreboard.getTotalScore().toString() + " points.")
        results.addAll(scoreboard.getRoundScores())
        return results
    }

    /**
     * Sets [handle] properties to save state of [GameViewModel] between lifecycles.
     */
    fun saveState(){
        //Primitives
        handle.set(ROUND_KEY, round)
        handle.set(ROLLS_LEFT_KEY, rollsLeft)
        handle.set(SPINNER_CHOICE_KEY, spinnerChoice)
        //Parcelables
        handle.set(DICE_KEY, dice)
        handle.set(SCOREBOARD_KEY, scoreboard)
    }
}