package com.example.smarttourismapp.ui.common.button;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatButton;

// Primary button
public class PrimaryButton extends AppCompatButton{
    public PrimaryButton(Context context){
        super(context);
        init();
    }

    public PrimaryButton(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public PrimaryButton(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(30f);
        drawable.setColor(Color.parseColor("#003E83"));

        setBackground(drawable);
        setTextColor(Color.WHITE);
        setTextSize(16);
        setPadding(40, 20, 40, 20);
        setAllCaps(false);
    }
}
