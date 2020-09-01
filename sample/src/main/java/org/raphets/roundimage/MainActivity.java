package org.raphets.roundimage;

import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.squareup.picasso.Picasso;
import org.raphets.roundimageview.RoundImageView;

public class MainActivity extends AppCompatActivity {

    private RoundImageView iv1,iv2,iv3,iv4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RoundImageView iv1= (RoundImageView) findViewById(R.id.iv1);

        iv1.setBorderWidth(3)
                .setBorderColor(Color.RED)
                .setType(RoundImageView.TYPE_ROUND)
                .setLeftTopCornerRadius(0)
                .setRightTopCornerRadius(10)
                .setRightBottomCornerRadius(30)
                .setLeftBottomCornerRadius(50);

        Picasso.with(this)
                .load("http://ww2.sinaimg.cn/large/610dc034jw1fa42ktmjh4j20u011hn8g.jpg")
                .into(iv1);


    }
}
