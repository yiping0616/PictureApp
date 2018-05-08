package com.example.mom.pictureapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.CursorLoader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {   //原本 LoaderCallbacks<Object> 的Object 要改成 Cursor

    private static final int REQUEST_READ_STORAGE = 3;

    SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //外部存取相關權限也是 危險權限 , 需要實作向使用者要求權限
        //權限確認
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);   //檢查權限
        if (permission != PackageManager.PERMISSION_GRANTED) {    //已有權限 = PERMISSION_GRANTED
            //未取得權限，向使用者要求允許權限
            ActivityCompat.requestPermissions( this,
                    new String[]{ READ_EXTERNAL_STORAGE },
                    REQUEST_READ_STORAGE );
        }else{
            //已有權限，可進行檔案存取
            readThumbnails();
        }
    }
    //向使用者要求權限後 , 不管APPLY OR DENY 都會執行 MainActivity 的 onRequestPermissionsResult()
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //取得聯絡人權限 , 進行存取
                    readThumbnails();
                } else {
                    //使用者拒絕權限 , 顯示對話框告知
                    new AlertDialog.Builder(this)
                            .setMessage("必須允許外部存取權限才能顯示圖檔")
                            .setPositiveButton("OK", null)
                            .show();
                }
                return;
        }
    }

    //查詢縮圖 存取外部資源比較耗費系統資源 在查詢和處理結果會影響畫面元件順暢
    //因此在這使用 CursorLoader機制 , 當資料庫中的資料更動時 可設計出自動更新 ListView 或 其他元件上的資料。
    private void readThumbnails() {
        GridView grid = findViewById(R.id.grid);
        String[] from = {MediaStore.Images.Thumbnails.DATA , MediaStore.Images.Media.DISPLAY_NAME};
        int[] to = new int[] { R.id.thumb_image , R.id.thumb_text};
        adapter = new SimpleCursorAdapter(
                getBaseContext() ,
                R.layout.thumb_item ,
                null ,     //要使用 CursorLoader機制  Cursor值先使用null
                from ,
                to ,
                0 );
        grid.setAdapter(adapter);
        getLoaderManager().initLoader(0 , null , this); //第三個參數 要給的是 LoaderManager , 讓MainActivity實作LoaderManager介面

        grid.setOnItemClickListener(this );  //點擊grid其中一個項目後顯示圖檔  項目點擊事件處理
    }

    //在剛建立 Loader 時會自動執行此方法，在此處 (onCreateLoader) 可開始查詢圖檔資料，告訴 Loader 我們想要的
    //在完成時它會以非同步方式 通知另一個 onLoadFinished 方法，三個必需實作的方法
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        return new CursorLoader(this , uri , null , null , null , null);   //產生並回傳資料讀取器(CursorLoader)物件，並將 uri 傳遞給它
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {  //當Loader查詢完成時 會自動呼叫onLoadFinished()
        adapter.swapCursor(data);                       //此時即呼叫 Adapter 的 swapCursor() 替換adapter內的Cursor物件
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    //implement OnItemClickListener 需實作的方法
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this , DetailActivity.class);
        intent.putExtra("POSITION" , position);
        startActivity(intent);
    }
}
