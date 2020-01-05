package com.uom.happycelebrate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run()
            {

                Intent intent = new Intent(MainActivity.this, CardsListPage.class);
                startActivity(intent);
                MainActivity.this.finish();

            }
        };

        handler.postDelayed(r, 5000);



    }
}
