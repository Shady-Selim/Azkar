package com.shady_selim.azkar.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shady_selim.azkar.R;
import com.shady_selim.azkar.firebaseDB.AzkarListClass;

import java.util.List;
import java.util.Random;

public class AzkarWidget extends AppWidgetProvider {

    static void updateAppWidget(final Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences preferences = context.getSharedPreferences("zikr", 0);
        if (preferences.contains("azkar")) {
            CharSequence widgetText;
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.azkar_widget);
            List<AzkarListClass> azkaListClassList = new Gson().fromJson(preferences.getString("azkar", null),new TypeToken<List<AzkarListClass>>() {}.getType());
            Random rand = new Random();
            widgetText= azkaListClassList.get(rand.nextInt(azkaListClassList.size() -1)).content + "\n\n" + context.getString(R.string.repeated_text) + " " + azkaListClassList.get(rand.nextInt(azkaListClassList.size() -1)).count;
            views.setTextViewText(R.id.appwidget_text, widgetText);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        /*FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("arabic").child("morning").child("azkar");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("test", "Count is: " + dataSnapshot.getChildrenCount());

                List<Map<String, String>> azkarList = ((List<Map<String, String>>)dataSnapshot.getValue());
                Random rand = new Random();
                widgetText[0]= azkarList.get(rand.nextInt(azkarList.size() -1)).get("content");
            }
            @Override
            public void onCancelled(DatabaseError error) {
                widgetText[0] = context.getString(R.string.no_data);
                Log.w("test", "Failed to read value.", error.toException());
            }
        });*/
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}

