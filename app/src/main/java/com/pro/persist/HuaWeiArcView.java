package com.pro.persist;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by JackYang on 2017/9/25.
 * 模仿华为健康 消耗热量和运动计步的自定义view
 */

public class HuaWeiArcView extends View {

    //热量圆弧宽度
    private float hotStrokeWidth = 40f;
    //热量圆弧当前进度颜色
    private int hotStrokeProgressColor = Color.parseColor("#586FFB");

    //计步圆弧宽度
    private float stepStrokeWidth = 20f;
    //计步圆弧当前进度颜色
    private int stepStrokeProgressColor = Color.parseColor("#04DAB3");

    //默认的圆弧颜色
    private int defaultStrokeColor = Color.parseColor("#ECECEC");
    //默认开始角度
    private int startAngle = 180;
    //默认扫过的弧度
    private int defaultSweepAngle = 180;
    //当前热度长度
    private int currentHotLength = 40;
    //当前计步长度
    private int currentStepLength = 35;

    //两个圆环之间的间距  文字间距
    private float stokeOffset = 50;
    //文字大小
    private int stepTextSize = 30;
    private float stepTextHeight = 0;
    //当前步数文字大小
    private int stepNumSize = 60;
    private float stepNumHeight = 0;
    //当前步数
    private int currentStepNum = 6649;
    //目标步数
    private int targetStepNum = 10000;


    public HuaWeiArcView(Context context) {
        super(context);
    }

    public HuaWeiArcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HuaWeiArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //中心点坐标
        float centerX = getWidth() / 2;
        //热量外矩形区域
        RectF hotRectF = new RectF();
        float hotL = hotStrokeWidth / 2;
        float hotT = hotStrokeWidth / 2;
        float hotR = centerX * 2 - hotStrokeWidth / 2;
        float hotB = hotR;
        hotRectF.set(hotL, hotT, hotR, hotB);
        //计步外矩形区域
        RectF stepRectF = new RectF();
        float stepL = hotStrokeWidth / 2 + stokeOffset;
        float stepT = hotStrokeWidth / 2 + stokeOffset;
        float stepR = centerX * 2 - hotStrokeWidth / 2 - stokeOffset;
        float stepB = stepR;
        stepRectF.set(stepL, stepT, stepR, stepB);
        //绘制热量圆弧
        drawHotStroke(canvas, centerX, hotRectF);
        //绘制计步圆弧
        drawStepStroke(canvas, centerX, stepRectF);
        //绘制文字
        drawText(canvas, centerX);
        //绘制当前步数
        drawStepNum(canvas, centerX);
        //绘制强度文字
        drawStrongerText(canvas, centerX);
    }

    private void drawStrongerText(Canvas canvas, float centerX) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(stepTextSize);
        paint.setTextAlign(Paint.Align.CENTER);
        String textTop = "";
        if (currentStepNum < targetStepNum / 2) {
            textTop = "毫无压力";
        } else if (currentStepNum >= targetStepNum / 2) {
            textTop = "中高强度";
        } else if (currentStepNum > targetStepNum) {
            textTop = "超常发挥";
        }
        Rect textF = new Rect();
        paint.getTextBounds(textTop, 0, textTop.length(), textF);
        float textY = textF.height() / 2 + hotStrokeWidth / 2 + stepStrokeWidth + 2 * stokeOffset + stepTextHeight + stokeOffset / 2 + stepNumHeight ;
        canvas.drawText(textTop, centerX, textY, paint);
    }

    private void drawStepNum(Canvas canvas, float centerX) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(stepNumSize);
        paint.setTextAlign(Paint.Align.CENTER);
        String textTop = currentStepNum + "步";
        Rect textF = new Rect();
        paint.getTextBounds(textTop, 0, textTop.length(), textF);
        stepNumHeight = textF.height();
        float textY = textF.height() / 2 + hotStrokeWidth / 2 + stepStrokeWidth + 2 * stokeOffset + stepTextHeight + stokeOffset / 2;
        canvas.drawText(textTop, centerX, textY, paint);
    }

    private void drawText(Canvas canvas, float centerX) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(stepTextSize);
        paint.setTextAlign(Paint.Align.CENTER);
        String textTop = "目标" + " " + targetStepNum;
        Rect textF = new Rect();
        paint.getTextBounds(textTop, 0, textTop.length(), textF);
        //文字高度
        stepTextHeight = textF.height();
        float textY = textF.height() / 2 + hotStrokeWidth / 2 + stepStrokeWidth + 2 * stokeOffset;
        canvas.drawText(textTop, centerX, textY, paint);
    }

    //热量所有的绘制
    private void drawHotStroke(Canvas canvas, float x, RectF f) {
        //绘制热量默认的圆弧
        drawDefaultHotStroke(canvas, f, hotStrokeWidth);
        //绘制当前进度
        drawProgressHotStroke(canvas, f);
    }

    private void drawProgressHotStroke(Canvas canvas, RectF f) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(hotStrokeProgressColor);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(hotStrokeWidth);
        canvas.drawArc(f, startAngle, currentHotLength, false, paint);
    }


    //计步所有绘制
    private void drawStepStroke(Canvas canvas, float x, RectF f) {
        //绘制计步默认的圆弧
        drawDefaultHotStroke(canvas, f, stepStrokeWidth);
        //绘制当前进度
        drawProgressStepStroke(canvas, f);
    }

    private void drawProgressStepStroke(Canvas canvas, RectF f) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(stepStrokeProgressColor);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stepStrokeWidth);
        canvas.drawArc(f, startAngle, currentStepLength, false, paint);
    }

    private void drawDefaultHotStroke(Canvas canvas, RectF f, float strokeWidth) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(defaultStrokeColor);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawArc(f, startAngle, defaultSweepAngle, false, paint);
    }



}
