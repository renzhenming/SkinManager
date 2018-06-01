package com.rzm.skinmanager;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;

import com.rzm.skinmanager.skin.ISkinChangeListener;
import com.rzm.skinmanager.skin.SkinAttrSupport;
import com.rzm.skinmanager.skin.SkinManager;
import com.rzm.skinmanager.skin.attr.SkinAttr;
import com.rzm.skinmanager.skin.attr.SkinView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by renzhenming on 2018/6/1.
 */

public class BaseSkinActivity extends Activity implements ISkinChangeListener {

    private SkinAppCompatViewInflater mAppCompatViewInflater;
    private static final String TAG = "BaseSkinActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(this);
        //安卓源码，因为Factory只能设置一次，所以要判断一下
        if (inflater.getFactory() == null){
            LayoutInflaterCompat.setFactory2(inflater,this);
        }

        super.onCreate(savedInstanceState);
    }

    /**
     * From {@link LayoutInflater.Factory2}.
     */
    @Override
    public final View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        // If the Factory didn't handle it, let our createView() method try

        //parent 指的是当前这个view的parent,所以最外层布局parent为null
        Log.e(TAG,"parent -> "+parent+"");
        View view = createView(parent, name, context, attrs);
        //一个activity布局对应多个SkinView
        if (view != null){
            Log.e(TAG,view+"");
            //这行代码是为了添加上下文context，没有必要放在这里，只需要设置一次就好，可以放在application中
            SkinManager.getInstance().init(this);
            //获取一个view中的皮肤属性集合
            List<SkinAttr> skinAttrList = SkinAttrSupport.getSkinAttrs(context,attrs);
            //把每个view的属性集合封装进一个SkinView中
            SkinView skinView = new SkinView(view,skinAttrList);
            //交给Manager处理
            manageSkinView(skinView);
        }
        return view;
    }
    /**
     * 统一管理SkinView
     * 这个方法的作用是在activity启动的时候将其中所有需要的属性集合存入map中，这样我们
     * 需要换肤的时候点击换肤按钮就可以拿到所有的需要设置的属性
     * @param skinView
     */
    private void manageSkinView(SkinView skinView) {
        //获取当前activity中SkinView的集合
        List<SkinView> skinViews = SkinManager.getInstance().getSkinViews(this);
        if (skinViews == null){
            skinViews = new ArrayList<>();
            //还没有这个集合，就去注册进去，把这个list加入到Map中（map是一个以activity为key，以Skin View集合为value的总的存放所有activity的集合）
            SkinManager.getInstance().register(skinViews,this);
        }
        //不断的将SkinView添加进去
        skinViews.add(skinView);

        // 如果需要换肤
        if(SkinManager.getInstance().needChangeSkin()){
            SkinManager.getInstance().changeSkin(skinView);
        }
    }

    private View createView(View parent, String name, Context context, AttributeSet attrs) {
        final boolean isPre21 = Build.VERSION.SDK_INT < 21;

        if (mAppCompatViewInflater == null) {
            mAppCompatViewInflater = new SkinAppCompatViewInflater();
        }

        // We only want the View to inherit it's context if we're running pre-v21
        final boolean inheritContext = isPre21 && true
                && shouldInheritContext((ViewParent) parent);

        return mAppCompatViewInflater.createView(parent, name, context, attrs, inheritContext,
                isPre21, /* Only read android:theme pre-L (L+ handles this anyway) */
                true ,/* Read read app:theme as a fallback at all times for legacy reasons */
                true
        );
    }
    private boolean shouldInheritContext(ViewParent parent) {
        if (parent == null) {
            // The initial parent is null so just return false
            return false;
        }
        while (true) {
            if (parent == null) {
                // Bingo. We've hit a view which has a null parent before being terminated from
                // the loop. This is (most probably) because it's the root view in an inflation
                // call, therefore we should inherit. This works as the inflated layout is only
                // added to the hierarchy at the end of the inflate() call.
                return true;
            } else if (parent == getWindow().getDecorView() || !(parent instanceof View)
                    || ViewCompat.isAttachedToWindow((View) parent)) {
                // We have either hit the window's decor view, a parent which isn't a View
                // (i.e. ViewRootImpl), or an attached view, so we know that the original parent
                // is currently added to the view hierarchy. This means that it has not be
                // inflated in the current inflate() call and we should not inherit the context.
                return false;
            }
            parent = parent.getParent();
        }
    }

    @Override
    public void changeSkin(String path) {
        Log.d(TAG,"换肤完成 -> "+path);
    }
}
