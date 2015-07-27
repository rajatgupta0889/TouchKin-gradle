package com.touchKin.touchkinapp.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.touchKin.touckinapp.R.color;

public class MyLinearLayout extends View {
	Paint paint;
	private PointF point;

	public MyLinearLayout(Context context) {

		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub

	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub

		if (point != null) {
			int radius = 50;
			paint.setColor(color.text_orange);
			canvas.drawCircle(point.x, point.y, radius, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			point = new PointF(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_UP:
			point = null;
			break;
		case MotionEvent.ACTION_MOVE:
			point = new PointF(event.getX(), event.getY());
			break;
		}
		invalidate();
		return true;
	}
}
