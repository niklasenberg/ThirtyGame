package se.umu.nien1121.thirtygame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner

class MainActivity : AppCompatActivity() {

    private lateinit var throwButton: Button
    private lateinit var choiceSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        throwButton = findViewById(R.id.throw_button)
        choiceSpinner = findViewById(R.id.choice_spinner)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, resources.getStringArray(R.array.score_choices)
        )
        choiceSpinner.adapter = adapter
    }
}