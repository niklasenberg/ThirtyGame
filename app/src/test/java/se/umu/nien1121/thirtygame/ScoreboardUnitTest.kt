package se.umu.nien1121.thirtygame

import org.junit.Test

import org.junit.Assert.*

class ScoreboardUnitTest {

    @Test
    fun calculateScore_is_correct(){
        val scoreboard = Scoreboard(intArrayOf(), intArrayOf(), intArrayOf(
            LOW, 4, 5, 6, 7, 8, 9, 10, 11, 12
        ))
        val dice = arrayListOf<Die>()

        //1, 1, 1, 2, 4 and 4, scoreMode 5
        dice.add(Die(enabled = true, 1, selected = false, counted = false))
        dice.add(Die(true, 1, selected = false, counted = false))
        dice.add(Die(true, 1, selected = false, counted = false))
        dice.add(Die(true, 2, selected = false, counted = false))
        dice.add(Die(true, 4, selected = false, counted = false))
        dice.add(Die(true, 4, selected = false, counted = false))
        scoreboard.calculateScore(dice, "5")
        assertEquals(10, scoreboard.getTotalScore())
        scoreboard.reset()
        dice.forEach{it.reset()}

        //1,2,3,4,5,6, scoreMode LOW
        dice[0].value = 1
        dice[1].value = 2
        dice[2].value = 3
        dice[3].value = 4
        dice[4].value = 5
        dice[5].value = 6
        scoreboard.calculateScore(dice, "Low")
        assertEquals(6, scoreboard.getTotalScore())
        scoreboard.reset()
        dice.forEach{it.reset()}

        //3,2,4,4,4,3, scoreMode 8
        dice[0].value = 3
        dice[1].value = 2
        dice[2].value = 4
        dice[3].value = 4
        dice[4].value = 4
        dice[5].value = 3
        scoreboard.calculateScore(dice, "8")
        assertEquals(8, scoreboard.getTotalScore())
        scoreboard.reset()
        dice.forEach{it.reset()}

        //5,2,3,1,2,3, scoreMode 5
        dice[0].value = 5
        dice[1].value = 2
        dice[2].value = 3
        dice[3].value = 1
        dice[4].value = 2
        dice[5].value = 3
        scoreboard.calculateScore(dice, "5")
        assertEquals(15, scoreboard.getTotalScore())
        scoreboard.reset()
        dice.forEach{it.reset()}

        //6,4,5,5,6,6, scoreMode 12
        dice[0].value = 5
        dice[1].value = 2
        dice[2].value = 3
        dice[3].value = 1
        dice[4].value = 2
        dice[5].value = 3
        scoreboard.calculateScore(dice, "12")
        assertEquals(12, scoreboard.getTotalScore())
        scoreboard.reset()
        dice.forEach{it.reset()}
    }
}