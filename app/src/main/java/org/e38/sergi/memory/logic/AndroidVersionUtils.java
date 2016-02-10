package org.e38.sergi.memory.logic;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * Created by sergi on 2/2/16.
 */
public class AndroidVersionUtils {

    public static Drawable getDrawale(Context context, int id) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            drawable = context.getDrawable(id);
        else drawable = context.getResources().getDrawable(id);
        return drawable;
    }
}

