package com.miku.deepnote;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

public class MainActivity extends AppCompatActivity {

    TextView title,text;
    SeekBar title_size,text_size;
    View title_color,text_color;
    TextView title_color_reset,text_color_reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        title=findViewById(R.id.title);
        text=findViewById(R.id.text);
        title_size=findViewById(R.id.title_size);
        text_size=findViewById(R.id.text_size);
        title_color=findViewById(R.id.title_color);
        text_color=findViewById(R.id.text_color);
        title_color_reset=findViewById(R.id.title_color_reset);
        text_color_reset=findViewById(R.id.text_color_reset);
        title_color_reset.setOnClickListener(v -> {
            title_color.setBackgroundColor(Constants.DEFAULT_TITLE_COLOR);
            title_color.setTag(Constants.DEFAULT_TITLE_COLOR);
        });
        text_color_reset.setOnClickListener(v -> {
            text_color.setBackgroundColor(Constants.DEFAULT_TEXT_COLOR);
            text_color.setTag(Constants.DEFAULT_TEXT_COLOR);
        });


        Button save=findViewById(R.id.save);
        save.setOnClickListener(v -> {
            doSave(true);
        });
        Button pin=findViewById(R.id.pin);
        pin.setOnClickListener(v -> {
            pinWidgetToHomeScreen(MainActivity.this);
        });



        SharedPreferences sp=getSharedPreferences("Note",MODE_PRIVATE);
        title.setText(sp.getString("title",""));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP,sp.getInt("title_size", Constants.DEFAULT_TITLE_SIZE));
        text.setText(sp.getString("text",""));
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP,sp.getInt("text_size", Constants.DEFAULT_TEXT_SIZE));
        int titleColor=sp.getInt("title_color", Constants.DEFAULT_TITLE_COLOR);
        int textColor=sp.getInt("text_color", Constants.DEFAULT_TEXT_COLOR);
        title_color.setBackgroundColor(titleColor);
        title_color.setTag(titleColor);
        text_color.setBackgroundColor(textColor);
        text_color.setTag(textColor);

        title_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPicker("选择标题颜色",title_color);
            }
        });
        text_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPicker("选择内容颜色",text_color);
            }
        });
        title_size.setProgress(sp.getInt("title_size", Constants.DEFAULT_TITLE_SIZE));
        text_size.setProgress(sp.getInt("text_size", Constants.DEFAULT_TEXT_SIZE));
        title_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP,progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        text_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text.setTextSize(TypedValue.COMPLEX_UNIT_SP,progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        doSave(false);
    }

    private void doSave(boolean showToast){
        getSharedPreferences("Note",MODE_PRIVATE).edit().
                putString("title",title.getText().toString())
                .putString("text",text.getText().toString())
                .putInt("title_size",title_size.getProgress())
                .putInt("text_size",text_size.getProgress())
                .putInt("title_color",(int)title_color.getTag())
                .putInt("text_color",(int)text_color.getTag())
                .apply();
        if(showToast){
            Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
        }
        Intent intent =new Intent(MainActivity.this, NoteWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        sendBroadcast(intent);
    }

    void showColorPicker(String title,View colorView){
        ColorPickerDialog.Builder dialogBuilder=new ColorPickerDialog.Builder(this)
                .setTitle(title)
                .setPositiveButton(R.string.ok,
                        new ColorEnvelopeListener() {
                            @Override
                            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                colorView.setBackgroundColor(envelope.getColor());
                                colorView.setTag(envelope.getColor());
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                .attachAlphaSlideBar(true) // the default value is true.
                .setBottomSpace(12); // set a bottom space between the last slidebar and buttons.
        ColorPickerView colorPickerView =dialogBuilder.getColorPickerView();
        colorPickerView.setFlagView(new CustomFlag(this, R.layout.custom_flag_view));
        colorPickerView.setInitialColor((int) colorView.getTag());
        colorPickerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                colorPickerView.setInitialColor((int) colorView.getTag());
            }
        },10);

        dialogBuilder.show();
    }


    private void pinWidgetToHomeScreen(Context context) {
        // 1. 获取 AppWidgetManager
        AppWidgetManager appWidgetManager = (AppWidgetManager) context.getSystemService(Context.APPWIDGET_SERVICE);

        // 2. 获取小部件的 ComponentName
        ComponentName myWidgetComponent = new ComponentName(context, NoteWidgetProvider.class);

        // 3. 检查设备是否支持固定小部件
        if (appWidgetManager.isRequestPinAppWidgetSupported()) {
            // 4. 创建回调 Intent
            Intent successIntent = new Intent(WidgetPinSuccessReceiver.ACTION_PINNED_SUCCESS)
                    .setClass(context, WidgetPinSuccessReceiver.class);

            // 5. 创建 PendingIntent（兼容 Android 12+）
            PendingIntent successPendingIntent = PendingIntent.getBroadcast(
                    context,
                    0, // requestCode
                    successIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // 6. 发起请求
            if(!appWidgetManager.requestPinAppWidget(myWidgetComponent,null,successPendingIntent)){
                Toast.makeText(context, R.string.widget_pin_fail, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, R.string.widget_pin_fail, Toast.LENGTH_SHORT).show();
        }
    }

}