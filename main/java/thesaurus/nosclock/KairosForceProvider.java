package thesaurus.nosclock;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.*;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.RemoteViews;


public class KairosForceProvider extends AppWidgetProvider {
    private static final String ACTION_KAIROS_INTERVAL = "thesaurus.nosclock.KairosForce.INTERVAL";
    private static final long INTERVAL = 60 * 1000;


    private PendingIntent getAlermPendingIntent(Context context){
        Intent alarmIntent = new Intent(context, KairosForceProvider.class);

        alarmIntent.setAction(ACTION_KAIROS_INTERVAL);

        return PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
    }


    private void setInterval(Context context, long interval) {
        PendingIntent operation = getAlermPendingIntent(context);

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        long now          = System.currentTimeMillis();
        long oneHourAfter = now / interval * interval + interval;


        am.set(AlarmManager.RTC, oneHourAfter, operation);

    }

    private void updateClock(Context context){
        ComponentName	cn = new ComponentName(context, KairosForceProvider.class);
        RemoteViews		rv = new RemoteViews(context.getPackageName(), R.layout.nos_clock_widget);

        long			now = System.currentTimeMillis();
        Date			dt = new Date(now);


        // 表示の更新
        java.text.DateFormat fmt;

        //*****************************************
        //
        // ローカル時間の表示
        //
        //*****************************************
        fmt = android.text.format.DateFormat.getTimeFormat(context);


        Pattern pt = Pattern.compile("^(\\D*)(\\d+\\:\\d+)(\\D*)$");
        Matcher match = pt.matcher(fmt.format(dt));

        String prefix	= "";
        String suffix	= "";
        String TimeText	= "";

        if(match.find()){
            prefix		= match.group(1).trim();
            TimeText	= match.group(2).trim();
            suffix		= match.group(3).trim();
        }

        //rv.setTextViewText(R.id.PrefixText, prefix);
        //rv.setTextViewText(R.id.TimeText,   TimeText);
        //rv.setTextViewText(R.id.SuffixText, suffix);

        //*****************************************
        //
        // ローカル日付の表示
        //
        //*****************************************
        //CharSequence dateText  = android.text.format.DateFormat.format("yyyy/MM/dd kk:mm:ss", Calendar.getInstance());
        CharSequence dateText  = android.text.format.DateFormat.format("yyyy/MM/dd", Calendar.getInstance());
        //CharSequence dateText2  = android.text.format.DateFormat.format("kk:mm:ss", Calendar.getInstance());
        //fmt = android.text.format.DateFormat.getLongDateFormat(context);
        //rv.setTextViewText(R.id.DateText, fmt.format(dt));

        // インターバルの設定
        setInterval(context, INTERVAL);


        // Paint作成
        String txt = dateText.toString();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);



        //paint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        Typeface typeface = FontUtils.getTypefaceFromAssetsZip(context,"font/astral.zip");
        paint.setTypeface(typeface);

        // 描画テキストの領域を取得
        // rect.widthとmtwは違う。rect.widthの方が小さい。
        // rect.heightとfmHeightは違う。rect.heightの方が小さい。
        Rect rect = new Rect();
        paint.getTextBounds(txt, 0, txt.length(), rect);
        Paint.FontMetrics fm = paint.getFontMetrics();
        int mtw = (int) paint.measureText(txt);
        int fmHeight = (int) (Math.abs(fm.top) + fm.bottom);
        Log.v("onCreate", "rect.w=" + rect.width() + " rect.h=" + rect.height() + " fm.top=" + fm.top + " fm.bottom=" + fm.bottom + " fm.ascent=" + fm.ascent + "fm.descent=" + fm.descent + " fm.height=" + fmHeight + " mtw=" + mtw);

        // 描画領域ピッタリのビットマップ作成
        int margin = 1; // ギリギリすぎるので上下左右に多少余裕を取る
        Bitmap bmp = Bitmap.createBitmap(mtw + margin * 2, fmHeight + margin * 2, Bitmap.Config.ARGB_4444);
        Canvas cv = new Canvas(bmp);
        cv.drawText(txt, margin, Math.abs(fm.ascent) + margin, paint);
        rv.setImageViewBitmap(R.id.PrefixImage,bmp);

        //--------------------------------------------
        paint.getTextBounds(TimeText, 0, TimeText.length(), rect);
        fm = paint.getFontMetrics();
        mtw = (int) paint.measureText(TimeText);
        fmHeight = (int) (Math.abs(fm.top) + fm.bottom);
        Bitmap bmp2 = Bitmap.createBitmap(mtw + margin * 2, fmHeight + margin * 2, Bitmap.Config.ARGB_4444);
        Canvas cv2 = new Canvas(bmp2);
        cv2.drawText(TimeText, margin, Math.abs(fm.ascent) + margin, paint);
        rv.setImageViewBitmap(R.id.TimeImage, bmp2);
        //--------------------------------------------

        AppWidgetManager.getInstance(context).updateAppWidget(cn, rv);

        return;
    }

    @Override
    public void onEnabled(Context context){
        super.onEnabled(context);

        Log.i("onEnable", "DONE.");
        return;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int ids []){
        super.onUpdate(context, manager, ids);

        //時計の更新処理
        updateClock(context);
        setInterval(context, 100);
        Log.i("onUpdate", "DONE.");

        return;
    }


    @Override
    public void onReceive(Context context, Intent intent){

        if(ACTION_KAIROS_INTERVAL.equals(intent.getAction())){
            updateClock(context);
            Log.i("onReceive", "ACTION_KAIROS_INTERVAL.");
        }


        super.onReceive(context, intent);
        return;
    }

    @Override
    public void onDeleted(Context context, int[] ids){

        super.onDeleted(context, ids);

        Log.i("onDeleted", "DONE.");

        return;
    }

    @Override
    public void onDisabled(Context context){

        // 時計更新のタイマーを停止する
        PendingIntent operation = getAlermPendingIntent(context);

        AlarmManager	am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        am.cancel(operation);

        Log.i("onDisabled", "DONE.");

        super.onDisabled(context);
        return;
    }
}