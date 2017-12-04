package com.example.zhangjing.forself;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SmsRecevier extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);
                Date date = new Date(msg.getTimestampMillis());//时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String receiveTime = format.format(date);
                String message = "来自:" + msg.getDisplayOriginatingAddress()
                        + "的短信:\n" + msg.getDisplayMessageBody()
                        + "\n 时间:"
                        + receiveTime;
     //           Log.i("forself", message);

                //处理的逻辑
                if (msg.getDisplayMessageBody().contains("快递") ||
                        msg.getDisplayMessageBody().contains("验证码")) {
                    //TODO
                    String phoneNumber = "********";//填自己的号码
                    transmitMessageTo(phoneNumber, message);
                }

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
