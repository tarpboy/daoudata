package com.devcrane.payfun.daou.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.devcrane.payfun.daou.utility.FormatUtils;


/**
 * Created by ntb on 6/2/16.
 */
public class MoneyEditText extends EditText {

    public MoneyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setTextMoney(getTextDouble());
//        setInputType(InputType.TYPE_CLASS_NUMBER);
        setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) });
        addTextChangedListener(mFormatMoneyTextWatcher);
//        setOnTouchListener(otl);
        //customEdittext();
    }

    private void customEdittext(){
//        ShapeDrawable shape = new ShapeDrawable(new RectShape());
//        shape.getPaint().setColor(Color.GRAY);
//        shape.getPaint().setStyle(Paint.Style.STROKE);
//        shape.getPaint().setStrokeWidth(3);
//        // Assign the created border to EditText widget
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            this.setBackground(shape);
//        }else{
//            this.setBackgroundDrawable(shape);
//        }
        this.setBackgroundColor(Color.TRANSPARENT);
    }
    private OnTouchListener otl = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    EditText edt =((EditText)v);
                    edt.setSelection(edt.getText().length());
                    break;
            }
            return  true;
        }
    };
    private TextWatcher mFormatMoneyTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
        Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
        Runnable workRunnable;

        @Override
        public void afterTextChanged(final Editable s) {

            handler.removeCallbacks(workRunnable);
            workRunnable = new Runnable() {
                @Override
                public void run() {
                    formatText(s);
                }
            };
            handler.postDelayed(workRunnable, 800 /*delay*/);


        }
        void formatText(Editable s){

            final boolean b = s.toString().isEmpty();
            String txtValue = s.toString();
            txtValue = txtValue.replace(",","");
            final double value = b ? 0 : FormatUtils.parseDouble(s.toString());

            removeTextChangedListener(this);
            {
                setTextMoney(value);
                setSelection(getText().length());
            }
            addTextChangedListener(this);
        }
    };

    public void setTextMoney(double value) {
        String txtValue =FormatUtils.formatMoney(value);
        if(txtValue.equals("0"))
            txtValue = "";
        this.setText(txtValue);
    }

    public Double getTextDouble() {
        return FormatUtils.parseDouble(getText().toString());
    }
}
