package com.magnusnorgaard.apimemes.Class;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;

public class GlobalExtra {

    private final Context context;

    public GlobalExtra(Context context){
        this.context = context;
    }

    @SuppressLint("ObsoleteSdkInt")
    public void CopyClipboard(String textToCopy, boolean toast){
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                clipboard.setText(textToCopy);
            } else {
                clipboard.setPrimaryClip(ClipData.newPlainText(context.getPackageName(), textToCopy));
            }

            if (toast){
                Toast.makeText(context, "Link del meme copiado exitosamente", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            if (toast){
                Toast.makeText(context, "Ocurrio un error al copiar el link del meme: "+e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
    public void saveToSharedPreferences(String key, String value){
        Editor shared = PreferenceManager.getDefaultSharedPreferences(context).edit();
        shared.putString(key, value);
        shared.apply();
        shared.commit();
    }

    public String getValueFromSharedPreferences(String key){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, null);
    }

    public void sendMemeToWhatsApp(Bitmap bitmap) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("image/*");
            i.setPackage("com.whatsapp");
            i.putExtra(android.content.Intent.EXTRA_STREAM, Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "", null)));
            context.startActivity(Intent.createChooser(i, "Compartir con"));
        } catch (Exception e) {
            Toast.makeText(context, "Error "+e, Toast.LENGTH_LONG).show();
        }
    }

    public Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap bitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);
        return bitmap;
    }
}