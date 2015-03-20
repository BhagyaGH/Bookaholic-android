package com.bhagya.bookaholic.map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.bhagya.bookaholic.R;

public class DrawView extends View {

	Paint paint = new Paint();

	public DrawView(Context context) {
		super(context);
		paint.setColor(Color.BLACK);
	}

	@Override
	public void onDraw(Canvas canvas) {
		BookFairMap map = new BookFairMap("A1", "A5");
		map.createMap();
		int[] cA1 = map.getCoordinates("A1");
		int[] cA2 = map.getCoordinates("A2");

		Resources res = getResources();
		Bitmap bitmap = BitmapFactory
				.decodeResource(res, R.drawable.map_a);
		
		canvas.setBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, true));

		canvas.drawLine(cA1[0], cA1[1], cA2[0], cA2[1], paint);
	}

}
