package com.leaf.collegeidleapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.leaf.collegeidleapp.bean.Collection;
import com.leaf.collegeidleapp.util.MyCollectionDbHelper;

public class CommodityDetailActivity extends Activity {

    // 数据库操作帮助类
    private MyCollectionDbHelper dbHelper;
    // 当前用户学号
    private String currentStuId;
    // 当前商品信息
    private Collection currentCollection;
    // 收藏状态
    private boolean isCollected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_detail);

        // 初始化数据库（参数与MyCollectionDbHelper构造函数一致）
        dbHelper = new MyCollectionDbHelper(
                this,
                "tb_collection",
                null,
                1
        );

        // 从Intent获取数据
        parseIntentData();

        // 初始化界面
        setupViews();

        // 检查初始收藏状态
        checkInitialCollectionStatus();
    }

    // 解析Intent数据
    private void parseIntentData() {
        Intent intent = getIntent();
        currentStuId = intent.getStringExtra("STUDENT_ID");

        currentCollection = new Collection();
        currentCollection.setStuId(currentStuId);
        currentCollection.setPicture(intent.getByteArrayExtra("COMMODITY_IMAGE"));
        currentCollection.setTitle(intent.getStringExtra("COMMODITY_TITLE"));
        currentCollection.setDescription(intent.getStringExtra("COMMODITY_DESCRIPTION"));
        currentCollection.setPrice(intent.getFloatExtra("COMMODITY_PRICE", 0));
        currentCollection.setPhone(intent.getStringExtra("SELLER_PHONE"));
    }

    // 初始化视图
    private void setupViews() {
        // 商品图片
        ImageView ivImage = findViewById(R.id.ivCommodityImage);
        byte[] imageBytes = currentCollection.getPicture();
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(
                    imageBytes,
                    0,
                    imageBytes.length
            );
            ivImage.setImageBitmap(bitmap);
        }

        // 文字信息
        ((TextView) findViewById(R.id.tvTitle)).setText(currentCollection.getTitle());
        ((TextView) findViewById(R.id.tvDescription)).setText(currentCollection.getDescription());
        ((TextView) findViewById(R.id.tvPrice)).setText(
                String.format("¥%.2f", currentCollection.getPrice())
        );
        ((TextView) findViewById(R.id.tvContact)).setText(
                "联系方式：" + currentCollection.getPhone() + "\n" +
                        "发布人学号：" + currentStuId
        );

        // 按钮事件
        setupButtonActions();
    }

    // 设置按钮点击事件
    private void setupButtonActions() {
        // 返回按钮
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 收藏按钮
        ImageButton btnFavorite = findViewById(R.id.btnFavorite);
        btnFavorite.setOnClickListener(v -> toggleCollectionStatus());
    }

    // 检查初始收藏状态
    private void checkInitialCollectionStatus() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return checkIfCollected();
            }

            @Override
            protected void onPostExecute(Boolean result) {
                isCollected = result;
                updateFavoriteButton();
            }
        }.execute();
    }

    // 数据库检查是否已收藏
    private boolean checkIfCollected() {
        for (Collection item : dbHelper.readMyCollections(currentStuId)) {
            if (item.getTitle().equals(currentCollection.getTitle()) &&
                    item.getPrice() == currentCollection.getPrice()) {
                return true;
            }
        }
        return false;
    }

    // 切换收藏状态
    private void toggleCollectionStatus() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                if (isCollected) {
                    // 执行取消收藏
                    dbHelper.deleteMyCollection(
                            currentCollection.getTitle(),
                            currentCollection.getDescription(),
                            currentCollection.getPrice()
                    );
                    return false;
                } else {
                    // 执行添加收藏
                    dbHelper.addMyCollection(currentCollection);
                    return true;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                isCollected = result;
                updateFavoriteButton();
                showStatusToast();
            }
        }.execute();
    }

    // 更新收藏按钮图标
    private void updateFavoriteButton() {
        ImageButton btn = findViewById(R.id.btnFavorite);
        int iconRes = isCollected ?
                android.R.drawable.btn_star_big_on :
                android.R.drawable.btn_star_big_off;
        btn.setImageResource(iconRes);
    }

    // 显示操作状态提示
    private void showStatusToast() {
        String message = isCollected ?
                "已添加到收藏夹" :
                "已从收藏夹移除";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        // 关闭数据库连接
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}