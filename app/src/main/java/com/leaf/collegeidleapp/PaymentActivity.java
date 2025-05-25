package com.leaf.collegeidleapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

public class PaymentActivity extends AppCompatActivity {

    private BigDecimal totalAmount;
    private int selectedPayment = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // 初始化基础组件
        TextView tvBack = findViewById(R.id.tv_back);
        Button btnConfirm = findViewById(R.id.btn_confirm);
        TextView tvAmount = findViewById(R.id.tv_amount);

        // 返回按钮
        tvBack.setOnClickListener(v -> finish());

        // 处理金额显示
        try {
            String amountStr = getIntent().getStringExtra("total_amount");
            totalAmount = new BigDecimal(amountStr);
            tvAmount.setText("￥" + totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
        } catch (Exception e) {
            Toast.makeText(this, "金额错误", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 支付方式选择
        setupPaymentOptions();

        // 确认支付
        btnConfirm.setOnClickListener(v -> {
            if (selectedPayment == -1) {
                Toast.makeText(this, "请选择支付方式", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(this)
                    .setTitle("确认支付")
                    .setMessage("支付金额：￥" + totalAmount)
                    .setPositiveButton("确定", (d, w) -> completePayment())
                    .setNegativeButton("取消", null)
                    .show();
        });
    }

    private void setupPaymentOptions() {
        int[] paymentIds = {R.id.layout_alipay, R.id.layout_wechat, R.id.layout_bank};
        for (int id : paymentIds) {
            findViewById(id).setOnClickListener(v -> {
                selectedPayment = getPaymentIndex(id);
                updateSelectionUI(id);
            });
        }
    }

    private int getPaymentIndex(int viewId) {
        if (viewId == R.id.layout_alipay) return 0;
        if (viewId == R.id.layout_wechat) return 1;
        return 2;
    }

    private void updateSelectionUI(int selectedId) {
        int[] allIds = {R.id.layout_alipay, R.id.layout_wechat, R.id.layout_bank};
        for (int id : allIds) {
            findViewById(id).setBackgroundColor(
                    id == selectedId ? 0xFFE3F2FD : Color.TRANSPARENT
            );
        }
    }

    private void completePayment() {
        // 设置返回结果
        Intent result = new Intent();
        result.putExtra("payment_success", true);
        setResult(RESULT_OK, result);

        // 显示支付成功提示
        Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();

        // 关闭当前页面
        finish();
    }

    // PaymentActivity.java
    private void handleAmountData() {
        try {
            // 使用统一的键名获取参数
            String amountStr = getIntent().getStringExtra("total_amount");
            if (amountStr == null || amountStr.isEmpty()) {
                throw new IllegalArgumentException("未接收到金额参数");
            }

            // 直接解析字符串
            totalAmount = new BigDecimal(amountStr)
                    .setScale(2, BigDecimal.ROUND_HALF_UP);

            // 更新金额显示
            TextView tvAmount = findViewById(R.id.tv_amount);
            tvAmount.setText("￥" + totalAmount);

        } catch (Exception e) {
            Toast.makeText(this, "金额错误", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}