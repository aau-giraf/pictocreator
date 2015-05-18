/*
 * Copyright 2014 Alex Curran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.amlcurran.showcaseview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

class StandardShowcaseDrawer implements ShowcaseDrawer {

    protected final Paint eraserPaint;
    protected final Drawable showcaseDrawable;
    private final Paint basicPaint;
    // Why final dude! why .-. and why private? this is not a playground for kids, we are adults here?
    private float showcaseRadius;
    private boolean drawInnerCircle = true;
    private boolean drawRect;
    protected int backgroundColour;

    public void setDrawInnerCircle(boolean b)
    {
        drawInnerCircle = b;
    }

    public void setDrawRect(boolean b) { drawRect = b; }

    // - now we gonna need this to "jump over" the proof-kid fence -.-
    public float getShowcaseRadius() {

        return showcaseRadius;
    }

    @Override public void setInnerRadius(float innerRadius) {

    }

    @Override public float getInnerRadius() {

        return 0;
    }

    @Override public void setOuterRadius(float outerRadius) {

    }

    @Override public float getOuterRadius() {

        return 0;
    }

    public void setShowcaseRadius(float showcaseRadius) {

        this.showcaseRadius = showcaseRadius;
    }

    public StandardShowcaseDrawer(Resources resources) {
        PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
        eraserPaint = new Paint();
        eraserPaint.setColor(0xFFFFFF);
        eraserPaint.setAlpha(0);
        eraserPaint.setXfermode(xfermode);
        eraserPaint.setAntiAlias(true);
        basicPaint = new Paint();
        showcaseRadius = resources.getDimension(R.dimen.showcase_radius);
        showcaseDrawable = resources.getDrawable(R.drawable.cling_bleached);
    }

    @Override
    public void setShowcaseColour(int color) {
        showcaseDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void drawShowcase(Bitmap buffer, float x, float y, float scaleMultiplier) {
        Canvas bufferCanvas = new Canvas(buffer);
        if (drawRect) {
            int rectRadius = Math.round(showcaseRadius * 0.67f);
            int xPadding = 10;
            Rect r = new Rect( (int) x - rectRadius - xPadding, (int) y + rectRadius, (int) x + rectRadius + xPadding, (int) y - rectRadius);
            bufferCanvas.drawRect(r, eraserPaint);
        }
        else {
            bufferCanvas.drawCircle(x, y, this.getShowcaseRadius(), eraserPaint);
        }
        int halfW = getShowcaseWidth() / 2;
        int halfH = getShowcaseHeight() / 2;
        int left = (int) (x - halfW);
        int top = (int) (y - halfH);
        showcaseDrawable.setBounds(left, top,
                left + getShowcaseWidth(),
                top + getShowcaseHeight());

        if (drawInnerCircle)
            showcaseDrawable.draw(bufferCanvas);
    }
    
    @Override
    public void drawShowcase(Bitmap buffer, float x, float y, float innerRadius, float outerRadius, float scaleMultiplier ) {
        // pass
    } 

    @Override
    public int getShowcaseWidth() {
        return showcaseDrawable.getIntrinsicWidth();
    }

    @Override
    public int getShowcaseHeight() {
        return showcaseDrawable.getIntrinsicHeight();
    }

    @Override
    public float getBlockedRadius() {
        return showcaseRadius;
    }

    @Override
    public void setBackgroundColour(int backgroundColor) {
        this.backgroundColour = backgroundColor;
    }

    @Override
    public void erase(Bitmap bitmapBuffer) {
        bitmapBuffer.eraseColor(backgroundColour);
    }

    @Override
    public void drawToCanvas(Canvas canvas, Bitmap bitmapBuffer) {
        canvas.drawBitmap(bitmapBuffer, 0, 0, basicPaint);
    }

}
