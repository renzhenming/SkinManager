package com.rzm.skinmanager.skin;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


/**
 * Created by rzm on 2017/9/2.
 */

public class SkinPreUtils {

    private static final String TAG = "SkinPreUtils";
    private static final String SKIN_PATH = "skin_path";
    private static final String NEED_CHANGE = "need_change";

    static {
        mSkinPreUtils = new SkinPreUtils();
    }
    private static SkinPreUtils mSkinPreUtils;
    private static SharedPreferences mPreferences;

    private SkinPreUtils(){}

    public static SkinPreUtils getInstance(Context context) {
        if (mPreferences == null){
            mPreferences = context.getSharedPreferences("skin_info", Context.MODE_PRIVATE);
        }
        return mSkinPreUtils;
    }

    public String getSkinPath() {
        String path = mPreferences.getString(SKIN_PATH,null);
        Log.d(TAG,"返回皮肤路径:"+path);
        return path;
    }

    public void clearSkinInfo() {
        Log.d(TAG,"清除皮肤信息");
        mPreferences.edit().remove(SKIN_PATH).commit();
        mPreferences.edit().putBoolean(NEED_CHANGE,false).commit();
    }

    public void saveSkinPath(String path) {
        Log.d(TAG,"保存皮肤路径 -> "+path);
        mPreferences.edit().putString(SKIN_PATH,path).commit();
        mPreferences.edit().putBoolean(NEED_CHANGE,true).commit();
    }

    public boolean needChangeSkin() {
        return mPreferences.getBoolean(NEED_CHANGE,false);
    }
}
