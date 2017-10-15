package com.example.blackwidow;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

/**
 * Created by Ryan on 10/15/2017.
 */

public class AnimationScan extends AnimationDrawable {
    private static final String TAG = "AnimationScan";
    private Context _context;

    /**
     *
     * @param context Context to use to get a resource drawable
     * @param resId Resource drawable background
     */
    public AnimationScan(Context context, int resId) {
        super();
        _context = context;
        Drawable _background = _context.getResources().getDrawable(resId, _context.getTheme());

        final int frames = 110;
        for (int i = 0; i < frames; i+=2) {
            final int temp = i;
            if (i == 0) {
                this.addFrame(_background, 4000);
            }
            else {
                ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
                    @Override
                    public Shader resize(int width, int height) {
                        return new LinearGradient(width/2, 0, width/2, height,
                                new int[]{0x00FF0000, 0xFFFFDBDB, 0x00FF0000},
                                new float[]{0, (float)temp / 100, 1},  // start, center and end position
                                Shader.TileMode.CLAMP);
                    }
                };
                PaintDrawable pd = new PaintDrawable();
                pd.setShape(new RectShape());
                pd.setShaderFactory(sf);
                pd.setCornerRadius(_context.getResources().getDimension(R.dimen.btnScanRadius));
                this.addFrame(new LayerDrawable(new Drawable[] {_background, pd}), 20);
            }
        }
    }
}
