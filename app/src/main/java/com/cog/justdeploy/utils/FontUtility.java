package com.cog.justdeploy.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by GP on 17/04/2018
 */
public class FontUtility {
    private final Context context;

    public FontUtility(Context context) {
        this.context = context;
    }

    //Changing Font to Roboto:

    /**
     * function
     */
    public Typeface getBold() {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
    }

    /**
     * function
     */
    public Typeface getLatoBold() {
        return Typeface.createFromAsset(context.getAssets(), "fonts/lato_bold.ttf");
    }

    /**
     * function
     */
    public Typeface getLatoHeavy() {
        return Typeface.createFromAsset(context.getAssets(), "fonts/lato_heavy.ttf");
    }

    /**
     * function
     */
    public Typeface getRegular() {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
    }

    /**
     * function
     */
    public Typeface getMedium() {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
    }

    /**
     * function
     */
    public Typeface getLight() {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
    }

    /**
     * function
     */
    public Typeface getThin() {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
    }

    /**
     * function
     */
    public Typeface getHeavy() {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Black.ttf");
    }

    /**
     * function
     */
    public Typeface getItalic() {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Italic.ttf");
    }

}
