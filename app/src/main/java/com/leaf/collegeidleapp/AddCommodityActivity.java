package com.leaf.collegeidleapp;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.leaf.collegeidleapp.bean.Commodity;
import com.leaf.collegeidleapp.util.CommodityDbHelper;

import java.io.ByteArrayOutputStream;

public class AddCommodityActivity extends AppCompatActivity {

    TextView tvStuId;
    ImageButton ivPhoto;
    EditText etTitle, etPrice, etPhone, etDescription;
    Spinner spType;
    private byte[] selectedPictureBytes; // 存储选择的图片二进制数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_commodity);

        // 初始化学号显示
        tvStuId = findViewById(R.id.tv_student_id);
        tvStuId.setText(getIntent().getStringExtra("user_id"));

        // 返回按钮
        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // 图片选择控件
        ivPhoto = findViewById(R.id.iv_photo);
        ivPhoto.setOnClickListener(v -> showImageSelectionDialog());

        // 输入控件初始化
        etTitle = findViewById(R.id.et_title);
        etPrice = findViewById(R.id.et_price);
        etPhone = findViewById(R.id.et_phone);
        etDescription = findViewById(R.id.et_description);
        spType = findViewById(R.id.spn_type);

        // 发布按钮
        Button btnPublish = findViewById(R.id.btn_publish);
        btnPublish.setOnClickListener(v -> {
            if (checkInput()) {
                publishCommodity();
            }
        });
    }

    // 显示图片选择弹窗
    private void showImageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_image_selector, null);
        builder.setView(dialogView);

        ImageView ivImage1 = dialogView.findViewById(R.id.iv_image1);
        ImageView ivImage2 = dialogView.findViewById(R.id.iv_image2);
        ImageView ivImage3 = dialogView.findViewById(R.id.iv_image3);
        ImageView ivImage4 = dialogView.findViewById(R.id.iv_image4);
        ImageView ivImage5 = dialogView.findViewById(R.id.iv_image5);

        AlertDialog dialog = builder.create();
        dialog.show();

        // 图片1点击事件
        ivImage1.setOnClickListener(v -> {
            handleImageSelection(R.drawable.icon_take_photo);
            dialog.dismiss();
        });

        // 图片2点击事件
        ivImage2.setOnClickListener(v -> {
            handleImageSelection(R.drawable.icon_learning_use);
            dialog.dismiss();
        });

        ivImage3.setOnClickListener(v -> {
            handleImageSelection(R.drawable.icon_electric_prodduct);
            dialog.dismiss();
        });

        ivImage4.setOnClickListener(v -> {
            handleImageSelection(R.drawable.icon_sports_good);
            dialog.dismiss();
        });

        ivImage5.setOnClickListener(v -> {
            handleImageSelection(R.drawable.icon_daily_use);
            dialog.dismiss();
        });

    }

    // 处理图片选择
    private void handleImageSelection(int imageResId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResId);
        ivPhoto.setImageBitmap(bitmap);

        // 转换图片为字节数组
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        selectedPictureBytes = stream.toByteArray();
    }

    // 发布商品
    private void publishCommodity() {
        CommodityDbHelper dbHelper = new CommodityDbHelper(
                getApplicationContext(),
                CommodityDbHelper.DB_NAME,
                null,
                1
        );

        Commodity commodity = new Commodity();
        commodity.setTitle(etTitle.getText().toString());
        commodity.setCategory(spType.getSelectedItem().toString());
        commodity.setPrice(Float.parseFloat(etPrice.getText().toString()));
        commodity.setPhone(etPhone.getText().toString());
        commodity.setDescription(etDescription.getText().toString());
        commodity.setStuId(tvStuId.getText().toString());
        commodity.setPicture(selectedPictureBytes); // 使用预置图片数据

        if (dbHelper.AddCommodity(commodity)) {
            Toast.makeText(this, "商品发布成功!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "发布失败!", Toast.LENGTH_SHORT).show();
        }
    }

    // 输入合法性检查
    private boolean checkInput() {
        if (etTitle.getText().toString().trim().isEmpty()) {
            showToast("商品标题不能为空!");
            return false;
        }
        if (etPrice.getText().toString().trim().isEmpty()) {
            showToast("商品价格不能为空!");
            return false;
        }
        if (spType.getSelectedItem().toString().equals("请选择类别")) {
            showToast("请选择商品类别!");
            return false;
        }
        if (etPhone.getText().toString().trim().isEmpty()) {
            showToast("联系方式不能为空!");
            return false;
        }
        if (etDescription.getText().toString().trim().isEmpty()) {
            showToast("商品描述不能为空!");
            return false;
        }
        if (selectedPictureBytes == null) {
            showToast("请选择商品图片!");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}