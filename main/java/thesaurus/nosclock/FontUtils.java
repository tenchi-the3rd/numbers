package thesaurus.nosclock;

/**
 * Created by tanaka-kazuk on 2018/02/08.
 */

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * フォント関連の共通クラス
 */
public class FontUtils {
    /**
     * フォントファイルtt○を assets から読み込む
     * @param context コンテキスト
     * @param path    フォント ファイルを示す assets フォルダからの相対パス
     * @return 成功時は Typeface インスタンス、それ以外は null
     */
    public static Typeface getTypefaceFromAssets(Context context, String path ) {
        return Typeface.createFromAsset( context.getAssets(), path );
    }
    /**
     * フォントZIPを assets から読み込む
     * @param context コンテキスト
     * @param path    フォントZIP ファイルを示す assets フォルダからの相対パス
     * @return 成功時は Typeface インスタンス、それ以外は null
     */
    public static Typeface getTypefaceFromAssetsZip( Context context , String path ) {
        Typeface typefaceOriginal = null;
        InputStream  inputStream;
        try {
            AssetManager assetManager  = context.getAssets();
            inputStream  = assetManager.open(path, AssetManager.ACCESS_STREAMING);
            ZipInputStream zipInputStream = new ZipInputStream(assetManager.open(path, AssetManager.ACCESS_STREAMING));
            ZipEntry zipEntry             = zipInputStream.getNextEntry();
            if (zipEntry != null) {
                path = context.getFilesDir().toString() + "/" + zipEntry.getName();
                FileOutputStream fileOutputStream = new FileOutputStream( path , false );
                byte[] buf = new byte[256];
                int size = 0;
                while ((size = zipInputStream.read(buf, 0, buf.length)) > -1) {
                    fileOutputStream.write(buf, 0, size);
                }
                fileOutputStream.close();
                zipInputStream.closeEntry();
            }
            zipInputStream.close();
            inputStream.close();
            if(path != null){
                Log.d( "Assets path" , path );
                typefaceOriginal = Typeface.createFromFile(path);
            }
        } catch (IOException e) {
            Log.e( "IOException ZIP" , e.getMessage() );
        }
        return  typefaceOriginal;
    }
}