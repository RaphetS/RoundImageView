package org.raphets.roundimage;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.squareup.picasso.Picasso;
import org.raphets.roundimageview.RoundImageView;

public class MainActivity extends AppCompatActivity {

    private RoundImageView iv1,iv2,iv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv1= (RoundImageView) findViewById(R.id.iv1);
        iv2= (RoundImageView) findViewById(R.id.iv2);
        iv3= (RoundImageView) findViewById(R.id.iv3);

        iv2.setBorderColor(Color.YELLOW);

        Picasso.with(this)
                .load("http://ww2.sinaimg.cn/large/610dc034jw1fa42ktmjh4j20u011hn8g.jpg")
                .into(iv1);

        Picasso.with(this)
                .load("http://ww2.sinaimg.cn/large/610dc034jw1fa42ktmjh4j20u011hn8g.jpg")
                .into(iv3);

    }
}
