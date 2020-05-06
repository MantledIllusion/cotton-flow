package com.mantledillusion.vaadin.cotton.component.css;

import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * A {@link CssProperty} that contains color values.
 */
public class CssColorProperty extends AbstractCssProperty {

    CssColorProperty(String stylePropertyName) {
        super(stylePropertyName);
    }

    public CssStyle ofRGB(int r, int g, int b) {
        return ofRGB(r, g, b, 1);
    }

    public CssStyle ofRGB(int r, int g, int b, double a) {
        if (NumberUtils.min(r, g, b, a) < 0 || NumberUtils.max(r, g, b) > 255 || a > 1) {
            throw new Http901IllegalArgumentException("Cannot create color style with values out of range 0<=RGB<=1 or 0<=A<=1");
        }
        return CssStyle.of(getStylePropertyName(), a == 1 ? "rgb("+r+","+g+","+b+")" : "rgba("+r+","+g+","+b+","+a+")");
    }

    public CssStyle ofHSL(double h, double s, double l) {
        return ofHSL(h, s, l, 1);
    }

    public CssStyle ofHSL(double h, double s, double l, double a) {
        if (NumberUtils.min(h, s, l, a) < 0 || NumberUtils.max(h, s, l, a) > 1) {
            throw new Http901IllegalArgumentException("Cannot create color style with values out of range 0<=HSBL<=1");
        }
        return CssStyle.of(getStylePropertyName(), a == 1 ? "hsl("+h+","+s+","+l+")" : "hsla("+h+","+s+","+l+","+a+")");
    }

    public CssStyle ofHSB(double h, double s, double b) {
        return ofHSB(h, s, b, 1);
    }

    public CssStyle ofHSB(double h, double s, double b, double a) {
        if (NumberUtils.min(h, s, b, a) < 0 || NumberUtils.max(h, s, b, a) > 1) {
            throw new Http901IllegalArgumentException("Cannot create color style with values out of range 0<=HSBA<=1");
        }

        if (s == 0.0D) {
            return ofRGB((int) Math.round(b * 255), (int) Math.round(b * 255), (int) Math.round(b * 255));
        } else {
            double var_h = h * 6.0D;
            if (var_h == 6.0D) {
                var_h = 0.0D;
            }

            double var_i = Math.floor(var_h);
            double var_1 = b * (1.0D - s);
            double var_2 = b * (1.0D - s * (var_h - var_i));
            double var_3 = b * (1.0D - s * (1.0D - (var_h - var_i)));
            double var_r;
            double var_g;
            double var_b;
            if (var_i == 0.0D) {
                var_r = b;
                var_g = var_3;
                var_b = var_1;
            } else if (var_i == 1.0D) {
                var_r = var_2;
                var_g = b;
                var_b = var_1;
            } else if (var_i == 2.0D) {
                var_r = var_1;
                var_g = b;
                var_b = var_3;
            } else if (var_i == 3.0D) {
                var_r = var_1;
                var_g = var_2;
                var_b = b;
            } else if (var_i == 4.0D) {
                var_r = var_3;
                var_g = var_1;
                var_b = b;
            } else {
                var_r = b;
                var_g = var_1;
                var_b = var_2;
            }

            return ofRGB((int) Math.round(var_r * 255), (int) Math.round(var_g * 255), (int) Math.round(var_b * 255), a);
        }
    }
}
