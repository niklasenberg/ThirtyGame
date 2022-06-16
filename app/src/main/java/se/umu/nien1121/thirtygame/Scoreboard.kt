package se.umu.nien1121.thirtygame

import android.os.Parcel
import android.os.Parcelable

/**
 * Public constant for "Low" values, used by [Scoreboard] and [GameViewModel]
 */
const val LOW = 3

/**
 * Class representing scoreboard for game application. Keeps track of scores for each round. Implements [Parcelable] interface.
 * @param resultKeys initial rounds that already have been noted score for.
 * @param resultValues initial scores that already have been played a round for.
 * @param scoreChoices remaining options to be used for score calculation by the user.
 */
class Scoreboard(var resultKeys: IntArray, var resultValues: IntArray, var scoreChoices: IntArray) :
    Parcelable {

    /**
     * Map of round number --> score
     */
    private var result: MutableMap<Int, Int> = mutableMapOf<Int, Int>().apply {
        for (i in resultKeys.indices) this[resultKeys[i]] = resultValues[i]
    }

    /**
     * Remaining options to be used for score calculation by the user.
     */
    private var availableScoreChoices: ArrayList<Int> = scoreChoices.toCollection(ArrayList())

    /**
     * Secondary constructor for parcelled objects, enables transfer between lifecycles.
     * @param parcel constructed parcel of prior die object, initialized in [writeToParcel]
     */
    constructor(parcel: Parcel) : this(
        parcel.createIntArray() ?: intArrayOf(),
        parcel.createIntArray() ?: intArrayOf(),
        parcel.createIntArray() ?: intArrayOf()
    )

    /**
     * Resets [result] and [availableScoreChoices] to initial state
     */
    fun reset() {
        result.clear()
        availableScoreChoices.clear()
        availableScoreChoices.addAll(LOW..12)
    }

    /**
     * Updates available score choices for the user
     * @return [ArrayList] of legible score choices that have not already been chosen by user.
     */
    fun updateScoreChoices(): ArrayList<String> {
        val choiceStrings = ArrayList<String>()
        availableScoreChoices.forEach {
            if (it == LOW) {
                choiceStrings.add("Low")
            } else {
                choiceStrings.add(it.toString())
            }
        }
        return choiceStrings
    }

    /**
     * Given a collection of dice and a target score to grade these dice against, notes score
     * dependent on board state. Uses [findSum] for score calculation.
     * @param dice collection of [Die] to be calculated score for
     * @param scoreString target score chosen by user
     */
    fun calculateScore(dice: ArrayList<Die>, scoreString: String) {
        //Convert scoreString to number equivalent
        val scoreChoice: Int = if (scoreString == "Low") {
            LOW
        } else {
            scoreString.toInt()
        }

        //Copy dice array to enable sorting
        val diceCopy = ArrayList<Die>(dice)
        //Remove current choice from available choices and init map entry for this score
        availableScoreChoices.remove(scoreChoice)
        result[scoreChoice] = 0

        if (scoreChoice == LOW) {
            //Not the sum of all dice with a value less or equal to 3 (LOW)
            result[scoreChoice] =
                result[scoreChoice]!! + diceCopy.filter { it.value <= LOW }.sumOf { it.value }
        } else {
            //Sort dice collection and use recursive helper method to note score
            diceCopy.sortByDescending { it.value }
            findSum(diceCopy, arrayListOf(), scoreChoice)
        }
    }

    /**
     * Recursive helper method used to calculate score when target score is above [LOW].
     * Finds smallest subset of dice ([heldDice]) with given sum ([scoreChoice]).
     * @param dice All [Die] objects to be traversed.
     * MUST be descending sorted, to ensure smallest subset in calculation.
     * @param heldDice [Die] objects that qualify for evaluation
     * , i.e have a possibility of reaching exact target sum [scoreChoice].
     * @param scoreChoice target score to calculate score for.
     */
    private fun findSum(dice: ArrayList<Die>, heldDice: ArrayList<Die>, scoreChoice: Int) {
        //Get current sum of held dice
        val currentSum = heldDice.sumOf { it.value }

        when {
            currentSum == scoreChoice -> {
                //Target sum reached, note result and mark heldDice as counted.
                result[scoreChoice] = result[scoreChoice]!! + currentSum
                heldDice.forEach { it.counted = true }

                //Release all held dice and return
                heldDice.clear()
                return
            }
            currentSum < scoreChoice -> {
                dice.forEach {
                    //Find die that is not held, not already counted and when added with currentSum,
                    // does not exceed the given target sum (scoreChoice).
                    if (!heldDice.contains(it) && !it.counted && currentSum + it.value <= scoreChoice) {
                        //"Hold" this die and call method recursively
                        heldDice.add(it)
                        findSum(dice, heldDice, scoreChoice)
                    }
                }
            }
            else -> return
        }
    }

    /**
     * Gets total score of all played rounds
     * @return total score for current state of game
     */
    fun getTotalScore(): Int {
        var sum = 0
        for (i in result.values) {
            sum += i
        }
        return sum
    }

    /**
     * Gets list of scores for each round.
     * @return [ArrayList] of strings with score choice and correlating score
     */
    fun getRoundScores(): ArrayList<String> {
        val roundScores = arrayListOf<String>()
        result.keys.sorted().forEach {
            when (it) {
                LOW -> roundScores.add("Low: " + result[it] + " points.")
                else -> roundScores.add(it.toString() + ": " + result[it] + " points.")
            }
        }
        return roundScores
    }

    /**
     * Writes properties to [Parcel] for current object.
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeIntArray(result.keys.toIntArray())
        parcel.writeIntArray(result.values.toIntArray())
        parcel.writeIntArray(availableScoreChoices.toIntArray())
    }

    /**
     * Helper method required by [Parcelable], not used.
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Companion object required by [Parcelable]
     */
    companion object CREATOR : Parcelable.Creator<Scoreboard> {
        /**
         * Creates single object from parcel, by use of secondary constructor.
         */
        override fun createFromParcel(parcel: Parcel): Scoreboard {
            return Scoreboard(parcel)
        }

        /**
         * Helper method for creation of objects for multiple parcels, not used.
         */
        override fun newArray(size: Int): Array<Scoreboard?> {
            return arrayOfNulls(size)
        }
    }
}