package com.example.zhangjing.forself;

import android.app.Activity;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SMSContent extends ContentObserver {

    private Cursor cursor = null;
    private String last_id = "";
    private Activity activity;
    private Uri uri;

    public SMSContent(Handler handler, Activity activity) {
        super(handler);
        // this.handler = handler;
        this.activity = activity;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        // 读取收件箱中指定号码的短信
        cursor = activity.managedQuery(Uri.parse("content://sms/inbox"),
                new String[]{"_id", "address", "read", "body"}, "read=?", new String[]{"0"}, "date desc");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String phone = cursor.getString(cursor.getColumnIndex("address"));
                String body = cursor.getString(cursor.getColumnIndex("body"));
                String _id = cursor.getString(cursor.getColumnIndex("_id"));
                // 缓存上一次信息
                if (!last_id.equals(_id)) {
//                    System.out.println("短信---->" + "电话号码：" + phone + "内容：" + body);
                    last_id = _id;
                    System.out.println(_id);
                    ContentValues values = new ContentValues();
                    values.put("read", "1"); // 修改短信为已读模式
                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    msg.obj = phone;
                    this.handler.sendMessage(msg);
                }

            }

        }
        cursor.close();
    }

    // 处理接收的短信
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.i("foeself","处理接收的短信" + msg.obj);

                    break;

                default:
                    break;
            }

        }

    };
}