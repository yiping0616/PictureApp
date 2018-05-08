package com.example.mom.pictureapp;

import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

public class DetailActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{

    //由 區域變數 提昇為 屬性
    private int position;
    private ImageView image;
    private Cursor cursor;

    GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detector = new GestureDetector(this , this);

        position = getIntent().getIntExtra("POSITION" , 0);  //取得由MainActivity所傳遞的點擊項目位置
        image = findViewById(R.id.imageView);   //取得畫面的ImageView物件
        CursorLoader loader = new CursorLoader(this ,   //產生CursorLoader物件 並提供他查詢的位置與條件
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null , null , null ,null);
        cursor = loader.loadInBackground();   //loader.loadInBackground() 要求以背景方法查詢 查詢到的結果儲存在cursor物件
        cursor.moveToPosition(position);
        updateImage();
    }
    //更新圖檔的 updateImage()
    private void updateImage(){
        String imagePath = cursor.getString( cursor.getColumnIndex(MediaStore.Images.Media.DATA)); //取得查詢結果中原圖的儲存路徑
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);  //BitmapFactory.decodeFile() 讀取路徑中的圖檔 並換為Bitmap 物件
        image.setImageBitmap(bitmap);   //畫面中的ImageView 以顯示 Bitmap物件
    }


    //implement GestureDetector.OnGestureListener 介面 可協助判斷 Motion event(動作事件 輕按長按滑動..)
    //實作GestureDetector.OnGestureListener 介面必要的六個方法
    @Override
    public boolean onDown(MotionEvent e) {  //在畫面中按下時
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) { //輕觸螢幕當未放開時

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) { //輕觸螢幕放開時
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {  //使用者按下後 移動時
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {  //長觸螢幕時 大概是2sec

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {  //使用者快速滑動螢幕後放開時   e1滑動起始點 e1.getX() e1.getY()可取得座標XY值  e2滑動結束點  velocityX 橫向速度 velocityY 直向速度
        float distance = e2.getX() - e1.getX();  //左右滑動之距離 假設移動100點才算滑動 正的：由右向左 負的：由左向右
        if(distance > 100){
            //由左向右  往前一張圖
            if(!cursor.moveToPrevious()){   //判斷前一筆是否有資料 若無資料代表已是第一筆 直接跳到最後一筆
                cursor.moveToLast();
            }
            updateImage();
        }
        else if(distance <-100){
            //由右向左  往後一張圖
            if(!cursor.moveToNext()){     //判斷下一筆是否有資料 若無資料代表已是最後一筆 直接跳到第一筆
                cursor.moveToFirst();
            }
            updateImage();
        }
        return false;
    }

    //因為會自動執行 Activity的 onTouchEvent() , 因此需要覆寫原本沒什麼用的 onTouchEvent() , return 內容改由 GestureDetector處理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);      //覆寫之後 使用者在螢幕上的操作 全交給 GestureDetector物件處理
    }
}
