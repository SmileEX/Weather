package com.example.myapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyAsyncTask extends AsyncTask<Void, Void, String> {

    private TextView textView;

    public MyAsyncTask(TextView textView) {
        this.textView = textView;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            // 创建 OkHttp 客户端
            OkHttpClient client = new OkHttpClient();

            // 创建请求
            Request request = new Request.Builder()
                    .url("https://api.xygeng.cn/one")  // 将此替换为你的 API 地址
                    .build();

            // 执行请求并获取响应
            Response response = client.newCall(request).execute();

            // 检查响应是否成功
            if (response.isSuccessful()) {
                // 返回响应体的字符串
                return response.body().string();
            } else {
                // 处理错误
                return "Error: " + response.code();
            }
        } catch (IOException e) {
            // 处理异常
            return "Error: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // 将结果显示在 TextView 中
        DetailWis wis = DetailWis.fromJson(result);
        textView.setText(wis.getData().getContent() + "\n--" + wis.getData().getOrigin());
    }
}
