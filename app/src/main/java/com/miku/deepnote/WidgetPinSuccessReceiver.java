package com.miku.deepnote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class WidgetPinSuccessReceiver extends BroadcastReceiver {

    public static final String ACTION_PINNED_SUCCESS = "com.miku.deepnote.ACTION_PINNED_SUCCESS";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 当小部件成功固定到桌面时调用
        Toast.makeText(
                context,
                R.string.widget_pin_success,
                Toast.LENGTH_LONG
        ).show();

        // 可以在这里执行其他操作，例如记录日志、发送事件等
    }
}
