package com.leaf.collegeidleapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leaf.collegeidleapp.adapter.MyCollectionAdapter;
import com.leaf.collegeidleapp.bean.Collection;
import com.leaf.collegeidleapp.util.MyCollectionDbHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * 我的收藏Activity类
 * @author autumn_leaf
 */
public class MyCollectionActivity extends AppCompatActivity {

    private static final int PAYMENT_REQUEST_CODE =1001 ;
    ListView lvMyCollection;
    List<Collection> myCollections = new ArrayList<>();

    TextView tvTotalPrice;
    TextView tvStuId;

    MyCollectionDbHelper dbHelper;
    //CommodityDbHelper commodityDbHelper;
    MyCollectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);

        tvTotalPrice = findViewById(R.id.tv_total_price);
        Button btnPay = findViewById(R.id.btn_pay);

        //返回
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvStuId = findViewById(R.id.tv_stuId);
        tvStuId.setText(this.getIntent().getStringExtra("stuId"));
        lvMyCollection = findViewById(R.id.lv_my_collection);
        dbHelper = new MyCollectionDbHelper(getApplicationContext(),MyCollectionDbHelper.DB_NAME,null,1);
        myCollections = dbHelper.readMyCollections(tvStuId.getText().toString());
        adapter = new MyCollectionAdapter(getApplicationContext());
        adapter.setData(myCollections);
        lvMyCollection.setAdapter(adapter);
        // 第五步：加载数据（关键修正点）
        refreshDataAndCalculateTotal(); // 现在所有依赖项已初始化


        //设置长按删除事件
        lvMyCollection.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyCollectionActivity.this);
                builder.setTitle("提示:").setMessage("确定删除此收藏商品吗?").setIcon(R.drawable.icon_user).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Collection collection = (Collection) adapter.getItem(position);
                        //删除收藏商品项
                        dbHelper.deleteMyCollection(collection.getTitle(),collection.getDescription(),collection.getPrice());
                        Toast.makeText(MyCollectionActivity.this,"删除成功!",Toast.LENGTH_SHORT).show();
                    }
                }).show();
                return false;
            }
        });
        //页面刷新

        TextView tvRefresh = findViewById(R.id.tv_refresh);
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCollections = dbHelper.readMyCollections(tvStuId.getText().toString());
                adapter.setData(myCollections);
                lvMyCollection.setAdapter(adapter);
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float total = calculateTotal();
                if (total > 0) {
                    String formattedAmount = String.format(Locale.US, "%.2f", total);
                    Intent intent = new Intent(MyCollectionActivity.this, PaymentActivity.class);
                    intent.putExtra("total_amount", formattedAmount);
                    startActivityForResult(intent, PAYMENT_REQUEST_CODE);
                } else {
                    Toast.makeText(MyCollectionActivity.this, "当前没有需要支付的商品", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // 新增方法：刷新数据并计算总价
    private void refreshDataAndCalculateTotal() {
        // 刷新数据
        myCollections = dbHelper.readMyCollections(tvStuId.getText().toString());
        adapter.setData(myCollections);
        lvMyCollection.setAdapter(adapter);

        // 计算并显示总价
        float total = 0;
        for(Collection item : myCollections) {
            total += item.getPrice(); // 直接使用float类型价格
        }
        tvTotalPrice.setText(String.format("总价：￥%.2f", total));
    }

    // 新增方法：计算总价
    private float calculateTotal() {
        float total = 0;
        for (Collection item : myCollections) {
            total += item.getPrice(); // 直接使用float类型价格
        }
        return total;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                boolean success = data.getBooleanExtra("payment_success", false);
                if (success) {
                    // 删除所有当前收藏的商品
                    for (Collection collection : myCollections) {
                        dbHelper.deleteMyCollection(
                                collection.getTitle(),
                                collection.getDescription(),
                                collection.getPrice()
                        );
                    }
                    // 刷新数据
                    refreshDataAndCalculateTotal();
                    Toast.makeText(this, "支付成功，已删除已购商品", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
