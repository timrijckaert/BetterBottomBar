package be.rijckaert.tim.betterbottombar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import be.rijckaert.tim.library.BetterBottomBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BetterBottomBar betterBottomBar = (BetterBottomBar) findViewById(R.id.bottom_navigation);
        Log.d("Activity", "The selected tab is: " + betterBottomBar.getSelectedTab());
    }
}
