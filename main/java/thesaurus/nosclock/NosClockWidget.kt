package thesaurus.nosclock

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.widget.RemoteViews
import android.widget.TextClock
import android.widget.EditText
import java.util.*


/**
 * Implementation of App Widget functionality.
 */
class NosClockWidget:AppWidgetProvider() {

    internal val btn1Filter = "com.moonlight_aska.android.noswidget.edittext"

    override fun onUpdate(context:Context, appWidgetManager:AppWidgetManager, appWidgetIds:IntArray) {
        // There may be multiple widgets active, so update all of them


        val views = RemoteViews(context.packageName, R.layout.nos_clock_widget)
        //val edittext = RemoteViews(context.packageName, R.id.PrefixText)

        //val rv = RemoteViews(context.packageName, R.layout.nos_clock_widget)

        //val dt = Date()
        //rv.setTextViewText(R.id.PrefixText, dt.toString())
        /** font 指定 */

        //edittext.setTypeface(astralType)
        //Log.d("you", "TextClock#is24HourModeEnabled:" + textClock.is24HourModeEnabled())
        appWidgetManager.updateAppWidget(appWidgetIds, views)


/*        for (appWidgetId in appWidgetIds)
        {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }*/
    }

    override fun onEnabled(context:Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context:Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        internal fun updateAppWidget(context:Context, appWidgetManager:AppWidgetManager,
                                     appWidgetId:Int) {

        }
    }
}
