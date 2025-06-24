package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;

import com.example.tuan17.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//         Chỉ dùng trong quá trình phát triển
//        deleteDatabase("banhang.db"); // <-- Xóa database cũ
//
//        DatabaseHelper dbHelper = new DatabaseHelper(this);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
        setContentView(R.layout.activity_main);
        // Tạo Handler để chuyển Activity sau 10 giây
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Chuyển sang Activity2
                Intent intent = new Intent(MainActivity.this,Login_Activity.class);
                startActivity(intent);
                finish(); // Kết thúc Activity1 nếu không muốn quay lại
            }
        }, 100); // 1000 milliseconds = 1 seconds

    }
}