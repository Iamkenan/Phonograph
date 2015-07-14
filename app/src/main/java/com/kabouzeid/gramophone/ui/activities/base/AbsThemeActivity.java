package com.kabouzeid.gramophone.ui.activities.base;

import android.app.ActivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.afollestad.materialdialogs.ThemeSingleton;
import com.kabouzeid.gramophone.R;
import com.kabouzeid.gramophone.interfaces.KabViewsDisableAble;
import com.kabouzeid.gramophone.util.ColorUtil;
import com.kabouzeid.gramophone.util.PreferenceUtil;
import com.kabouzeid.gramophone.util.Util;

/**
 * @author Aidan Follestad (afollestad), Karim Abou Zeid (kabouzeid)
 */

public abstract class AbsThemeActivity extends AppCompatActivity implements KabViewsDisableAble {
    private int colorPrimary;
    private int colorPrimaryDarker;
    private int colorAccent;
    private boolean darkTheme;

    @Nullable
    private ActivityManager.TaskDescription taskDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceUtil.getInstance(this).getGeneralTheme());
        super.onCreate(savedInstanceState);
        setupTheme();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recreateIfThemeChanged();
    }

    private void setupTheme() {
        colorPrimary = PreferenceUtil.getInstance(this).getThemeColorPrimary(this);
        colorPrimaryDarker = ColorUtil.shiftColorDown(colorPrimary);
        colorAccent = PreferenceUtil.getInstance(this).getThemeColorAccent(this);
        darkTheme = PreferenceUtil.getInstance(this).getGeneralTheme() == R.style.Theme_MaterialMusic;

        ThemeSingleton.get().positiveColor = colorAccent;
        ThemeSingleton.get().negativeColor = colorAccent;
        ThemeSingleton.get().neutralColor = colorAccent;
        ThemeSingleton.get().widgetColor = colorAccent;
        ThemeSingleton.get().darkTheme = darkTheme;

        if (!overridesTaskColor()) {
            notifyTaskColorChange(getThemeColorPrimary());
        }
    }

    protected void recreateIfThemeChanged() {
        if (didThemeChanged()) {
            recreate();
        }
    }

    private boolean didThemeChanged() {
        return colorPrimary != PreferenceUtil.getInstance(this).getThemeColorPrimary(this) ||
                colorAccent != PreferenceUtil.getInstance(this).getThemeColorAccent(this) ||
                darkTheme != (PreferenceUtil.getInstance(this).getGeneralTheme() == R.style.Theme_MaterialMusic);
    }

    protected void notifyTaskColorChange(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Sets color of entry in the system recents page
            if (taskDescription == null || taskDescription.getPrimaryColor() != color) {
                taskDescription = new ActivityManager.TaskDescription(
                        null,
                        null,
                        color);
                setTaskDescription(taskDescription);
            }
        }
    }

    public int getThemeColorPrimary() {
        return colorPrimary;
    }

    public int getThemeColorPrimaryDarker() {
        return colorPrimaryDarker;
    }

    public int getThemeColorAccent() {
        return colorAccent;
    }

    protected void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            Util.setAllowDrawUnderStatusBar(getWindow());
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            Util.setStatusBarTranslucent(getWindow(), true);
    }

    protected final void setNavigationBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setNavigationBarColor(ColorUtil.shiftColorDown(color));
    }

    protected final void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(ColorUtil.shiftColorDown(color));
        // also do this on Lollipop in case the user modified the statusbar height
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final View statusBar = getWindow().getDecorView().getRootView().findViewById(R.id.status_bar);
            if (statusBar != null) statusBar.setBackgroundColor(color);
        }
    }

    protected final void setNavigationBarThemeColor() {
        setNavigationBarColor(colorPrimary);
    }

    protected final void setStatusBarThemeColor() {
        setStatusBarColor(colorPrimary);
    }

    protected final void resetNavigationBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setNavigationBarColor(ColorUtil.resolveColor(this, android.R.attr.navigationBarColor));
    }

    protected final void resetStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setStatusBarColor(ColorUtil.resolveColor(this, android.R.attr.statusBarColor));
    }

    protected boolean overridesTaskColor() {
        return false;
    }
}