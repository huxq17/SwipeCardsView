/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huxq17.example.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.huxq17.example.R;


public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setBackground(int resid) {
        getWindow().setBackgroundDrawableResource(resid);
    }

    public void setBackground(Drawable drawable) {
        getWindow().setBackgroundDrawable(drawable);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getWindow().setBackgroundDrawable(null);
    }

    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    protected int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight();
    }

    protected int getScreenWidth() {
        return findViewById(android.R.id.content).getWidth();
    }

    private Base mBase;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        mBase = Base.getInstance(getApplicationContext());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view != null && mBase != null && mBase.isHideInput(view, ev)) {
                mBase.HideSoftInput(view.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 格式化字符串
     *
     * @param format
     * @param args
     */
    public String format(String format, Object... args) {
        if (mBase != null) {
            return mBase.format(format, args);
        } else {
            return null;
        }
    }

    public void setText(Object obj, String str) {
        if (mBase != null) {
            mBase.setText(obj, str);
        }
    }

    /**
     * 获取edittext，textView,checkbox和button的文字
     *
     * @param obj
     * @return
     */
    public String getText(Object obj) {
        if (mBase != null) {
            return mBase.getText(obj);
        } else {
            return "";
        }
    }

    public boolean isEmpty(Object obj) {
        if (mBase != null) {
            return mBase.isEmpty(obj);
        } else {
            return true;
        }
    }

    public boolean isEmpty(String str) {
        return mBase != null ? mBase.isEmpty(str) : true;
    }

    public void toast(String msg) {
        if (mBase != null) {
            mBase.toast(msg);
        }
    }

    public void toastAll(String msg) {
        if (mBase != null) {
            toastAll(msg);
        }
    }

    public void toastL(String msg) {
        if (mBase != null) {
            mBase.toastL(msg);
        }
    }

    public void toastAllL(String msg) {
        if (mBase != null) {
            mBase.toastAllL(msg);
        }
    }
}
