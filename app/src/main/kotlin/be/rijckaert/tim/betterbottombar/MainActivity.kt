package be.rijckaert.tim.betterbottombar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import be.rijckaert.tim.library.BetterBottomBar

class MainActivity : AppCompatActivity() {
    private lateinit var betterBottomBar: BetterBottomBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        betterBottomBar = findViewById(R.id.bottom_navigation) as BetterBottomBar
        betterBottomBar.betterBottomBarClickListener = {
            //get state
        }
    }
}