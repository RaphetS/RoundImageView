package org.raphets.roundimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

/**
 * 圆形、圆角图片
 */

public class RoundImageView extends ImageView {
    /**
     * 图片的类型，圆形or圆角
     */
    private int type;
    public static final int TYPE_CIRCLE = 0;
    public static final int TYPE_ROUND = 1;
    public static final int TYPE_OVAL = 2;
    /**
     * 描边的颜色、宽度
     */
    private int mBorderColor;
    private float mBorderWidth;
    /**
     * 圆角的大小
     */
    private float mCornerRadius;
    //左上角圆角大小
    private float mLeftTopCornerRadius;
    //右上角圆角大小
    private float mRightTopCornerRadius;
    //左下角圆角大小
    private float mLeftBottomCornerRadius;
    //右下角圆角大小
    private float mRightBottomCornerRadius;

    /**
     * 绘图的Paint
     */
    private Paint mBitmapPaint;
    private Paint mBorderPaint;
    /**
     * 圆角的半径
     */
    private float mRadius;
    /**
     * 3x3 矩阵，主要用于缩小放大
     */
    private Matrix mMatrix;
    /**
     * 渲染图像，使用图像为绘制图形着色
     */
    private BitmapShader mBitmapShader;
    /**
     * view的宽度
     */
    private int mWidth;
    /**
     * 圆角图片区域
     */
    private RectF mRoundRect;

    private Path mRoundPath;

    private boolean useUnitDip = true;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyleAttr, 0);

        type = a.getInt(R.styleable.RoundImageView_type, TYPE_OVAL);
        mBorderColor = a.getColor(R.styleable.RoundImageView_border_color, Color.WHITE);
        mBorderWidth = a.getDimension(R.styleable.RoundImageView_border_width, 0);
        mCornerRadius = a.getDimension(R.styleable.RoundImageView_corner_radius, dp2px(10));
        mLeftTopCornerRadius = a.getDimension(R.styleable.RoundImageView_leftTop_corner_radius, 0);
        mLeftBottomCornerRadius = a.getDimension(R.styleable.RoundImageView_leftBottom_corner_radius, 0);
        mRightTopCornerRadius = a.getDimension(R.styleable.RoundImageView_rightTop_corner_radius, 0);
        mRightBottomCornerRadius = a.getDimension(R.styleable.RoundImageView_rightBottom_corner_radius, 0);

        a.recycle();

        init();
    }

    private void init() {
        mRoundPath = new Path();
        mRoundRect = new RectF();
        mMatrix = new Matrix();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);

        updateStrokePaint();
    }

    private void updateStrokePaint() {
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /**
         * 如果类型是圆形，则强制改变view的宽高一致，以小值为准
         */
        if (type == TYPE_CIRCLE) {
            mWidth = Math.min(MeasureSpec.getSize(widthMeasureSpec),
                    MeasureSpec.getSize(heightMeasureSpec));
            mRadius = mWidth / 2 - mBorderWidth / 2;
            setMeasuredDimension(mWidth, mWidth);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 圆角图片的范围
        if (type == TYPE_ROUND || type == TYPE_OVAL) {
            mRoundRect.set(mBorderWidth / 2, mBorderWidth / 2, w - mBorderWidth / 2, h - mBorderWidth / 2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        setupShader();

        if (type == TYPE_ROUND) {
            setRoundPath();

            canvas.drawPath(mRoundPath, mBitmapPaint);

            //绘制描边
            if (mBorderWidth > 0) {
                canvas.drawPath(mRoundPath, mBorderPaint);
            }
        } else if (type == TYPE_CIRCLE) {
            canvas.drawCircle(mRadius + mBorderWidth / 2, mRadius + mBorderWidth / 2, mRadius, mBitmapPaint);

            //绘制描边
            if (mBorderWidth > 0) {
                canvas.drawCircle(mRadius + mBorderWidth / 2, mRadius + mBorderWidth / 2, mRadius, mBorderPaint);
            }
        } else {
            canvas.drawOval(mRoundRect, mBitmapPaint);

            //绘制描边
            if (mBorderWidth > 0) {
                canvas.drawOval(mRoundRect, mBorderPaint);
            }
        }
    }


    private void setRoundPath() {
        mRoundPath.reset();
        /**
         * 如果四个圆角大小都是默认值0，
         * 则将四个圆角大小设置为mCornerRadius的值
         */
        if (mLeftTopCornerRadius == 0 &&
                mLeftBottomCornerRadius == 0 &&
                mRightTopCornerRadius == 0 &&
                mRightBottomCornerRadius == 0) {

            mRoundPath.addRoundRect(mRoundRect,
                    new float[]{mCornerRadius, mCornerRadius,
                            mCornerRadius, mCornerRadius,
                            mCornerRadius, mCornerRadius,
                            mCornerRadius, mCornerRadius},
                    Path.Direction.CW);

        } else {
            mRoundPath.addRoundRect(mRoundRect,
                    new float[]{mLeftTopCornerRadius, mLeftTopCornerRadius,
                            mRightTopCornerRadius, mRightTopCornerRadius,
                            mRightBottomCornerRadius, mRightBottomCornerRadius,
                            mLeftBottomCornerRadius, mLeftBottomCornerRadius},
                    Path.Direction.CW);
        }
    }


    /**
     * 初始化BitmapShader
     */
    private void setupShader() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        Bitmap bmp = drawableToBitmap(drawable);
        // 将bmp作为着色器，就是在指定区域内绘制bmp
        mBitmapShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mMatrix.setTranslate(0, 0);
        float scale = 1.0f;
        if (type == TYPE_CIRCLE) {
            if (bmp.getWidth() != getWidth() || bmp.getHeight() != getHeight()) {
                // 拿到bitmap宽或高的小值
                int bSize = Math.min(bmp.getWidth(), bmp.getHeight());
                scale = mWidth * 1.0f / bSize;
                //使缩放后的图片居中
                float dx = (bmp.getWidth() * scale - mWidth) / 2;
                float dy = (bmp.getHeight() * scale - mWidth) / 2;
                mMatrix.setTranslate(-dx, -dy);
            }
        } else if (type == TYPE_ROUND || type == TYPE_OVAL) {
            if (bmp.getWidth() != getWidth() || bmp.getHeight() != getHeight()) {
                // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
                scale = Math.max(getWidth() * 1.0f / bmp.getWidth(),
                        getHeight() * 1.0f / bmp.getHeight());
                //使缩放后的图片居中
                float dx = (scale * bmp.getWidth() - getWidth()) / 2;
                float dy = (scale * bmp.getHeight() - getHeight()) / 2;
                mMatrix.setTranslate(-dx, -dy);
            }
        }
        // shader的变换矩阵，我们这里主要用于放大或者缩小
        mMatrix.preScale(scale, scale);
        // 设置变换矩阵
        mBitmapShader.setLocalMatrix(mMatrix);
        // 设置shader
        mBitmapPaint.setShader(mBitmapShader);
    }


    /**
     * drawable转bitmap
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }


    /**
     * 设置图片类型:
     * imageType=0 圆形图片
     * imageType=1 圆角图片
     * 默认为圆形图片
     */
    public RoundImageView setType(int imageType) {
        if (this.type != imageType) {
            this.type = imageType;
            if (this.type != TYPE_ROUND && this.type != TYPE_CIRCLE && this.type != TYPE_OVAL) {
                this.type = TYPE_OVAL;
            }
            requestLayout();
        }
        return this;
    }


    /**
     * 设置圆角图片的圆角大小
     */
    public RoundImageView setCornerRadius(int cornerRadius) {
        cornerRadius = dp2px(cornerRadius);
        if (mCornerRadius != cornerRadius) {
            mCornerRadius = cornerRadius;
            invalidate();
        }
        return this;
    }

    /**
     * 设置圆角图片的左上圆角大小
     */
    public RoundImageView setLeftTopCornerRadius(int cornerRadius) {
        cornerRadius = dp2px(cornerRadius);
        if (mLeftTopCornerRadius != cornerRadius) {
            mLeftTopCornerRadius = cornerRadius;
            invalidate();
        }
        return this;
    }

    /**
     * 设置圆角图片的右上圆角大小
     */
    public RoundImageView setRightTopCornerRadius(int cornerRadius) {
        cornerRadius = dp2px(cornerRadius);
        if (mRightTopCornerRadius != cornerRadius) {
            mRightTopCornerRadius = cornerRadius;
            invalidate();
        }
        return this;
    }

    /**
     * 设置圆角图片的左下圆角大小
     */
    public RoundImageView setLeftBottomCornerRadius(int cornerRadius) {
        cornerRadius = dp2px(cornerRadius);
        if (mLeftBottomCornerRadius != cornerRadius) {
            mLeftBottomCornerRadius = cornerRadius;
            invalidate();
        }
        return this;
    }

    /**
     * 设置圆角图片的右下圆角大小
     */
    public RoundImageView setRightBottomCornerRadius(int cornerRadius) {
        cornerRadius = dp2px(cornerRadius);
        if (mRightBottomCornerRadius != cornerRadius) {
            mRightBottomCornerRadius = cornerRadius;
            invalidate();
        }

        return this;
    }


    /**
     * 设置描边宽度
     */
    public RoundImageView setBorderWidth(int borderWidth) {
        borderWidth = dp2px(borderWidth);
        if (mBorderWidth != borderWidth) {
            mBorderWidth = borderWidth;
            updateStrokePaint();
            invalidate();
        }

        return this;
    }

    /**
     * 设置描边颜色
     */
    public RoundImageView setBorderColor(int borderColor) {
        if (mBorderColor != borderColor) {
            mBorderColor = borderColor;
            updateStrokePaint();
            invalidate();
        }

        return this;
    }

    private int dp2px(int dpVal) {
        if (!useUnitDip) {
            return dpVal;
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    public boolean isUseUnitDip() {
        return useUnitDip;
    }

    public void setUseUnitDip(boolean useUnitDip) {
        this.useUnitDip = useUnitDip;
    }
}
