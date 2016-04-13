package com.huxq17.example.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by huxq17 on 2016/4/11.
 */
public class BaseFragment extends Fragment {
    private Base mBase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBase = Base.getInstance(getActivity().getApplicationContext());
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
