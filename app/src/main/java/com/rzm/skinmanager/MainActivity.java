package com.rzm.skinmanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.rzm.skinmanager.skin.SkinManager;

import java.io.File;

public class MainActivity extends BaseSkinActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},111);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length<1){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},111);
        }
    }

    public void skin(View view){
        // 从服务器上下载
        String skinPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator +"skin.skin";

        // 换肤
        int result = SkinManager.getInstance().loadSkin(skinPath);
    }

    public void skin1(View view){
        // 恢复默认
        SkinManager.getInstance().restoreDefault();
    }


    public void skin2(View view){
        // 跳转
        startActivity(new Intent(getApplicationContext(),NextActivity.class));
    }
}
