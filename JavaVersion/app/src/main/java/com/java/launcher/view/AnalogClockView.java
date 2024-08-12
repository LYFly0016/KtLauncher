package com.java.launcher.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.MotionEvent;


import com.java.launcher.R;

import java.util.Calendar;

public class AnalogClockView extends View {
    private Paint paintCircle;   // 绘制时钟表盘圆圈的画笔
    private Paint paintHour;     // 绘制小时指针的画笔
    private Paint paintMinute;   // 绘制分钟指针的画笔
    private Paint paintSecond;   // 绘制秒钟指针的画笔
    private Bitmap hourHandBitmap;
    private Bitmap minuteHandBitmap;
    private Bitmap secondHandBitmap;
    private Bitmap clockBackgroundBitmap;

    private Paint paintText;     // 绘制数字时钟的画笔
    private Paint paintTick;     // 绘制刻度线的画笔
    private RectF circleBounds;  // 定义时钟表盘的边界
    private float centerX;       // 时钟的中心 X 坐标
    private float centerY;       // 时钟的中心 Y 坐标
    private float radius;        // 时钟表盘的半径
    private float scaleFactor = 1f; // 缩放因子
    private static final float MAX_SCALE_FACTOR = 1.25f;
    private static final float MIN_SCALE_FACTOR = 0.75f;
    private float scaleStartDistance = 0; // 缩放开始时的距离
    private Handler handler;     // 用于定时更新时钟显示的 Handler
    private Runnable clockUpdater; // 定时更新时钟的 Runnable
    private OnScaleListener onScaleListener;

    public AnalogClockView(Context context) {
        super(context);
        init(context);
    }

    public AnalogClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public interface OnScaleListener {
        void onScale(float scaleFactor);
    }

    public void setOnScaleListener(OnScaleListener listener) {
        this.onScaleListener = listener;
    }

    private void init(Context context) {
        // 初始化表盘画笔
        paintCircle = new Paint();
        paintCircle.setColor(Color.BLACK);
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setStrokeWidth(5);
        paintCircle.setAntiAlias(true);
        // 加载 Drawable 资源
//        Drawable hourHandDrawable = context.getDrawable(R.drawable.hour_hand);
//        Drawable minuteHandDrawable = context.getDrawable(R.drawable.minute_hand);
//        Drawable secondHandDrawable = context.getDrawable(R.drawable.second_hand);
        Drawable clockBackgroundDrawable = context.getDrawable(R.drawable.clock_background);

        // 转换 Drawable 为 Bitmap
//        hourHandBitmap = drawableToBitmap(hourHandDrawable);
//        minuteHandBitmap = drawableToBitmap(minuteHandDrawable);
//        secondHandBitmap = drawableToBitmap(secondHandDrawable);
        clockBackgroundBitmap = drawableToBitmap(clockBackgroundDrawable);

        // 初始化小时指针画笔
        paintHour = new Paint();
        paintHour.setColor(Color.BLACK);
        paintHour.setStrokeWidth(8);
        paintHour.setAntiAlias(true);

        // 初始化分钟指针画笔
        paintMinute = new Paint();
        paintMinute.setColor(Color.BLACK);
        paintMinute.setStrokeWidth(6);
        paintMinute.setAntiAlias(true);

        // 初始化秒钟指针画笔
        paintSecond = new Paint();
        paintSecond.setColor(Color.RED);
        paintSecond.setStrokeWidth(4);
        paintSecond.setAntiAlias(true);

        // 初始化数字时钟画笔
        paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(60); // 设置文本大小
        paintText.setTextAlign(Paint.Align.CENTER); // 文本居中
        paintText.setAntiAlias(true);

        // 初始化刻度线画笔
        paintTick = new Paint();
        paintTick.setColor(Color.BLACK);
        paintTick.setAntiAlias(true);

        // 设置定时更新时钟显示
        handler = new Handler();
        clockUpdater = new Runnable() {
            @Override
            public void run() {
                invalidate(); // 触发重绘
                handler.postDelayed(this, 1000); // 每秒更新一次
            }
        };
        handler.post(clockUpdater);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = Math.max(MIN_SCALE_FACTOR, Math.min(MAX_SCALE_FACTOR, scaleFactor));

        if (onScaleListener != null) {
            onScaleListener.onScale(scaleFactor);
        }
        invalidate(); // 触发重绘
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2f; // 计算中心 X 坐标
        centerY = h / 2f; // 计算中心 Y 坐标
        radius = Math.min(centerX, centerY) - 20; // 计算半径，并留出一定的边距
        circleBounds = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius); // 设置表盘的边界矩形
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawClock(canvas);
    }

    private void drawClock(Canvas canvas) {
        // 应用缩放因子
        canvas.save(); // 保存画布状态
        canvas.scale(scaleFactor, scaleFactor, centerX, centerY); // 缩放画布

        // 计算背景图的目标宽高
        int bgWidth = (int) (radius * 2.25); // 背景图宽度
        int bgHeight = (int) (radius * 2.25); // 背景图高度

        // 创建一个缩放后的背景图
        Bitmap scaledBackgroundBitmap = Bitmap.createScaledBitmap(clockBackgroundBitmap, bgWidth, bgHeight, true);

        // 计算背景图的绘制位置，确保它居中
        float left = centerX - bgWidth / 2;
        float top = centerY - bgHeight / 2;

        // 绘制背景图，保持圆形
        canvas.drawBitmap(scaledBackgroundBitmap, left, top, null);


        // 绘制时钟表盘圆圈
//        canvas.drawCircle(centerX, centerY, radius, paintCircle);

        // 绘制刻度线
        drawTicks(canvas);

        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        float hours = calendar.get(Calendar.HOUR_OF_DAY);
        float minutes = calendar.get(Calendar.MINUTE);
        float seconds = calendar.get(Calendar.SECOND);

        // 计算指针的角度
        float hourAngle = (hours + minutes / 60f) * 30f;
        float minuteAngle = (minutes + seconds / 60f) * 6f;
        float secondAngle = seconds * 6f;

        // 绘制时针、分针和秒针
//        drawHand(canvas, hourHandBitmap, hourAngle, radius * 0.5f);
//        drawHand(canvas, minuteHandBitmap, minuteAngle, radius * 0.7f);
//        drawHand(canvas, secondHandBitmap, secondAngle, radius * 0.9f);
//        // 绘制小时指针
        drawHand(canvas, hourAngle, radius * 0.5f, paintHour);

        // 绘制分钟指针
        drawHand(canvas, minuteAngle, radius * 0.7f, paintMinute);

        // 绘制秒钟指针
        drawHand(canvas, secondAngle, radius * 0.9f, paintSecond);

        // 绘制数字时钟
        drawText(canvas, hours, minutes, seconds);

        canvas.restore(); // 恢复画布状态
    }

//    private void drawHand(Canvas canvas, Bitmap handBitmap, float angle, float handLength) {
//        canvas.save();
//        canvas.rotate(angle, centerX, centerY);
//        canvas.drawBitmap(handBitmap, centerX - handBitmap.getWidth() / 2, centerY - handLength - handBitmap.getHeight() / 2, null);
//        canvas.restore();
//    }

    private void drawHand(Canvas canvas, float angle, float handLength, Paint paint) {
        double radians = Math.toRadians(angle - 90); // 将角度转换为弧度，并调整起始位置
        float endX = (float) (centerX + Math.cos(radians) * handLength); // 计算指针终点的 X 坐标
        float endY = (float) (centerY + Math.sin(radians) * handLength); // 计算指针终点的 Y 坐标
        canvas.drawLine(centerX, centerY, endX, endY, paint); // 绘制指针
    }

    private void drawText(Canvas canvas, float hours, float minutes, float seconds) {
        // 格式化时间文本
        String timeText = String.format("%02d:%02d:%02d", (int) hours, (int) minutes, (int) seconds);
        // 计算文本的基线位置，使其居中
        float textX = centerX;
        float textY = centerY + radius / 4f; // 将文本放置在表盘的底部
        canvas.drawText(timeText, textX, textY, paintText); // 绘制文本
    }

    private void drawTicks(Canvas canvas) {
        // 绘制小时刻度线和数字
        for (int i = 0; i < 12; i++) {
            float angle = i * 30f; // 每小时的角度
            paintTick.setStrokeWidth(6);
            drawTick(canvas, angle, radius * 0.9f, radius * 0.95f, paintTick, Color.BLACK); // 加粗刻度线
        }
        for (int i = 12; i >0; i--) {
            float angle = i * 30f; // 每小时的角度
            // 绘制整点数字
            String number = String.valueOf(i);
            double radians = Math.toRadians(angle - 90); // 将角度转换为弧度，并调整起始位置
            float numberX = (float) (centerX + Math.cos(radians) * radius * 0.75f); // 计算数字的 X 坐标
            float numberY = (float) (centerY + Math.sin(radians) * radius * 0.75f); // 计算数字的 Y 坐标
            canvas.drawText(number, numberX, numberY, paintText); // 绘制数字
        }

        // 绘制分钟刻度线
        for (int i = 0; i < 60; i++) {
            if (i % 5 != 0) { // 跳过小时刻度线的位置
                float angle = i * 6f; // 每分钟的角度
                paintTick.setStrokeWidth(3);
                drawTick(canvas, angle, radius * 0.9f, radius * 0.95f, paintTick, Color.GRAY); // 普通刻度线
            }
        }
    }

    private void drawTick(Canvas canvas, float angle, float startLength, float endLength, Paint paint, int color) {
        paint.setColor(color); // 设置颜色
        double radians = Math.toRadians(angle - 90); // 将角度转换为弧度，并调整起始位置
        float startX = (float) (centerX + Math.cos(radians) * startLength); // 刻度线起点 X 坐标
        float startY = (float) (centerY + Math.sin(radians) * startLength); // 刻度线起点 Y 坐标
        float endX = (float) (centerX + Math.cos(radians) * endLength); // 刻度线终点 X 坐标
        float endY = (float) (centerY + Math.sin(radians) * endLength); // 刻度线终点 Y 坐标
        canvas.drawLine(startX, startY, endX, endY, paint); // 绘制刻度线
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        handler.post(clockUpdater); // 重新启动定时器
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(clockUpdater); // 移除更新时钟的回调
    }

    // 更新TouchEvent缩放逻辑
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) { //双指触控
                    scaleStartDistance = getDistance(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2) {
                    float newDistance = getDistance(event);
                    if (scaleStartDistance > 0) {
                        float newScaleFactor = scaleFactor * (newDistance / scaleStartDistance); // 计算新的缩放因子
                        setScaleFactor(newScaleFactor); // 应用新的缩放因子，并限制在范围内
                        scaleStartDistance = newDistance; // 更新开始距离
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 2) {
                    scaleStartDistance = 0; // 重置开始距离
                }
                break;
        }
        return true;
    }

    private float getDistance(MotionEvent event) {
        float dx = event.getX(0) - event.getX(1);
        float dy = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
