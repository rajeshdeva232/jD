package com.cog.justdeploy.utils

import android.content.res.AssetManager
import android.graphics.Typeface
import android.util.Log
import java.util.HashMap

/**
 * Created by test on 26/4/18.
 */
object TypefaceUtils {

    private val sCachedFonts = HashMap<String, Typeface>()
    private val sCachedSpans = HashMap<Typeface, CalligraphyTypefaceSpan>()

    /**
     * A helper loading a custom font.
     *
     * @param assetManager App's asset manager.
     * @param filePath     The path of the file.
     * @return Return [Typeface] or null if the path is invalid.
     */
    fun load(assetManager: AssetManager, filePath: String): Typeface? {
        synchronized(sCachedFonts) {
            try {
                if (!sCachedFonts.containsKey(filePath)) {
                    val typeface = Typeface.createFromAsset(assetManager, filePath)
                    sCachedFonts[filePath] = typeface
                }
            } catch (error: Exception) {
                Log.w("Calligraphy", "Can't create asset from $filePath. Make sure you have passed in the correct path and file name.", error)
            }
            return sCachedFonts[filePath]
        }
    }

    /**
     * A helper loading custom spans so we don't have to keep creating hundreds of spans.
     *
     * @param typeface not null typeface
     * @return will return null of typeface passed in is null.
     */
    fun getSpan(typeface: Typeface?): CalligraphyTypefaceSpan? {
        if (typeface == null) return null
        synchronized(sCachedSpans) {
            if (!sCachedSpans.containsKey(typeface)) {
                val span = CalligraphyTypefaceSpan(typeface)
                sCachedSpans[typeface] = span
            }
            return sCachedSpans[typeface]
        }
    }

    /**
     * Is the passed in typeface one of ours?
     *
     * @param typeface nullable, the typeface to check if ours.
     * @return true if we have loaded it false otherwise.
     */
    fun isLoaded(typeface: Typeface?): Boolean {
        return typeface != null && sCachedFonts.containsValue(typeface)
    }
}
