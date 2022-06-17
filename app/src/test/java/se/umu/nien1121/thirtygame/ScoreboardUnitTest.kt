package se.umu.nien1121.thirtygame

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class ScoreboardUnitTest {

    private val scoreboard: Scoreboard = Scoreboard(intArrayOf(), intArrayOf(), intArrayOf(
        LOW, 4, 5, 6, 7, 8, 9, 10, 11, 12
    ))

    private val dice = arrayListOf(Die(enabled = true, 1, selected = false, counted = false),
        Die(enabled = true, 1, selected = false, counted = false),
        Die(enabled = true, 1, selected = false, counted = false),
        Die(enabled = true, 1, selected = false, counted = false),
        Die(enabled = true, 1, selected = false, counted = false),
        Die(enabled = true, 1, selected = false, counted = false))

    @Before
    fun setUp(){
        scoreboard.reset()
        dice.forEach{it.reset()}
    }

    @Test
    fun calculateScore(){
        //1, 1, 1, 2, 4 and 4, scoreMode 5
        dice[0].value = 1
        dice[1].value = 1
        dice[2].value = 1
        dice[3].value = 2
        dice[4].value = 4
        dice[5].value = 4
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
    }

    @Test
    fun calculateScore_rounds(){
        //6,4,3,3,4,1, all scoring modes/rounds
        dice[0].value = 6
        dice[1].value = 4
        dice[2].value = 3
        dice[3].value = 3
        dice[4].value = 4
        dice[5].value = 1

        scoreboard.calculateScore(dice, "Low")
        dice.forEach{it.counted = false}

        (4..12).forEach{ value ->
            scoreboard.calculateScore(dice, value.toString())
            dice.forEach{it.counted = false}
        }

        val scores = scoreboard.getRoundScores()

        assertEquals(126, scoreboard.getTotalScore())
        assertEquals("Low: 7 points.", scores[0])
        assertEquals("4: 12 points.", scores[1] )
        assertEquals("5: 5 points.", scores[2])
        assertEquals("6: 12 points.", scores[3])
        assertEquals("7: 21 points.", scores[4])
        assertEquals("8: 8 points.", scores[5])
        assertEquals("9: 18 points.", scores[6])
        assertEquals("10: 20 points.", scores[7])
        assertEquals("11: 11 points.", scores[8])
        assertEquals("12: 12 points.", scores[9])
    }
}