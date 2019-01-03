package com.zsc.edu.zscweather.fragment_content;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.WindowInsets;

public class WindowInsetsDrawerLayout extends DrawerLayout {
    public WindowInsetsDrawerLayout(@NonNull Context context) {
        super(context);
    }

    public WindowInsetsDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WindowInsetsDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        int cnt = getChildCount();
        for(int i = 0;i < cnt;i++){
            getChildAt(i).dispatchApplyWindowInsets(insets);
        }
        return super.onApplyWindowInsets(insets);
    }
}
