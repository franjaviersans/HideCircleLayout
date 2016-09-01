package fran.com.autohide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        HideLayout AutoHideLayout = (HideLayout) findViewById(R.id.tagLayout);
        CircularLayout CirLay = (CircularLayout) findViewById(R.id.circularLayout);


        LayoutInflater layoutInflater = getLayoutInflater();

        String tag;
        for (int i = 0; i <= 20; i++) {
            tag = "#Button" + i;
            View tagView = layoutInflater.inflate(R.layout.imageview_layout, null, false);

            Button B = (Button) tagView.findViewById(R.id.hideButton);

            B.setText(tag);
            B.setAlpha(0.5f);
            B.setEnabled(false);
            B.setClickable(false);

            CirLay.addView(tagView);
        }


    }
}
