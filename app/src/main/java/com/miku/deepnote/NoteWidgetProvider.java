package com.miku.deepnote;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.widget.RemoteViews;

public class NoteWidgetProvider extends AppWidgetProvider {
    static final String ACTION_CLICK = "com.miku.ACTION_CLICK";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int appWidgetId : appWidgetIds) {
            onWidgetUpdate(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_CLICK.equals(intent.getAction())) {
            Intent i = new Intent(context, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        } else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName provider = new ComponentName(context, NoteWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(provider);
            for (int appWidgetId : appWidgetIds) {
                onWidgetUpdate(context, appWidgetManager, appWidgetId);
            }
        }
    }

    private void onWidgetUpdate(Context context, AppWidgetManager appWidgetManager, int appWidgetId){
        Intent intent = new Intent(context, NoteWidgetProvider.class);
        intent.setAction(ACTION_CLICK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        SharedPreferences sp=context.getSharedPreferences("Note",Context.MODE_PRIVATE);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setTextViewText(R.id.title, sp.getString("title","我是标题"));
        views.setTextViewText(R.id.text, sp.getString("text","我是长长的内容"));
        views.setTextViewTextSize(R.id.title, TypedValue.COMPLEX_UNIT_SP, sp.getInt("title_size", Constants.DEFAULT_TITLE_SIZE));
        views.setTextViewTextSize(R.id.text, TypedValue.COMPLEX_UNIT_SP, sp.getInt("text_size", Constants.DEFAULT_TEXT_SIZE));

        int titleColor=sp.getInt("title_color", Constants.DEFAULT_TITLE_COLOR);
        int textColor=sp.getInt("text_color", Constants.DEFAULT_TEXT_COLOR);
        views.setTextColor(R.id.title, titleColor);
        views.setTextColor(R.id.text, textColor);

        views.setOnClickPendingIntent(R.id.widget, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


}
