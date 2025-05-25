package com.leaf.collegeidleapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.leaf.collegeidleapp.R;
import com.leaf.collegeidleapp.bean.Commodity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 商品列表适配器 - 用于展示所有商品信息的列表视图
 * 功能特性:
 * 1. 实现BaseAdapter进行数据绑定
 * 2. 使用ViewHolder模式优化列表性能
 * 3. 支持动态数据更新
 * 4. 处理商品图片的二进制数据转换
 *
 * @author autumn_leaf
 */
public class AllCommodityAdapter extends BaseAdapter {

    // 上下文对象，用于获取系统服务
    private Context context;

    // 布局填充器，用于将XML布局转换为View对象
    private LayoutInflater layoutInflater;

    // 数据源集合，存储所有商品对象
    private List<Commodity> commodities = new ArrayList<>();

    /**
     * 视图位置缓存
     * Key: 位置索引
     * Value: 对应的视图对象
     * 注意：长期保存View引用可能导致内存泄漏，建议改用RecyclerView
     */
    HashMap<Integer, View> location = new HashMap<>();

    /**
     * 构造方法
     * @param context 上下文对象，通常传入Activity实例
     */
    public AllCommodityAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context); // 初始化布局填充器
    }

    /**
     * 更新数据源并刷新列表
     * @param commodities 新的商品数据集合
     */
    public void setData(List<Commodity> commodities) {
        this.commodities = commodities;
        notifyDataSetChanged(); // 通知数据变化，触发列表刷新
    }

    /**
     * 获取数据项数量
     * @return 当前商品总数
     */
    @Override
    public int getCount() {
        return commodities.size();
    }

    /**
     * 获取指定位置的数据项
     * @param position 列表位置
     * @return 商品对象
     */
    @Override
    public Object getItem(int position) {
        return commodities.get(position);
    }

    /**
     * 获取数据项ID（简单实现直接返回位置）
     * @param position 列表位置
     * @return 与位置相同的ID值
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 核心方法：创建/复用列表项视图
     * @param position    当前项位置
     * @param convertView 可复用的旧视图
     * @param parent      父容器
     * @return 处理后的列表项视图
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // 视图复用逻辑
        if (location.get(position) == null) {  // 无缓存视图时新建
            // 1. 填充布局
            convertView = layoutInflater.inflate(R.layout.layout_all_commodity, null);

            // 2. 获取数据对象
            Commodity commodity = (Commodity) getItem(position);

            // 3. 创建ViewHolder并绑定视图
            holder = new ViewHolder(convertView, commodity);

            // 4. 缓存视图及对应的ViewHolder
            location.put(position, convertView);
            convertView.setTag(holder);
        } else {  // 有缓存视图时直接复用
            convertView = location.get(position);
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    /**
     * ViewHolder内部类 - 优化列表性能的关键
     * 功能：缓存视图元素引用，避免重复findViewById
     */
    static class ViewHolder {
        // 视图元素声明
        ImageView ivCommodity;
        TextView tvTitle, tvType, tvDescription, tvPrice;

        /**
         * 构造方法 - 初始化视图并绑定数据
         * @param itemView  列表项根视图
         * @param commodity 对应的商品数据
         */
        public ViewHolder(View itemView, Commodity commodity) {            // 初始化视图元素
            tvTitle = itemView.findViewById(R.id.tv_name);
            tvType = itemView.findViewById(R.id.tv_type);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvDescription = itemView.findViewById(R.id.tv_description);
            ivCommodity = itemView.findViewById(R.id.iv_commodity);

            // 数据绑定
            tvTitle.setText(commodity.getTitle());
            tvDescription.setText(commodity.getDescription());
            tvPrice.setText(String.valueOf(commodity.getPrice()) + "元");
            tvType.setText(commodity.getCategory());

            // 图片处理
            byte[] picture = commodity.getPicture();
            /*
             * 将字节数组转换为Bitmap的注意事项：
             * 1. 大图直接解码可能导致OOM，建议添加采样压缩
             * 2. 建议使用图片加载框架（如Glide）处理图片加载
             * 3. 当前实现方式适合小图，需确保商品图片经过压缩处理
             */
            Bitmap img = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            ivCommodity.setImageBitmap(img);
        }
    }
}