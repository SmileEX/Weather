package com.example.myapp;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class DetailWis {
    @SerializedName("code")
    private int code;
    @SerializedName("data")
    private Data data;
    public Data getData() {
        return data;
    }

    public static DetailWis fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, DetailWis.class);
    }

    public static class Data {
        private String tag;
        private String name;
        private String origin;
        private String content;

        // 其他字段的 getter 方法

        public String getTag() {
            return tag;
        }

        public String getName() {
            return name;
        }

        public String getOrigin() {
            return origin;
        }

        public String getContent() {
            return content;
        }
    }
}
