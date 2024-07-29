package com.java.launcher.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

public class AnalogClockView extends View {
    private Paint paintCircle;   // 绘制时钟表盘圆圈的画笔
    private Paint paintHour;     // 绘制小时指针的画笔
    private Paint paintMinute;   // 绘制分钟指针的画笔
    private Paint paintSecond;   // 绘制秒钟指针的画笔
    private Paint paintText;     // 绘制数字时钟的画笔
    private RectF circleBounds;  // 定义时钟表盘的边界
    private float centerX;       // 时钟的中心 X 坐标
    private float centerY;       // 时钟的中心 Y 坐标
    private float radius;        // 时钟表盘的半径
    private Handler handler;     // 用于定时更新时钟显示的 Handler
    private Runnable clockUpdater; // 定时更新时钟的 Runnable

    public AnalogClockView(Context context) {
        super(context);
        init();
    }

    public AnalogClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 初始化表盘画笔
        paintCircle = new Paint();
        paintCircle.setColor(Color.BLACK);
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setStrokeWidth(5);
        paintCircle.setAntiAlias(true);

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
        // 绘制时钟表盘圆圈
        canvas.drawCircle(centerX, centerY, radius, paintCircle);

        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        float hours = calendar.get(Calendar.HOUR_OF_DAY);
        float minutes = calendar.get(Calendar.MINUTE);
        float seconds = calendar.get(Calendar.SECOND);

        // 计算指针的角度
        float hourAngle = (hours + minutes / 60f) * 30f;
        float minuteAngle = (minutes + seconds / 60f) * 6f;
        float secondAngle = seconds * 6f;

        // 绘制小时指针
        drawHand(canvas, hourAngle, radius * 0.5f, paintHour);

        // 绘制分钟指针
        drawHand(canvas, minuteAngle, radius * 0.7f, paintMinute);

        // 绘制秒钟指针
        drawHand(canvas, secondAngle, radius * 0.9f, paintSecond);

        // 绘制数字时钟
        drawText(canvas, hours, minutes, seconds);
    }

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
        float textY = centerY + radius / 2f; // 将文本放置在表盘的底部
        canvas.drawText(timeText, textX, textY, paintText); // 绘制文本
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
}
