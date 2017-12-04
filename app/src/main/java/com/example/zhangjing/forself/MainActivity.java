package com.example.zhangjing.forself;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Uri SMS_INBOX = Uri.parse("content://sms/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SmsObserver smsContent = new SmsObserver(smsHandler, this);
        // 注册短信变化监听
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsContent);
    }
    public Handler smsHandler = new Handler() {
        //这里可以进行回调的操作
        //TODO
    };
    class SmsObserver extends ContentObserver {
        private Activity activity;
        private Cursor cursor = null;
        public SmsObserver(Handler handler, Activity activity) {
            super(handler);
            this.activity = activity;
//            getSmsFromPhone();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //每当有新短信到来时，使用我们获取短消息的方法
            getSmsFromPhone();
        }

        public void getSmsFromPhone() {
            String[] projection = new String[]{"_id", "address", "read", "body", "date"};//"_id", "address", "person",, "date", "type
            String where = " date >  "
                    + (System.currentTimeMillis() - 10 * 60 * 1000 * 1000);
            cursor = activity.managedQuery(SMS_INBOX, projection, where, null, "date desc");
            if (null == cursor)
                return;
            if (cursor.moveToFirst() && cursor.getString(cursor.getColumnIndex("read")).equals("0")) {
                String number = cursor.getString(cursor.getColumnIndex("address"));//手机号
                String d = cursor.getString(cursor.getColumnIndex("date"));//联系人姓名列表
                String body = cursor.getString(cursor.getColumnIndex("body"));
                //处理的逻辑
//                Log.i("forself", number + "&" + d + body);
                if (body.contains("快递") ||
                        body.contains("验证码")) {
                    //TODO
                    Date date = new Date();//时间
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String receiveTime = format.format(date);
                    String message = "转发*****"
                            + "的短信:\n" + body
                            + "\n来自:" + number
                            + "\n"
                            + receiveTime;
                    String phoneNumber = "*********";//接收短信的手机号
                    Log.i("forself", "转发处理");
                    transmitMessageTo(phoneNumber, message);
                }

            }
        }

        public void transmitMessageTo(String phoneNumber,String message){//转发短信
            SmsManager manager = SmsManager.getDefault();
            List<String> texts =manager.divideMessage(message);//这个必须有
            for(String text:texts){
                manager.sendTextMessage(phoneNumber, null, text, null, null);
            }
        }
    }
}
