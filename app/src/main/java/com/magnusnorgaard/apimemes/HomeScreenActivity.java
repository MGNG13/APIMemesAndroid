package com.magnusnorgaard.apimemes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.magnusnorgaard.apimemes.Class.APIMemes;
import com.magnusnorgaard.apimemes.Class.GlobalExtra;
import com.magnusnorgaard.apimemes.Class.StorageSaver;
import org.json.JSONArray;
import org.json.JSONObject;

public class HomeScreenActivity extends AppCompatActivity {

    private RecyclerView reyclerview;
    private ProgressBar progressBar;
    private FloatingActionButton floatingActionButton;
    private GlobalExtra globalReturns;
    private APIMemes globalApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // change ActionBar style
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle((Html.fromHtml("<font face=\"Times New Roman\">" + getString(R.string.app_name) + "</font>")));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.adb);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        // 1 find and set
        globalApi = new APIMemes(this);
        globalReturns = new GlobalExtra(this);
        reyclerview = findViewById(R.id.homescreen_recyclerView);
        progressBar = findViewById(R.id.homescreen_progressBar);
        floatingActionButton = findViewById(R.id.homescreen_floatingActionButton);

        // 2 config recyclerview
        reyclerview.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HomeScreenActivity.this);
        reyclerview.setLayoutManager(layoutManager);
        reyclerview.setItemAnimator(new DefaultItemAnimator());

        // 3 load
        globalApi.addOnLGetConnection(new APIMemes.addOnLGetConnection() {
            @Override
            public void OnActive(String response) {
                globalApi.addOnListJpgListener(new APIMemes.addOnListJpgListener() { // get images from server
                    @Override
                    public void OnSuccess(JSONObject response, JSONArray listFromImages) {
                        globalReturns.saveToSharedPreferences("listJPG", String.valueOf(listFromImages)); // save in device
                        globalApi.addOnListMp4Listener(new APIMemes.addOnListMp4Listener() {
                            @Override
                            public void OnSuccess(JSONObject response, JSONArray listFromVideos) {
                                JSONArray jsonArray = new JSONArray();
                                try {
                                    for (int i=0; i<listFromVideos.length(); i++){
                                        jsonArray.put(listFromVideos.get(i));
                                    }
                                    for (int i=0; i<listFromImages.length(); i++){
                                        jsonArray.put(listFromImages.get(i));
                                    }
                                } catch (Exception ignore) {
                                } finally {
                                    globalReturns.saveToSharedPreferences("listALL", String.valueOf(jsonArray)); // save in device
                                    setAdapter(2, null, jsonArray);
                                }
                            }

                            @Override
                            public void OnFailure(String reason) {
                                setAdapter(2, null, listFromImages);
                            }
                        });
                    }

                    @Override
                    public void OnFailure(String reason) {
                        setAdapter(1, reason, null);
                    }
                });
            }

            @Override
            public void OnNotActive(String error) {
                setAdapter(0, error, null);
            }
        });
        onClickFloating();
    }

    private Dialog dialog;
    public void onClickFloating(){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globalApi.addOnLGetConnection(new APIMemes.addOnLGetConnection() {
                    @Override
                    public void OnActive(String response) {
                        globalApi.addOnRandomJpgListener(new APIMemes.addOnRandomJpgListener() {
                            @Override
                            public void OnSuccess(JSONObject response, String url) {
                                dialog = new Dialog(HomeScreenActivity.this);
                                dialog.setContentView(R.layout.dialog);
                                ImageView image = dialog.findViewById(R.id.dialog_webView);
                                Glide.with(HomeScreenActivity.this).load(url)
                                        .transition(DrawableTransitionOptions.withCrossFade(250)).into(image);
                                dialog.show();
                                new GlobalExtra(HomeScreenActivity.this).CopyClipboard(url, true);
                            }

                            @Override
                            public void OnFailure(String reason) {
                                Toast.makeText(HomeScreenActivity.this, "Error "+reason, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void OnNotActive(String error) {
                        Toast.makeText(HomeScreenActivity.this, "Error "+error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void setAdapter(int state, String error, JSONArray array){
        int exitDuration = 1500;
        if(state == 0){
            if(globalReturns.getValueFromSharedPreferences("listALL") != null){
                progressBar.setVisibility(View.GONE);
                AlphaAnimation exit = new AlphaAnimation(0f, 1.0f);
                exit.setDuration(exitDuration);
                progressBar.setAnimation(exit);
                try {
                    JSONArray arrayListAll = new JSONArray(globalReturns.getValueFromSharedPreferences("listALL"));
                    reyclerview.setAdapter(new RecyclerviewItemAdapter(arrayListAll));
                } catch (Exception e) {
                    JSONArray arrayError = new JSONArray();
                    arrayError.put("Error: "+e);
                    reyclerview.setAdapter(new RecyclerviewItemAdapter(arrayError));
                }
            } else {
                if(globalReturns.getValueFromSharedPreferences("listJPG") != null){
                    progressBar.setVisibility(View.GONE);
                    AlphaAnimation exit = new AlphaAnimation(0f, 1.0f);
                    exit.setDuration(exitDuration);
                    progressBar.setAnimation(exit);
                    try {
                        JSONArray arrayListAll = new JSONArray(globalReturns.getValueFromSharedPreferences("listALL"));
                        reyclerview.setAdapter(new RecyclerviewItemAdapter(arrayListAll));
                    } catch (Exception e) {
                        JSONArray arrayError = new JSONArray();
                        arrayError.put("Error: "+e);
                        reyclerview.setAdapter(new RecyclerviewItemAdapter(arrayError));
                    }
                } else {
                    Toast.makeText(HomeScreenActivity.this, "Error al conectarse con la API: "+error, Toast.LENGTH_LONG).show();
                }
            }
        } else if(state == 1){
            if(globalReturns.getValueFromSharedPreferences("listALL") != null){
                progressBar.setVisibility(View.GONE);
                AlphaAnimation exit = new AlphaAnimation(0f, 1.0f);
                exit.setDuration(exitDuration);
                progressBar.setAnimation(exit);
                try {
                    JSONArray arrayListAll = new JSONArray(globalReturns.getValueFromSharedPreferences("listALL"));
                    reyclerview.setAdapter(new RecyclerviewItemAdapter(arrayListAll));
                } catch (Exception e) {
                    JSONArray arrayError = new JSONArray();
                    arrayError.put("Error: "+e);
                    reyclerview.setAdapter(new RecyclerviewItemAdapter(arrayError));
                }
            } else {
                if(globalReturns.getValueFromSharedPreferences("listJPG") != null){
                    progressBar.setVisibility(View.GONE);
                    AlphaAnimation exit = new AlphaAnimation(0f, 1.0f);
                    exit.setDuration(exitDuration);
                    progressBar.setAnimation(exit);
                    try {
                        JSONArray arrayListAll = new JSONArray(globalReturns.getValueFromSharedPreferences("listALL"));
                        reyclerview.setAdapter(new RecyclerviewItemAdapter(arrayListAll));
                    } catch (Exception e) {
                        JSONArray arrayError = new JSONArray();
                        arrayError.put("Error: "+e);
                        reyclerview.setAdapter(new RecyclerviewItemAdapter(arrayError));
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    AlphaAnimation exit = new AlphaAnimation(0f, 1.0f);
                    exit.setDuration(exitDuration);
                    progressBar.setAnimation(exit);
                    JSONArray arrayError = new JSONArray();
                    arrayError.put("Error: "+error);
                    reyclerview.setAdapter(new RecyclerviewItemAdapter(arrayError));
                }
            }
        } else if (state == 2){
            progressBar.setVisibility(View.GONE);
            AlphaAnimation exit = new AlphaAnimation(0f, 1.0f);
            exit.setDuration(exitDuration);
            progressBar.setAnimation(exit);
            reyclerview.setAdapter(new RecyclerviewItemAdapter(array));
        }
    }

    class RecyclerviewItemAdapter extends RecyclerView.Adapter<RecyclerviewItemAdapter.MyViewHolder> {

        private final JSONArray names;

        public RecyclerviewItemAdapter(JSONArray names){
            this.names = names;
        }

        @NonNull
        @Override
        public RecyclerviewItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
            return new MyViewHolder(view);
        }

        @SuppressLint({ "SetTextI18n", "RecyclerView" })
        @Override
        public void onBindViewHolder(@NonNull RecyclerviewItemAdapter.MyViewHolder holder, final int position) {
            holder.itemMain.setAnimation(AnimationUtils.loadAnimation(HomeScreenActivity.this, R.anim.fade_entry));

            try {
                holder.itemText.setText(String.valueOf( names.get(position) ));
                if (String.valueOf(names.get(position)).contains(".mp4")){
                    Glide.with(HomeScreenActivity.this)
                            .load(APIMemes.URL + APIMemes.VideosListConstant +names.get(position))
                            .transition(DrawableTransitionOptions.withCrossFade(250))
                            .into(holder.itemImage);
                    holder.itemMain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(HomeScreenActivity.this, ItemActivity.class);
                                intent.putExtra("ResourceName", String.valueOf(names.get(position)));
                                intent.putExtra("ResourceURL", APIMemes.URL + APIMemes.VideosListConstant +names.get(position));
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(HomeScreenActivity.this, "Error "+ e, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Glide.with(HomeScreenActivity.this)
                            .load(APIMemes.URL + APIMemes.ImagesListConstant +names.get(position))
                            .transition(DrawableTransitionOptions.withCrossFade(250))
                            .into(holder.itemImage);
                    holder.itemMain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(HomeScreenActivity.this, ItemActivity.class);
                                intent.putExtra("ResourceName", String.valueOf(names.get(position)));
                                intent.putExtra("ResourceURL", APIMemes.URL + APIMemes.ImagesListConstant +names.get(position));
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(HomeScreenActivity.this, "Error "+ e, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                if (getItemCount() <= 0){
                    holder.itemText.setText("Error "+e);
                } else {
                    Toast.makeText(HomeScreenActivity.this, "Error "+ e, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public int getItemCount() {
            try {
                return names.length();
            } catch (Exception ignore) {
                return 0;
            }
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            public ImageView itemImage;
            public TextView itemText;
            public ConstraintLayout itemMain;
            public MyViewHolder(View itemView) {
                super(itemView);
                itemMain = itemView.findViewById(R.id.item_mainData);
                itemImage = itemView.findViewById(R.id.item_imageView);
                itemText = itemView.findViewById(R.id.item_textView);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idItem = item.getItemId();
        if (idItem == R.id.download) {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_edittext);

            EditText editText = dialog.findViewById(R.id.dialog_edittext);
            Button button = dialog.findViewById(R.id.dialog_button);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().contains(APIMemes.URL)){
                        if (!s.toString().contains(APIMemes.URL2)){
                            onClickDialog(1, button, null, editText, dialog);
                        } else {
                            onClickDialog(0, button, s, null, dialog);
                        }
                    } else {
                        onClickDialog(0, button, s, null, dialog);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onClickDialog(int state, Button button, CharSequence s, EditText editText, Dialog dialog){
        if (state == 0){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String value = s.toString();
                        new StorageSaver(HomeScreenActivity.this).
                                SaveFile(value,
                                        value.substring(value.lastIndexOf("/")+1)
                                );
                        dialog.dismiss();
                    } catch (Exception e) {
                        Toast.makeText(HomeScreenActivity.this, "Error "+e, Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else if (state == 1){
            editText.setError("Ingresa un URL valido!");
            editText.requestFocus();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editText.setError("Ingresa un URL valido!");
                    editText.requestFocus();
                }
            });
        }
    }
}