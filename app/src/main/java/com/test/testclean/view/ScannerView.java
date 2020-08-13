package com.test.testclean.view;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;

import com.test.testclean.R;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;


@SuppressWarnings("FieldCanBeLocal")
public class ScannerView extends View {
    private Point mCenterPoint;
    private Paint mStrokePaint, mFillPaint, mFontPaint;
    private ValueAnimator mScanAnimator, mCleanAnimator, mCleanFinalAnimator;
    private float mScanProgress, mCleanProgress, mCleanFinalProgress;
    private boolean isClean;
    private Canvas mCanvas;
    private Path mPath;
    private LinkedList<ScanPoint> mScanPoints;
    private Random mRandom;
    private static DecimalFormat mDecimalFormat = new DecimalFormat();
    private boolean isCleanBeforeEnd;
    private CleanListener mListener;
    private ArgbEvaluator mEvaluator;

    private int oldRadiusOne; //初始内圆半径
    private int RADIUS_CIRCLE_ONE;   //内圆半径
    private int RADIUS_CIRCLE_TWO;   //内圆连接线圆
    private int RADIUS_CIRCLE_THREE; //八圆点的圆
    private int RADIUS_CIRCLE_FOUR;  //进度圆半径
    private int RADIUS_CIRCLE_FIVE;  //两侧弧形
    private int DISTANCE_ONE_TO_TWO = dp2px(50);
    private int DISTANCE_TWO_TO_THREE = dp2px(10);
    private int DISTANCE_THREE_TO_FOUR = dp2px(10);
    private int DISTANCE_FOUR_TO_FIVE = dp2px(15);
    private int WIDTH_CIRCLE_ONE = dp2px(6);
    private int WIDTH_CIRCLE_TWO = dp2px(2);
    private int WIDTH_LINE = dp2px(1);
    private int WIDTH_CIRCLE_FOUR = dp2px(6);
    private int WIDTH_CIRCLE_FIVE = dp2px(1);

    private float mTrashSize = 0.00f;
    private String mTrashSizeUnderStr = "Trash found";
    private String mCleanBeforeUnderStr = "Cleaning...";
    private int mTrashSizeTextSize = 30;
    private int mTrashSizeUnitTextSize = 12;
    private int mTrashSizeUnderStrTextSize = 12;

    private String mCleanFinalUpperStr = "EXCELLENT";
    private String mCleanFinalUnderLeftStr = "Clean up:";
    private int mCleanFinalUpperStrTextSize = 26;
    private int mCleanFinalUnderLeftTextSize = 12;
    private int mCleanFinalUnderRightTextSize = 15;

    private float tmp;
    private int[] startBgGradientColor = new int[]{0xFFC12800, 0xFFFF9600}; // 背景開始顔色 左上 、右下
    private int[] endBgGradientColor = new int[]{0xFF00219F, 0xFF00EF87};
    private int[] startBgGradientSkipColor = new int[]{0xFFCC5190, 0xFFB79988};
    private int[] endBgGradientSkipColor = new int[]{0xFF9B3EBF, 0xFF728EBA};
    private int[] bgColors = new int[2];
    private int circleProgressColor = 0x7FFFFFFF;
    private boolean isSkipColor = false;

    private boolean isScanRepeat;
    private boolean isAutoSize;


    public ScannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDraw();
        initParams(attrs);
        initAnimator();
        initText(context);
    }

    private void initText(Context context) {
        mTrashSizeUnderStr = "Trash found";
        mCleanBeforeUnderStr = "Cleaning...";
        mCleanFinalUpperStr = "EXCELLENT";
        mCleanFinalUnderLeftStr = "Clean up:";
    }


    private void initParams(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ScannerView);
        isAutoSize = typedArray.getBoolean(R.styleable.ScannerView_autoSize, false);
        typedArray.recycle();
        mStrokePaint.setColor(0xFFFFFFFF);
        mFillPaint.setColor(0xFFFFFFFF);
    }

    private void initDraw() {
        mStrokePaint = new Paint();
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mFillPaint = new Paint();
        mFillPaint.setAntiAlias(true);
        mFillPaint.setStyle(Paint.Style.FILL);
        mFontPaint = new Paint();
        mFontPaint.setStyle(Paint.Style.FILL);
        mFontPaint.setAntiAlias(true);
        mFontPaint.setTextAlign(Paint.Align.CENTER);
        mFontPaint.setColor(0xFFFFFFFF);
        mPath = new Path();
        mScanPoints = new LinkedList<>();
        mRandom = new Random();
        mEvaluator = new ArgbEvaluator();
    }


    private void initAnimator() {
        //ScanAnimator
        mScanAnimator = ValueAnimator.ofFloat(0, 1);
        mScanAnimator.setDuration(3000);
        mScanAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mScanAnimator.addUpdateListener(animation -> {
            mScanProgress = (float) animation.getAnimatedValue();
            scanPointOpa(mScanProgress);
            refreshView();
        });
        mScanAnimator.addListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                mScanPoints.clear();
                isScanRepeat = true;
            }
        });

        //CleanAnimator
        mCleanAnimator = ValueAnimator.ofFloat(0, 1);
        mCleanAnimator.setDuration(3000);
        mCleanAnimator.addUpdateListener(animation -> {
            mCleanProgress = (float) animation.getAnimatedValue();
            refreshView();
            if (mListener != null && mCleanProgress - tmp > 0.01) {

                if (mCleanProgress > 0.3 && mCleanProgress < 0.6 && isSkipColor) {
                    bgColors[0] = (int) mEvaluator.evaluate(-(0.3f - mCleanProgress) / 0.3f, startBgGradientSkipColor[0], endBgGradientSkipColor[0]);
                    bgColors[1] = (int) mEvaluator.evaluate(-(0.3f - mCleanProgress) / 0.3f, startBgGradientSkipColor[1], endBgGradientSkipColor[1]);
                } else {
                    bgColors[0] = (int) mEvaluator.evaluate(mCleanProgress, startBgGradientColor[0], endBgGradientColor[0]);
                    bgColors[1] = (int) mEvaluator.evaluate(mCleanProgress, startBgGradientColor[1], endBgGradientColor[1]);
                }
                tmp = mCleanProgress;
                mListener.onChange(bgColors);
            }
        });
        mCleanAnimator.addListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCleanFinalAnimator.cancel();
                isCleanBeforeEnd = true;
                tmp = 0;
                mCleanFinalAnimator.start();
            }
        });

        //CleanFinalAnimator
        mCleanFinalAnimator = ValueAnimator.ofFloat(0, 1);
        mCleanFinalAnimator.setDuration(500);
        mCleanFinalAnimator.addUpdateListener(animation -> {
            mCleanFinalProgress = (float) animation.getAnimatedValue();
            refreshView();
        });
        mCleanFinalAnimator.addListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null) {
                    mListener.onCleanEnd();
                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.mCanvas = canvas;
        drawFixedPattern();
        if (isClean && (mCleanProgress > 0.001 && mCleanProgress <= 1.0)) {
            drawScanCircleProgress();
            RADIUS_CIRCLE_ONE = (int) (oldRadiusOne + mCleanFinalProgress * DISTANCE_ONE_TO_TWO);
            if (isCleanBeforeEnd) {
                drawCleanFinalText();
            } else {
                drawTrashSize(mTrashSize * (1 - mCleanProgress), mCleanBeforeUnderStr);
            }
        } else {
            //scan
            drawFixedPattern();
            drawScanCircleProgress();
            drawScanSectorProgress();
            drawTrashSize(mTrashSize, mTrashSizeUnderStr);
            drawScanPoint();
        }
    }

    private void drawScanPoint() {
        for (ScanPoint scanPoint : mScanPoints) {
            RadialGradient radialGradient = new RadialGradient(
                    scanPoint.point.x, scanPoint.point.y, scanPoint.radius,
                    new int[]{0xFFFFFFFF, 0x00FFFFFF}, new float[]{0.1f, 0.99f}, Shader.TileMode.CLAMP
            );
            getFillPaint(0).setShader(radialGradient);
            mCanvas.drawCircle(scanPoint.point.x, scanPoint.point.y, scanPoint.radius, mFillPaint);
            mFillPaint.setShader(null);
        }
    }

    /**
     * 绘制扫描圆圈进度
     */
    private void drawScanCircleProgress() {
        float mSweepAngle = isClean ? (1 - mCleanProgress) * 360 : isScanRepeat ? 360 : mScanProgress * 360 + mScanProgress * 50;
        float radius = RADIUS_CIRCLE_FOUR + WIDTH_CIRCLE_FOUR / 2f;
        mCanvas.drawArc(mCenterPoint.x - radius, mCenterPoint.y - radius,
                mCenterPoint.x + radius, mCenterPoint.y + radius,
                -90, mSweepAngle, false, getStrokePaint(WIDTH_CIRCLE_FOUR, circleProgressColor));
    }

    /**
     * 绘制扫描扇形进度
     */
    private void drawScanSectorProgress() {
        mCanvas.save();
        float arcWidth = RADIUS_CIRCLE_TWO - RADIUS_CIRCLE_ONE - (WIDTH_CIRCLE_ONE + WIDTH_CIRCLE_TWO) / 2f;
        //设置圆弧渐变Paint mStrokePaint
        getStrokePaint(arcWidth);
        int[] colors = new int[]{0x4CB917FE, 0xD7617BFE};
        SweepGradient sweepGradient = new SweepGradient(mCenterPoint.x, mCenterPoint.y, colors, new float[]{0.0f, mScanProgress});
        mStrokePaint.setShader(sweepGradient);
        float difference = (arcWidth) / 2f;
        RectF progressRectF = new RectF(mCenterPoint.x - RADIUS_CIRCLE_TWO + WIDTH_CIRCLE_TWO / 2f + difference,
                mCenterPoint.y - RADIUS_CIRCLE_TWO + WIDTH_CIRCLE_TWO / 2f + difference,
                mCenterPoint.x + RADIUS_CIRCLE_TWO - WIDTH_CIRCLE_TWO / 2f - difference,
                mCenterPoint.y + RADIUS_CIRCLE_TWO - WIDTH_CIRCLE_TWO / 2f - difference);
        mCanvas.rotate(-90, mCenterPoint.x, mCenterPoint.y);
        mCanvas.drawArc(progressRectF, 0, 360 * mScanProgress, false, mStrokePaint);
        mStrokePaint.setShader(null);
        mCanvas.restore();
    }

    /**
     * 绘制固定底图
     */
    private void drawFixedPattern() {
        //内圆
        mCanvas.drawCircle(mCenterPoint.x, mCenterPoint.y, RADIUS_CIRCLE_ONE, getStrokePaint(WIDTH_CIRCLE_ONE));
        //线条连接圆
        mCanvas.drawCircle(mCenterPoint.x, mCenterPoint.y, RADIUS_CIRCLE_TWO, getStrokePaint(WIDTH_CIRCLE_TWO));
        drawLines();
        //两侧弧形
        mCanvas.drawArc(mCenterPoint.x - RADIUS_CIRCLE_FIVE, mCenterPoint.y - RADIUS_CIRCLE_FIVE,
                mCenterPoint.x + RADIUS_CIRCLE_FIVE, mCenterPoint.y + RADIUS_CIRCLE_FIVE,
                -45, 90, false, getStrokePaint(WIDTH_CIRCLE_FIVE, circleProgressColor));
        mCanvas.drawArc(mCenterPoint.x - RADIUS_CIRCLE_FIVE, mCenterPoint.y - RADIUS_CIRCLE_FIVE,
                mCenterPoint.x + RADIUS_CIRCLE_FIVE, mCenterPoint.y + RADIUS_CIRCLE_FIVE,
                135, 90, false, getStrokePaint(WIDTH_CIRCLE_FIVE, circleProgressColor));
    }

    /**
     * 绘制连接线、45°尖角及小圆点
     */
    private void drawLines() {
        for (int i = 0; i <= 360; i += 15) {
            Point lineStart = getCoordinateOfCircleAngle(mCenterPoint, RADIUS_CIRCLE_ONE, i);
            Point lineEnd = getCoordinateOfCircleAngle(mCenterPoint, RADIUS_CIRCLE_TWO, i);

            mCanvas.drawLine(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y, getStrokePaint(WIDTH_LINE));
            if (i % 45 == 0 && i != 360) {
                //绘制三角形及45°倍数圆点
                mPath.reset();
                Point triangleApex = getCoordinateOfCircleAngle(mCenterPoint, RADIUS_CIRCLE_TWO - dp2px(10), i);
                Point triangleLeft = getCoordinateOfCircleAngle(mCenterPoint, RADIUS_CIRCLE_TWO, i - 1.5f);
                Point triangleRight = getCoordinateOfCircleAngle(mCenterPoint, RADIUS_CIRCLE_TWO, i + 1.5f);
                mPath.moveTo(triangleApex.x, triangleApex.y);
                mPath.lineTo(triangleLeft.x, triangleLeft.y);
                mPath.lineTo(triangleRight.x, triangleRight.y);
                mPath.close();

                mCanvas.drawPath(mPath, getFillPaint(1));

                Point circlePoint = getCoordinateOfCircleAngle(mCenterPoint, RADIUS_CIRCLE_THREE, i);
                mCanvas.drawCircle(circlePoint.x, circlePoint.y, dp2px(2), getFillPaint(0, 0xFFE9EEFF));
            }
        }
    }

    /**
     * 扫描时出现的 圆点的添加、更新、删除
     *
     * @param scale 当前扫描进度
     */
    private void scanPointOpa(float scale) {
        int angle = (int) (scale * 360);

        //angle % 15尽量保证扫描原点不在线条上
        if (isAdd() && angle % 15 != 0 && scale < 0.9) {
            ScanPoint last = null;
            try {
                last = mScanPoints.getLast();
            } catch (Exception e) {
                //if null,skip
            }
            if (last == null || scale - last.scale > 0.02) {
                //原点区域控制在圆弧 最低位1/4以上 最高位1/4以下
                int limit = RADIUS_CIRCLE_ONE + DISTANCE_ONE_TO_TWO / 4 + mRandom.nextInt(DISTANCE_ONE_TO_TWO - DISTANCE_ONE_TO_TWO / 2);
                mScanPoints.add(new ScanPoint(
                        getCoordinateOfCircleAngle(mCenterPoint, limit, angle - 90), 10, scale + 0.15f));
            }
        }
        ListIterator<ScanPoint> iterable = mScanPoints.listIterator();
        while (iterable.hasNext()) {
            ScanPoint scanPoint = iterable.next();
            scanPoint.radius = (scanPoint.scale - scale) * 100;
            if (scanPoint.radius < 2) {
                iterable.remove();
                break;
            }
        }
    }

    /**
     * 随机添加
     *
     * @return true，添加
     */
    private boolean isAdd() {
        return mRandom.nextInt(3) == 1;
    }

    /**
     * 根据圆心，半径，角度 获取圆上坐标
     *
     * @param center 圆心坐标点
     * @param radius 半径
     * @param angle  角度
     * @return Point 对应圆上坐标
     */
    private Point getCoordinateOfCircleAngle(Point center, int radius, float angle) {
        Point point = new Point();
        point.x = (int) (center.x + radius * Math.cos(angle * 3.14 / 180));
        point.y = (int) (center.y + radius * Math.sin(angle * 3.14 / 180));
        return point;
    }

    /**
     * 统一刷新，可以控制刷新频率
     */
    private void refreshView() {
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initUnit(w, h);
    }

    /**
     * 初始化各项默认值
     */
    private void initUnit(int w, int h) {
        mCenterPoint = new Point();
        mCenterPoint.x = w / 2;
        mCenterPoint.y = h / 2;

        int min = Math.min(w, h);

        RADIUS_CIRCLE_ONE = min / 2 / 2 - dp2px(20);
        RADIUS_CIRCLE_TWO = RADIUS_CIRCLE_ONE + DISTANCE_ONE_TO_TWO;
        RADIUS_CIRCLE_THREE = RADIUS_CIRCLE_TWO + DISTANCE_TWO_TO_THREE;
        RADIUS_CIRCLE_FOUR = RADIUS_CIRCLE_THREE + DISTANCE_THREE_TO_FOUR;
        RADIUS_CIRCLE_FIVE = RADIUS_CIRCLE_FOUR + DISTANCE_FOUR_TO_FIVE;
        oldRadiusOne = RADIUS_CIRCLE_ONE;
    }


    private Paint getFontPaint(float textSize) {
        return getFontPaint(textSize, Paint.Align.CENTER);
    }

    private Paint getFontPaint(float textSize, Paint.Align align) {
        return getFontPaint(textSize, 0xFFFFFFFF, align);
    }

    private Paint getFontPaint(float textSize, @ColorInt int color, Paint.Align align) {
        mFontPaint.setTextSize(sp2px(textSize));
        mFontPaint.setTextAlign(align);
        mFontPaint.setColor(color);
        return mFontPaint;
    }

    private Paint getStrokePaint(float strokeWidth) {
        return getStrokePaint(strokeWidth, 0xFFFFFFFF);
    }

    private Paint getStrokePaint(float strokeWidth, @ColorInt int color) {
        return getPaint(mStrokePaint, strokeWidth, color);
    }

    private Paint getFillPaint(float strokeWidth) {
        return getFillPaint(strokeWidth, 0xFFFFFFFF);
    }

    private Paint getFillPaint(float strokeWidth, @ColorInt int color) {
        return getPaint(mFillPaint, strokeWidth, color);
    }

    private Paint getPaint(Paint paint, float strokeWidth, @ColorInt int color) {
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        return paint;
    }

    public void startScan() {
        mScanPoints.clear();
        mScanAnimator.cancel();
        mCleanAnimator.cancel();
        mCleanFinalAnimator.cancel();
        isClean = false;
        RADIUS_CIRCLE_ONE = oldRadiusOne;
        mTrashSize = 0;
        mCleanProgress = 0;
        mScanProgress = 0;
        mCleanFinalProgress = 0;
        isCleanBeforeEnd = false;
        isScanRepeat = false;
        mScanAnimator.start();
    }

    public void startClean() {
        mScanPoints.clear();
        mScanAnimator.cancel();
        mCleanAnimator.cancel();
        mCleanFinalAnimator.cancel();
        isClean = true;
        RADIUS_CIRCLE_ONE = oldRadiusOne;
        mCleanProgress = 0;
        mScanProgress = 0;
        mCleanFinalProgress = 0;
        isCleanBeforeEnd = false;
        isScanRepeat = false;
        mCleanAnimator.start();
    }

    public void onPause() {
        if (mScanAnimator.isRunning()) mScanAnimator.pause();
        if (mCleanAnimator.isRunning()) mCleanAnimator.pause();
        if (mCleanFinalAnimator.isRunning()) mCleanFinalAnimator.pause();
    }


    public void onResume() {
        if (mScanAnimator.isPaused()) mScanAnimator.resume();
        if (mCleanAnimator.isPaused()) mCleanAnimator.resume();
        if (mCleanFinalAnimator.isPaused()) mCleanFinalAnimator.resume();
    }

    public void release() {
        mScanPoints.clear();
        mScanAnimator.cancel();
        mCleanAnimator.cancel();
        mCleanFinalAnimator.cancel();
    }

    /**
     * 格式化显示字符串
     *
     * @param size 大小，KB单位
     * @return result[0]：大小，result[1]：单位
     */
    public static String[] getFormatSizeStr(float size) {
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

    /**
     * 设置垃圾文件大小
     *
     * @param size     文件大小，KB单位
     * @param underStr 底部文字
     */
    public void setScanTrashSize(float size, String underStr) {
        mTrashSize = size;
        mTrashSizeUnderStr = underStr;
    }

    public float getTrashSize() {
        return mTrashSize;
    }


    /**
     * 绘制扫描、第一段清理时文字
     *
     * @param size     文件大小    eg:  17.3 GB
     * @param underStr 下方文字
     */
    private void drawTrashSize(float size, String underStr) {
        //todo 删除下面格式化语句
        String[] sizeStr = getFormatSizeStr(size);
        Paint paint = getFontPaint(mTrashSizeTextSize);
        Paint.FontMetrics metrics = paint.getFontMetrics();
        float x = mCenterPoint.x + mFontPaint.measureText(sizeStr[0]) / 2;
        float y = mCenterPoint.y + Math.abs(metrics.bottom - (metrics.bottom - metrics.top) / 2);
        mCanvas.drawText(sizeStr[0], mCenterPoint.x, y, getFontPaint(mTrashSizeTextSize));
        mCanvas.drawText(sizeStr[1], x, y, getFontPaint(mTrashSizeUnitTextSize, Paint.Align.LEFT));
        mCanvas.drawText(underStr, mCenterPoint.x, mCenterPoint.y + RADIUS_CIRCLE_ONE / 2f, getFontPaint(mTrashSizeUnderStrTextSize));
    }

    /**
     * 绘制清理第二段文字
     */
    private void drawCleanFinalText() {
        String[] sizeStr = getFormatSizeStr(mCleanFinalProgress * mTrashSize);
        Paint paint = getFontPaint(mCleanFinalUpperStrTextSize);
        Paint.FontMetrics metrics = paint.getFontMetrics();
        float y = mCenterPoint.y + Math.abs(metrics.bottom - (metrics.bottom - metrics.top) / 2);
        mCanvas.drawText(mCleanFinalUpperStr, mCenterPoint.x, y, getFontPaint(mCleanFinalUpperStrTextSize));
        mCanvas.drawText(mCleanFinalUnderLeftStr, mCenterPoint.x, mCenterPoint.y + RADIUS_CIRCLE_ONE / 2f,
                getFontPaint(mCleanFinalUnderLeftTextSize, Paint.Align.RIGHT));
        mCanvas.drawText(sizeStr[0] + sizeStr[1], mCenterPoint.x, mCenterPoint.y + RADIUS_CIRCLE_ONE / 2f,
                getFontPaint(mCleanFinalUnderRightTextSize, 0xFFFF5E5E, Paint.Align.LEFT));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (isAutoSize) {
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;

            int size = Math.min(width, height);
            setMeasuredDimension((int) (size * 0.8), (int) (size * 0.8f));
        }
    }

    private int dp2px(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5);
    }

    private int sp2px(float sp) {
        return (int) (getResources().getDisplayMetrics().scaledDensity * sp + 0.5);
    }


    class ScanPoint {
        Point point;
        float radius;
        float scale;

        ScanPoint(Point point, float radius, float scale) {
            this.point = point;
            this.radius = radius;
            this.scale = scale;
        }
    }

    public interface CleanListener {
        void onChange(int[] colors);

        void onCleanEnd();
    }

    public void setCleanListener(CleanListener listener) {
        this.mListener = listener;
    }


    interface AnimationListener extends Animator.AnimatorListener {


        @Override
        default void onAnimationStart(Animator animation) {
        }

        @Override
        default void onAnimationEnd(Animator animation) {
        }

        @Override
        default void onAnimationCancel(Animator animation) {
        }

        @Override
        default void onAnimationRepeat(Animator animation) {
        }
    }
}
