package com.leaf.collegeidleapp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.leaf.collegeidleapp.adapter.AllCommodityAdapter;
import com.leaf.collegeidleapp.bean.Commodity;
import com.leaf.collegeidleapp.util.CommodityDbHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 主界面活动类
 * @author: autumn_leaf
 */
public class MainActivity extends AppCompatActivity {

    ListView lvAllCommodity;
    List<Commodity> allCommodities = new ArrayList<>();
    ImageButton ibLearning,ibElectronic,ibDaily,ibSports;

    CommodityDbHelper dbHelper;
    AllCommodityAdapter adapter;
    private ImageButton ibMyCollection;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvAllCommodity = findViewById(R.id.lv_all_commodity);
        dbHelper = new CommodityDbHelper(getApplicationContext(), CommodityDbHelper.DB_NAME, null, 1);
        adapter = new AllCommodityAdapter(getApplicationContext());
        allCommodities = dbHelper.readAllCommodities();
        adapter.setData(allCommodities);
        lvAllCommodity.setAdapter(adapter);
        final Bundle bundle = this.getIntent().getExtras();
        final TextView tvStuNumber = findViewById(R.id.tv_student_number);
        String str = "";
        if (bundle != null) {
            str = "欢迎" + bundle.getString("username") + ",你好!";
        }
        tvStuNumber.setText(str);
        //当前登录的学生账号
        final String stuNum = tvStuNumber.getText().toString().substring(2, tvStuNumber.getText().length() - 4);
        ImageButton IbAddProduct = findViewById(R.id.ib_add_product);
        //跳转到添加物品界面
        IbAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCommodityActivity.class);
                if (bundle != null) {
                    //获取学生学号
                    bundle.putString("user_id", stuNum);
                    intent.putExtras(bundle);
                }
                startActivity(intent);
            }
        });
        ImageButton IbPersonalCenter = findViewById(R.id.ib_personal_center);
        //跳转到个人中心界面
        IbPersonalCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PersonalCenterActivity.class);
                if (bundle != null) {
                    //获取学生学号
                    bundle.putString("username1", stuNum);
                    intent.putExtras(bundle);
                }
                startActivity(intent);
            }
        });
        //刷新界面
        TextView tvRefresh = findViewById(R.id.tv_refresh);
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allCommodities = dbHelper.readAllCommodities();
                adapter.setData(allCommodities);
                lvAllCommodity.setAdapter(adapter);
            }
        });
        //为每一个item设置点击事件
        lvAllCommodity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Commodity commodity = (Commodity) lvAllCommodity.getAdapter().getItem(position);
                Intent intent = new Intent(MainActivity.this, CommodityDetailActivity.class);

                // 直接传递对象需要序列化，推荐方式是用 Parcelable 传递整个对象
                // 这里改为更规范的参数传递方式
                intent.putExtra("COMMODITY_TITLE", commodity.getTitle());
                intent.putExtra("COMMODITY_DESCRIPTION", commodity.getDescription());
                intent.putExtra("COMMODITY_PRICE", commodity.getPrice());
                intent.putExtra("SELLER_PHONE", commodity.getPhone());
                intent.putExtra("STUDENT_ID", stuNum);

                // 如果是图片字节数组，建议改用图片URL传递（实际项目推荐）
                // 如果必须用字节数组，确保数据量合理
                if (commodity.getPicture() != null) {
                    intent.putExtra("COMMODITY_IMAGE", commodity.getPicture());
                }

                startActivity(intent);
            }
        });
        //点击不同的类别,显示不同的商品信息
        ibLearning = findViewById(R.id.ib_learning_use);
        ibElectronic = findViewById(R.id.ib_electric_product);
        ibDaily = findViewById(R.id.ib_daily_use);
        ibSports = findViewById(R.id.ib_sports_good);
        final Bundle bundle2 = new Bundle();
        //学习用品
        ibLearning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle2.putInt("status",1);
                Intent intent = new Intent(MainActivity.this,CommodityTypeActivity.class);
                intent.putExtras(bundle2);
                startActivity(intent);
            }
        });
        //电子用品
        ibElectronic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle2.putInt("status",2);
                Intent intent = new Intent(MainActivity.this,CommodityTypeActivity.class);
                intent.putExtras(bundle2);
                startActivity(intent);
            }
        });
        //生活用品
        ibDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle2.putInt("status",3);
                Intent intent = new Intent(MainActivity.this,CommodityTypeActivity.class);
                intent.putExtras(bundle2);
                startActivity(intent);
            }
        });
        //体育用品
        ibSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle2.putInt("status",4);
                Intent intent = new Intent(MainActivity.this,CommodityTypeActivity.class);
                intent.putExtras(bundle2);
                startActivity(intent);
            }
        });

        // 新增我的收藏按钮初始化
        ibMyCollection = findViewById(R.id.ib_my_collection);
        ibMyCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyCollectionActivity.class);
                if (bundle != null) {
                    // 使用更安全的方式传递参数
                    intent.putExtra("stuId", stuNum);
                }
                startActivity(intent);
            }
        });
    }

}