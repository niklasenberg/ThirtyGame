package se.umu.nien1121.thirtygame

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

private const val LOW = 3

class Scoreboard : Parcelable {
    private var result = mutableMapOf<Int, Int>()
    var availableScoreChoices: ArrayList<Int> = ArrayList()

    fun calculateScore(dice: ArrayList<Die>, scoreString: String) {
        val scoreMode: Int = if (scoreString == "Low") {
            LOW
        } else {
            scoreString.toInt()
        }

        availableScoreChoices.remove(scoreMode)
        val diceCopy = ArrayList<Die>(dice)
        result[scoreMode] = 0

        if (scoreMode == LOW) {
            for (die in diceCopy) {
                if (die.value <= LOW) {
                    result[scoreMode] = result[scoreMode]!! + die.value
                }
            }
        } else {
            diceCopy.sortByDescending { it.value }
            findSum(diceCopy, scoreMode, 0)
        }
    }

    private fun findSum(dice: ArrayList<Die>, scoreMode: Int, sum: Int) {
        if (sum == scoreMode) {
            result[scoreMode] = result[scoreMode]!! + sum
            return
        }

        for (die in dice) {
            if (!die.counted && sum + die.value <= scoreMode) {
                die.counted = true
                findSum(dice, scoreMode, sum + die.value)
            }
        }

        return
    }

    fun reset() {
        Log.d("RESULT", result.toString())
        result.clear()
        availableScoreChoices.clear()
        for (i in LOW..12) {
            availableScoreChoices.add(i)
        }
    }

    init {
        reset()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    fun getScoreChoices(): ArrayList<String> {
        val choiceStrings = ArrayList<String>()
        for (i in availableScoreChoices) {
            if (i == 3) {
                choiceStrings.add("Low")
            } else {
                choiceStrings.add(i.toString())
            }
        }
        return choiceStrings
    }

    companion object CREATOR : Parcelable.Creator<Scoreboard> {
        override fun createFromParcel(parcel: Parcel): Scoreboard {
            return Scoreboard()
        }

        override fun newArray(size: Int): Array<Scoreboard?> {
            return arrayOfNulls(size)
        }
    }
}