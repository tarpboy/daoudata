package com.devcrane.payfun.daou.utility;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.devcrane.payfun.daou.R;

/**
 * Created by jonathan on 2017. 12. 11..
 */

public class CustomAlertDialog extends Dialog {


    private Button mLeftButton;
    private LinearLayout ll_progress;

    private int mDrawable_id;


    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialog_alert_custom);

        ll_progress = (LinearLayout)findViewById(R.id.ll_progress);
        mLeftButton = (Button) findViewById(R.id.btn_left);


        ll_progress.setBackground(getContext().getResources().getDrawable(mDrawable_id));

        // 클릭 이벤트 셋팅
        if (mLeftClickListener != null && mRightClickListener != null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
        } else if (mLeftClickListener != null
                && mRightClickListener == null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
        } else {

        }
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public CustomAlertDialog(Context context, int drawable_id,
                        View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mLeftClickListener = singleListener;
        this.mDrawable_id = drawable_id;

    }





}

