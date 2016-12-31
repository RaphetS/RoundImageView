# RoundImageView
- support rounded corners,circle,oval（支持圆角图片、圆形图片、椭圆图片）
- support border（支持描边，可定制描边颜色、描边宽度）
- supports different radii on each corner（支持定制圆角图片各个圆角的半径）

## ScreenShot
![image](https://github.com/RaphetS/RoundImageView/blob/master/sample/src/main/res/drawable-xhdpi/screenshot.png)

## Gradle

```
dependencies {
    compile 'org.raphets:roundimageview:1.2.0'
}
```

## Usage

Define in xml:

```
<org.raphets.roundimageview.RoundImageView
            android:layout_width="180dp"
            android:layout_height="180dp"
            android:src="@drawable/a"
            app:border_width="3dp"
            app:border_color="@android:color/holo_red_dark"
            app:leftBottom_corner_radius="0dp"
            app:leftTop_corner_radius="30dp"
            app:rightBottom_corner_radius="30dp"
            app:rightTop_corner_radius="0dp"
            app:type="round" />
```

Or in code:

```
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
```
