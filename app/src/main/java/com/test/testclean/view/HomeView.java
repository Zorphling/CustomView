package com.test.testclean.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.test.testclean.R;

import java.text.DecimalFormat;


/**
 * 性能？
 */
public class HomeView extends View {
    private PointF mCenter;
    private int mWidth, mHeight;
    private Region mViewRegion;
    private Paint patternPaint, shaderPaint, textPaint;
    private ValueAnimator valueAnimator;
    private static DecimalFormat mDecimalFormat = new DecimalFormat();
    private int[] cleanedColors = new int[]{0x2661FEAE, 0x2617D5FE};
    private int[] cleanedEndColors = new int[]{0x4061FEAE, 0x4017D5FE};
    private int ringOneColor = 0x33FFFFFF;
    private int ringTwoColor = 0x33FFFFFF;
    private SparseArray<LinearGradient> gradientSparseArray = new SparseArray<>();
    private Point redPoint, redPoint2;
    //状态相关数据
    private boolean isCleaned = false;

    private float circleRadius;
    //三个半径变量
    private float contentCircleRadius, tmpContentCircleRadius, ringStart, ringBond;

    //三个圆弧的矩形位置,中上下
    private float lineOneRadius;
    private RectF lineRectOne, lineRectTwo, lineRectThree;

    //旋转度数
    private float rotate;
    //水波等动画进度
    private float animationProgress;

    private LinearGradient contentGradient;

    //String
    private float size;
    private String strJunkCleaner = "Junk Cleaner";
    private String strTapToClean = "Tap to clean";
    ;
    private String strCleaned = "Cleaned";
    private String strClean = "Clean";
    private String strGreat = "Great";
    private String strUsage = "unknown";
    private String[] sizeStr = new String[]{"0.00", "KB"};

    private Bitmap bitmap;
    private Rect srcRect;
    private Rect dstRect;

    public HomeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        patternPaint = new Paint();
        patternPaint.setAntiAlias(true);
        patternPaint.setDither(true);
        patternPaint.setFilterBitmap(true);

        shaderPaint = new Paint(patternPaint);
        textPaint = new Paint(patternPaint);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.home_view_cleaned);
        srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                rotate = value * 360;
                if (value > 0.2 && value < 0.9) {
                    animationProgress = (value - 0.2f) / 0.7f;
                } else {
                    animationProgress = -1;
                }
                invalidate();
            }
        });
        valueAnimator.setDuration(2500);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCircle(canvas);
        drawText(canvas);
        drawCircleLines(canvas);
        drawAnimation(canvas);
    }


    private void drawCircle(Canvas canvas) {
        // 中心空白区
        canvas.drawCircle(mCenter.x, mCenter.y, circleRadius, getPatternPaint(0, 0xFFFFFFFF, true));
        // 内容区域
        canvas.drawCircle(mCenter.x, mCenter.y, tmpContentCircleRadius, getContentBgPaint());
    }

    private void drawText(Canvas canvas) {
        int color = isCleaned ? 0xFF004FFF : 0xFFFF2000;

        if (isCleaned) {
            float y = mCenter.y * 0.9f;
            canvas.drawText(strCleaned, mCenter.x, y, getTextPaint(Paint.Align.CENTER, dp2px(32), color));
            y += dp2px(21);
            canvas.drawText(strUsage, mCenter.x, y, getTextPaint(Paint.Align.CENTER, dp2px(12), 0xFF9F9F9F));
            canvas.drawBitmap(bitmap, srcRect, dstRect, patternPaint);
        } else {
            float y = mCenter.y;
            canvas.drawText(strClean, mCenter.x, y, getTextPaint(Paint.Align.CENTER, dp2px(32), color));
            y += dp2px(30);
            canvas.drawText(strUsage, mCenter.x, y, getTextPaint(Paint.Align.CENTER, dp2px(12), 0xFF9F9F9F));
        }

    }

    float step1 = 0.3f;
    float step2 = 0.7f;

    private void drawAnimation(Canvas canvas) {
        if (animationProgress == -1) return;
        // 透明度的变化
        if (animationProgress < step1) {
            // 一段水波出现
            float progress = (animationProgress / step1);
            float ringOneStroke = ringBond * progress;
            float ringOneRadius = ringStart + ringOneStroke / 2;
            if (isCleaned) {
                canvas.drawCircle(mCenter.x, mCenter.y, ringOneRadius, getRingShaderPaint(ringOneRadius, 76, ringOneStroke));
            } else {
                canvas.drawCircle(mCenter.x, mCenter.y, ringOneRadius, getPatternPaint(ringOneStroke, ringOneColor, false));
            }
        } else if (animationProgress < step2) {
            // 二段水波出现，一段水波消失
            float progress = ((animationProgress - step1) / (step2 - step1));
            float ringTwoStroke = ringBond * progress;
            float ringTwoRadius = ringStart + ringTwoStroke / 2;

            float alphaProgress = progress > 0.6f ? 1 - (progress - 0.6f) / (1 - 0.6f) : 1;
            int alpha = (int) (26 * alphaProgress);

            if (isCleaned) {
                alpha = (int) (76 * alphaProgress);
                canvas.drawCircle(mCenter.x, mCenter.y, ringStart + ringBond / 2,
                        getRingShaderPaint(ringStart + ringBond / 2, alpha, ringBond));
                canvas.drawCircle(mCenter.x, mCenter.y, ringTwoRadius, getRingShaderPaint(ringTwoRadius, 76, ringTwoStroke));
            } else {
                canvas.drawCircle(mCenter.x, mCenter.y, ringStart + ringBond / 2,
                        getPatternPaint(ringBond, ringOneColor, alpha, false));
                canvas.drawCircle(mCenter.x, mCenter.y, ringTwoRadius, getPatternPaint(ringTwoStroke, ringTwoColor, false));
            }
        } else {
            // 中心区域动画
            float progress = (animationProgress - step2) / (1 - step2);
            // 环二消失
            float alphaProgress = progress < 0.6 ? 1 - progress / 0.6f : 0;
            int alpha = (int) (40 * alphaProgress);

            if (isCleaned) {
                alpha = (int) (76 * alphaProgress);
                canvas.drawCircle(mCenter.x, mCenter.y, ringStart + ringBond / 2,
                        getRingShaderPaint(ringStart + ringBond / 2, alpha, ringBond));
            } else {
                canvas.drawCircle(mCenter.x, mCenter.y, ringStart + ringBond / 2,
                        getPatternPaint(ringBond, ringTwoColor, alpha, false));
            }
            if (progress < 0.5) {
                // 放大
                tmpContentCircleRadius = contentCircleRadius + dp2px(8) * (progress / 0.5f);
            } else {
                // 缩小
                tmpContentCircleRadius = contentCircleRadius + dp2px(8) * ((1 - progress) / 0.5f);
            }
        }
    }


    private void drawCircleLines(Canvas canvas) {
        canvas.save();
        canvas.rotate(rotate, mCenter.x, mCenter.y);
        // 三个线条,中右
        canvas.drawArc(lineRectOne, 10, 220, false, getPatternPaint(dp2px(2), isCleaned ? 0xFF004FFF : 0xFFFF9600, false));
        canvas.drawArc(lineRectThree, 50, 252, false, getPatternPaint(dp2px(2), 0x99FFFFFF, false));

        canvas.drawCircle(redPoint.x + dp2px(3) / 4, redPoint.y + dp2px(3) / 4, dp2px(2), getPatternPaint(0, isCleaned ? 0xFFFFFFFF : 0xFFFFFFFF, true));
        canvas.drawCircle(redPoint2.x + dp2px(3) / 4, redPoint2.y + dp2px(3) / 4, dp2px(2), getPatternPaint(0, isCleaned ? 0xFFFFFFFF : 0xFFFFFFFF, true));
        canvas.restore();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initValues(w, h);
    }

    private void initValues(int w, int h) {
        mWidth = w;
        mHeight = h;
        mCenter = new PointF(mWidth / 2f, mHeight / 2f);
        // 6/10 = 3/5
        circleRadius = mCenter.x / 5 * 3.5f;
        contentCircleRadius = circleRadius - dp2px(18);
        tmpContentCircleRadius = contentCircleRadius;
        ringStart = circleRadius;
        ringBond = mCenter.x - circleRadius - dp2px(12);
        lineOneRadius = circleRadius;
        float lineTwoRadius = lineOneRadius - dp2px(6);
        float lineThreeRadius = lineOneRadius + dp2px(8);

        lineRectOne = new RectF(
                mCenter.x - lineOneRadius,
                mCenter.y - lineOneRadius,
                mCenter.x + lineOneRadius,
                mCenter.y + lineOneRadius);

        lineRectTwo = new RectF(
                mCenter.x - lineTwoRadius,
                mCenter.y - lineTwoRadius,
                mCenter.x + lineTwoRadius,
                mCenter.y + lineTwoRadius);

        lineRectThree = new RectF(
                mCenter.x - lineThreeRadius,
                mCenter.y - lineThreeRadius,
                mCenter.x + lineThreeRadius,
                mCenter.y + lineThreeRadius);

        contentGradient = new LinearGradient(
                mCenter.x - contentCircleRadius, mCenter.y - contentCircleRadius,
                mCenter.x + contentCircleRadius, mCenter.y + contentCircleRadius,
                0xFFFFFFFF, 0xFFDFDFDF, Shader.TileMode.CLAMP
        );

        redPoint = getCoordinateOfCircleAngle(mCenter, lineOneRadius, 230);
        redPoint2 = getCoordinateOfCircleAngle(mCenter, lineThreeRadius, 302);
        Path regionPath = new Path();
        regionPath.addCircle(mCenter.x, mCenter.y, contentCircleRadius, Path.Direction.CW);
        mViewRegion = new Region();
        mViewRegion.setPath(regionPath, new Region(0, 0, w, h));
        float rectOffset = dp2px(26);
        float y = mCenter.y * 1.3f;
        dstRect = new Rect(
                (int) (mCenter.x - rectOffset),
                (int) (y - rectOffset),
                (int) (mCenter.x + rectOffset),
                (int) (y + rectOffset)
        );
    }

    private Paint getPatternPaint(float strokeWidth, int color, boolean isFill) {
        return getPatternPaint(strokeWidth, color, -1, isFill);
    }

    int startR = Integer.parseInt("61", 16);
    int startG = Integer.parseInt("FE", 16);
    int startB = Integer.parseInt("AE", 16);
    int endR = Integer.parseInt("17", 16);
    int endG = Integer.parseInt("D5", 16);
    int endB = Integer.parseInt("FE", 16);

    private Paint getRingShaderPaint(float radius, int alpha, float strokeWidth) {
        LinearGradient linearGradient = gradientSparseArray.get(alpha);
        if (linearGradient == null) {
            int startColor = Color.argb(alpha, startR, startG, startB);
            int endColor = Color.argb(alpha, endR, endG, endB);
            linearGradient = new LinearGradient(
                    mCenter.x - radius, mCenter.y - radius, mCenter.x + radius, mCenter.x + radius, startColor, endColor, Shader.TileMode.CLAMP
            );
            gradientSparseArray.put(alpha, linearGradient);
        }
        patternPaint.setShader(null);
        patternPaint.setShader(linearGradient);
        patternPaint.setStyle(Paint.Style.STROKE);
        patternPaint.setStrokeWidth(strokeWidth);
        return patternPaint;
    }

    private Paint getPatternPaint(float strokeWidth, int color, int alpha, boolean isFill) {
        patternPaint.setShader(null);
        patternPaint.setStyle(isFill ? Paint.Style.FILL : Paint.Style.STROKE);
        patternPaint.setStrokeWidth(strokeWidth);
        patternPaint.setColor(color);
        if (alpha != -1) {
            patternPaint.setAlpha(alpha);
        }
        return patternPaint;
    }

    private Paint getTextPaint(Paint.Align align, float textSize, int color) {
        textPaint.setTextAlign(align);
        textPaint.setTextSize(textSize);
        textPaint.setColor(color);
        return textPaint;
    }

    private Paint getContentBgPaint() {
        return getShaderPaint(contentGradient);
    }

    private Paint getShaderPaint(Shader shader) {
        shaderPaint.setShader(shader);
        return shaderPaint;
    }


    private float dp2px(float dp) {
        return getResources().getDisplayMetrics().density * dp;
    }

    /**
     * 根据圆心，半径，角度 获取圆上坐标
     *
     * @param center 圆心坐标点
     * @param radius 半径
     * @param angle  角度
     * @return Point 对应圆上坐标
     */
    private Point getCoordinateOfCircleAngle(PointF center, float radius, float angle) {
        Point point = new Point();
        point.x = (int) (center.x + radius * Math.cos(angle * 3.14 / 180));
        point.y = (int) (center.y + radius * Math.sin(angle * 3.14 / 180));
        return point;
    }


    /**
     * 停止动画
     */
    public void release() {
        valueAnimator.cancel();
    }

    /**
     * 暂停动画
     */
    public void pause() {
        valueAnimator.pause();
    }

    /**
     * 恢复动画
     */
    public void resume() {
        valueAnimator.resume();
    }

    /**
     * 开始动画
     */
    public void start() {
        rotate = 0;
        valueAnimator.cancel();
        valueAnimator.start();
    }

    public void setCleanStatus(boolean isCleaned) {
        this.isCleaned = isCleaned;
    }


    /**
     * 设置可清理数据大小
     * 不要同时设置，isCleaned是对显示状态的控制
     *
     * @param size
     */
    public void setCleanData(float size) {
        sizeStr = getFormatSizeStrArr(size);
        this.size = size;
    }

    /**
     * 设置内存使用情况文字
     *
     * @param usage
     */
    public void setStorageData(String usage) {
        this.strUsage = usage;
    }

    public static String getFormatSizeStr(float size) {
        String[] arr = getFormatSizeStrArr(size);
        return arr[0] + arr[1];
    }

    public static String[] getFormatSizeStrArr(float size) {
        String[] result = new String[2];
        result[1] = "KB";
        float tmp = size;
        if (tmp > 1000) {
            result[1] = "MB";
            tmp /= 1024f;
            if (tmp > 1000) {
                tmp /= 1024f;
                result[1] = "GB";
            }
        }
        String pattern = "0.00";
        if (tmp < 10) {
            pattern = "0.00";
        } else if (tmp < 100) {
            pattern = "00.0";
        } else if (tmp < 1000) {
            pattern = "000";
        }
        mDecimalFormat.applyPattern(pattern);
        result[0] = mDecimalFormat.format(tmp);
        return result;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mViewRegion.contains((int) event.getX(), (int) event.getY())) {
                performClick();
            }
        }
        return true;
    }
}
