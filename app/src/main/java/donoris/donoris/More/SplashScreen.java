package donoris.donoris.More;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import donoris.donoris.MainActivity;
import donoris.donoris.R;

public class SplashScreen extends Activity
{
    private static int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
