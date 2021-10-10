package com.magnusnorgaard.apimemes.Class;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.magnusnorgaard.apimemes.R;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import java.io.File;
import java.util.List;

public class StorageSaver {

    private final AppCompatActivity activityContext;
    private BroadcastReceiver broadcastDownload;

    public StorageSaver(Context context){
        activityContext = (AppCompatActivity) context;
    }

    public void SaveFile(String URL, String nameFile){
        PermissionX.init(activityContext)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .explainReasonBeforeRequest()
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        if (!allGranted) {
                            SaveFile(URL, nameFile);
                        } else {
                            try {
                                DownloadManager mgr = (DownloadManager) activityContext.getSystemService(Context.DOWNLOAD_SERVICE);
                                Uri downloadUri = Uri.parse(URL);
                                DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                                        .setAllowedOverRoaming(false).setTitle(activityContext.getString(R.string.app_name)).setDescription("Descargando...")
                                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nameFile);
                                long downloadID = mgr.enqueue(request);
                                broadcastDownload = new BroadcastReceiver() {
                                    @Override
                                    public void onReceive(Context context, Intent intent) {
                                        if (downloadID == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)) {
                                            Toast.makeText(activityContext, "Descarga completada!\n\nGuardado en: "+ Environment.DIRECTORY_DOWNLOADS + File.separator + nameFile, Toast.LENGTH_LONG).show();
                                            activityContext.unregisterReceiver(broadcastDownload);
                                        }
                                    }
                                };
                                activityContext.registerReceiver(broadcastDownload, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                            } catch (Exception e) {
                                Toast.makeText(activityContext, "Error "+e, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
}