package com.devcrane.payfun.daou.customkeypad;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.utility.BHelper;

public class KeyboardUtil {
    static private KeyboardView keyboardView;
    private Keyboard k;//
    private EditText ed;
    Animation animationShow;
    static Animation animationHide;
    private Context context;
    static private onShowCloseListener showListener;
    static KeyboardUtil me;

    public interface onShowCloseListener {
        public void show();

        public void onPush();

        public void close();
    }

    public KeyboardUtil(View view, Context ctx, EditText edit) {
        this.ed = edit;
        this.context = ctx;
        k = new Keyboard(ctx, R.xml.symbols);
        keyboardView = (KeyboardViewNumber) view.findViewById(R.id.keyboard_view);
        keyboardView.setKeyboard(k);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(true);
        keyboardView.setVisibility(View.GONE);
        keyboardView.setOnKeyboardActionListener(listener);
        animationShow = AnimationUtils.loadAnimation(ctx, R.anim.popup_fade_in);
        animationHide = AnimationUtils.loadAnimation(ctx, R.anim.popup_fade_out);
        me = this;
    }

    public KeyboardUtil(Activity activity, Context ctx, EditText edit) {
        this.ed = edit;
        this.context = ctx;
        k = new Keyboard(ctx, R.xml.symbols);
        keyboardView = (KeyboardViewNumber) activity.findViewById(R.id.keyboard_view);
        keyboardView.setKeyboard(k);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(true);
        keyboardView.setVisibility(View.GONE);
        keyboardView.setOnKeyboardActionListener(listener);
        animationShow = AnimationUtils.loadAnimation(ctx, R.anim.popup_fade_in);
        animationHide = AnimationUtils.loadAnimation(ctx, R.anim.popup_fade_out);
        me = this;

    }
    public KeyboardUtil(Activity activity, Context ctx) {
        this.context = ctx;
        k = new Keyboard(ctx, R.xml.symbols);
        keyboardView = (KeyboardViewNumber) activity.findViewById(R.id.keyboard_view);
        keyboardView.setKeyboard(k);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(true);
        keyboardView.setVisibility(View.GONE);
        keyboardView.setOnKeyboardActionListener(listener);
        animationShow = AnimationUtils.loadAnimation(ctx, R.anim.popup_fade_in);
        animationHide = AnimationUtils.loadAnimation(ctx, R.anim.popup_fade_out);
        me = this;

    }
    public static KeyboardUtil getInstance(Activity activity, Context ctx){
     return    me!=null? me: new KeyboardUtil(activity,ctx);
    }
    public void setShowListener(onShowCloseListener listener) {
        showListener = listener;
    }

    private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }


        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            //Log.i("KEY_BOARD","pressed: "+ (char) primaryCode + "with keyCodes:"+ (char)keyCodes[0]);
            Editable editable = ed.getText();
            int start = ed.getSelectionEnd();
            if (primaryCode == Keyboard.KEYCODE_DELETE) {
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
                if (showListener != null) {
                    showListener.onPush();
                }
            } else if (primaryCode == 4896) {
                editable.clear();
                if (showListener != null) {
                    showListener.onPush();
                }
            }else if (primaryCode == -10 || primaryCode == KeyEvent.KEYCODE_BACK) {
                hideKeyboard();
            }else if (primaryCode == -15) {
                editable.insert(start, "00");
                if (showListener != null) {
                    showListener.onPush();
                }
            }else if (primaryCode == -25) {
                editable.insert(start, "000");
                if (showListener != null) {
                    showListener.onPush();
                }
            }
            else {
                editable.insert(start, Character.toString((char) primaryCode));
                if (showListener != null) {
                    showListener.onPush();
                }
            }

        }
    };

    public static boolean isShow() {
        if(keyboardView ==null)
            return false;
        return keyboardView.getVisibility() == View.VISIBLE;
    }

    public boolean showKeyboard() {
        BHelper.db("showKeyboard 1");
        if (keyboardView.getVisibility() == View.GONE || keyboardView.getVisibility() == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
            if (showListener != null) {
                showListener.show();
            }
            return true;
        } else {
            return false;
        }

    }

    public void startShow() {
        keyboardView.startAnimation(animationShow);
    }

    public void hideKeyboard() {
        if (keyboardView!=null && keyboardView.getVisibility() == View.VISIBLE) {
            keyboardView.startAnimation(animationHide);
            keyboardView.setVisibility(View.GONE);
            if (showListener != null) {
                showListener.close();
            }
        }
    }
    public static void hideSoftKeyboard(EditText editText, Activity at) {
        InputMethodManager imm = (InputMethodManager) at.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
    public static void hideSoftKeyboard(Activity at) {
        // Check if no view has focus:
        View view = at.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) at.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}