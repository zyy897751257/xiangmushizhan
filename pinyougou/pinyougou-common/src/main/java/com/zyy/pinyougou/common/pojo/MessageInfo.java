package com.zyy.pinyougou.common.pojo;

public class MessageInfo<T> {

    public static final int METHOD_ADD = 1;         //新增
    public static final int METHOD_UPDATE = 2;      //更新
    public static final int METHOD_DELETE = 3;      //删除

    private Object context;
    private String topic;
    private String tags;
    private String keys;
    private int method;

    public MessageInfo() {
    }

    public MessageInfo(Object context, String topic, String tags, int method) {
        this.context = context;
        this.topic = topic;
        this.tags = tags;
        this.method = method;
    }

    public MessageInfo(Object context, String topic, String tags, String keys, int method) {
        this.context = context;
        this.topic = topic;
        this.tags = tags;
        this.keys = keys;
        this.method = method;
    }

    public Object getContext() {
        return context;
    }

    public void setContext(Object context) {
        this.context = context;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }
}
