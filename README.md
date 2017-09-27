通过一个简单的自定义的View,模仿华为健康里面消耗热量和计步的圆弧View,来深入的理解RectF以及 canvas的drawArc方法。

华为健康的View是这样的：

![目标效果](https://i.imgur.com/O3Hp98B.png)

最近写了一些自定义的view，其中用到RectF,Rect的地方比较多，于是有了这篇文章，来深入理解下RectF对象。先上效果图：

![效果图](https://i.imgur.com/pmaS1Wp.png)

效果还是有点不太一样，但是别太在意那些细节了。。。

闲言少叙，分析下这个简单的View，可以看到是由两个圆弧和三段文字组成的，这两个圆弧，扫过的区域都是180度，两个圆弧之间需要留有间隙。
而要实现画一段圆弧，其实只需要两个步骤：

>1：算出圆弧中线的一个矩形的方阵，这个地方就需要用到RectF了，为什么说是圆弧中线呢，因为圆弧是由宽度的，但是在绘制的时候，事实上是以圆弧最中间的那条线为基础画的，如果画360度的话，刚好是这个正方形的内切圆。

>2：通过canvas的drawArc方法，就可以了。
>这个oval 参数，就是我们 第一步得到的，圆弧的外切正方形，startAngle是开始的角度，需要注意的是，这个地方开始的0度是几何里面的180度，也就是中心点右边开始计数。
>
>sweepAngle，就是圆弧横扫的角度，很明显，我们的view都是180度。
>
>useCenter，是否包含中心点，我们这里明显不需要
>
>paint 就是圆弧的画笔了
>
> public void drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint) {
        throw new RuntimeException("Stub!");
    }


分析完成之后，就开始动手操作了，
 

 	//默认开始角度
    private int startAngle = 180;

    //默认扫过的弧度
    private int defaultSweepAngle = 180;
	
	   //中心点坐标
        float centerX = getWidth() / 2;
        //热量外矩形区域
        RectF hotRectF = new RectF();
        float hotL = hotStrokeWidth / 2;
        float hotT = hotStrokeWidth / 2;
        float hotR = centerX * 2 - hotStrokeWidth / 2;
        float hotB = hotR;
        hotRectF.set(hotL, hotT, hotR, hotB);


	//默认的灰色区域
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
	//当前进度的圆弧
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


不知道大家有没有注意到计算外切正方形的时候，减去了画笔的宽度的一般，这一点就是上面提到的，绘制弧线的基准是以最中间那条线为准的，如果不减去的话，你会发现，圆弧的上下左右四个点会超出这个view之外。

绘制下面的圆弧的原理和这个一样，就不再赘述了，现在来分析下RectF

		RectF (float left, 
                float top, 
                float right, 
                float bottom)

这是RectF的构造函数，官网的解释是代表左上右下四个方向的坐标，但是其实这样解释并不准确，根据自己的实验，我们可以这样理解，前两个参数是矩阵左上角点的坐标，后两个是右下角点的坐标，这样，一个矩形就出来了。需要注意的是，要保证left <= right ， top <= bottom.嗯，差不多，还是很好理解的。

那么 RectF 和 Rect 的区别是什么呢？
Rect的坐标点为int值，所以精度就没有RectF高了，RectF的构造函数可以直接把 RectF或者Rect传进去构建矩阵。其它的方法，都基本类似。

绘制文字的时候需要注意两个地方，文字的Y坐标，需要加上自身的高度的一半，避免出现覆盖的情况，这个地方使用了Paint的一个方法 getTextBounds 事实上把文字的属性都给 Rect 对象了，这样就可以拿到文字的所有想要的属性，而我们只需要传递文字内容和长度就可以了。



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

至此，这个简单的自定义View 已经写完了，之所以写这篇文章，是因为两点有猫腻的地方，一个是RectF的坐标点的定义,还有一个是canvas的drawArc 方法。














