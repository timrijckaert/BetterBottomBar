package be.rijckaert.tim.betterbottombar;

import android.os.Bundle;
import android.support.design.internal.BottomNavigationItemView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import be.rijckaert.tim.library.BetterBottomBar;

public class MainActivity extends AppCompatActivity {

    private BetterBottomBar betterBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        betterBottomBar = (BetterBottomBar) findViewById(R.id.bottom_navigation);
        betterBottomBar.setBetterBottomBarClickListener(new BetterBottomBar.BetterBottomBarClickListener() {
            @Override
            public void tabClicked(@NotNull final BottomNavigationItemView btmNavItem) {
                tellAboutSelectedTab();
            }
        });

        tellAboutSelectedTab();
    }

    private int tellAboutSelectedTab() {
        return Log.d("Activity", "The selected tab is: " + betterBottomBar.getSelectedTab());
    }
}