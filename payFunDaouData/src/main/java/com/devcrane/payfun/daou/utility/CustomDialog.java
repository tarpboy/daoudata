package com.devcrane.payfun.daou.utility;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devcrane.payfun.daou.R;

/**
 * Created by jonathan on 2017. 12. 11..
 */

public class CustomDialog extends Dialog {


    LinearLayout ll_progress;
    int mDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialog_custom);


        ll_progress = (LinearLayout)findViewById(R.id.ll_progress);

        ll_progress.setBackground(getContext().getResources().getDrawable(mDrawable));





//        setLayout();
//        setTitle(mTitle);
//        setContent(mContent);
//        setClickListener(mLeftClickListener , mRightClickListener);
    }



    public CustomDialog(Context context, int drawable) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 지저분한(?) 다이얼 로그 제목을 날림
        mDrawable = drawable;

    }



    public void changeDrawable(int drawable)
    {
        ll_progress.setBackground(getContext().getResources().getDrawable(drawable));

    }






}

