package com.decoration.appshore.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangyu on 26/11/15.
 */

public class FlowingView extends View implements View.OnClickListener, View.OnLongClickListener {

    private int index;

    private Bitmap imageBmp;

    private String imageFilePath;

    private int width;

    private int height;


    private Paint paint;

    private Rect rect;

    private int footHeight;

    public FlowingView(Context context, int index, int width) {
        super(context);
        this.index = index;
        this.width = width;
        init();
    }

    private void init() {
        setOnClickListener(this);
        setOnLongClickListener(this);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(Color.WHITE);
        if (imageBmp != null && rect != null) {
            canvas.drawBitmap(imageBmp, null, rect, paint);
        }
        super.onDraw(canvas);
    }

    public void loadImage() {
        InputStream inStream = null;
        try {
            inStream = getContext().getAssets().open(imageFilePath); // TODO url download
            imageBmp = BitmapFactory.decodeStream(inStream);
            inStream.close();
            inStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (imageBmp != null) {
            int bmpWidth = imageBmp.getWidth();
            int bmpHeight = imageBmp.getHeight();
            height = (int) (bmpHeight * width / bmpWidth);
            rect = new Rect(0, 0, width, height);
        }
    }

    public void reload() {
        if (imageBmp == null) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    InputStream inStream = null;
                    try {
                        inStream = getContext().getAssets().open(imageFilePath); // TODO url download
                        imageBmp = BitmapFactory.decodeStream(inStream);
                        inStream.close();
                        inStream = null;
                        postInvalidate();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void recycle() {
        if (imageBmp == null || imageBmp.isRecycled())
            return;
        new Thread(new Runnable() {

            @Override
            public void run() {
                imageBmp.recycle();
                imageBmp = null;
                postInvalidate();
            }
        }).start();
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(getContext(), "long click : " + index, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getContext(), "click : " + index, Toast.LENGTH_SHORT).show();
    }


    public int getViewHeight() {
        return height;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }

    public Bitmap getImageBmp() {
        return imageBmp;
    }

    public void setImageBmp(Bitmap imageBmp) {
        this.imageBmp = imageBmp;
    }

    public int getFootHeight() {
        return footHeight;
    }

    public void setFootHeight(int footHeight) {
        this.footHeight = footHeight;
    }
}
