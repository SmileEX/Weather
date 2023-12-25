package com.example.myapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<String> dataList;

    public MyAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    // 创建新的视图（由布局管理器调用）
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    // 替换视图内容（由布局管理器调用）
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String itemText = dataList.get(position);
        holder.textView.setText(itemText);
    }

    // 返回数据集的大小（由布局管理器调用）
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // 提供对视图的引用
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView);
        }
    }
}
