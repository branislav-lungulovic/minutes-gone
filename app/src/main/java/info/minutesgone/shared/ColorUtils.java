package info.minutesgone.shared;

import android.graphics.Color;

public class ColorUtils {

    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r,255),
                Math.min(g,255),
                Math.min(b,255));
    }

    public static int getColorFromGradient(int[] colors, float[] positions, float v ){

        if( colors.length == 0 || colors.length != positions.length ){
            throw new IllegalArgumentException();
        }

        if( colors.length == 1 ){
            return colors[0];
        }

        if( v <= positions[0]) {
            return colors[0];
        }

        if( v >= positions[positions.length-1]) {
            return colors[positions.length-1];
        }

        for( int i = 1; i < positions.length; ++i ){
            if( v <= positions[i] ){
                float t = (v - positions[i-1]) / (positions[i] - positions[i-1]);
                return lerpColor(colors[i-1], colors[i], t);
            }
        }

        //should never make it here
        throw new RuntimeException();
    }

    private static int lerpColor(int colorA, int colorB, float t){
        int alpha = (int)Math.floor(Color.alpha(colorA) * ( 1 - t ) + Color.alpha(colorB) * t);
        int red   = (int)Math.floor(Color.red(colorA)   * ( 1 - t ) + Color.red(colorB)   * t);
        int green = (int)Math.floor(Color.green(colorA) * ( 1 - t ) + Color.green(colorB) * t);
        int blue  = (int)Math.floor(Color.blue(colorA)  * ( 1 - t ) + Color.blue(colorB)  * t);

        return Color.argb(alpha, red, green, blue);
    }

}
