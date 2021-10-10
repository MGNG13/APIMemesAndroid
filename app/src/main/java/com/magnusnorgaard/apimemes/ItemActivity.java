package com.magnusnorgaard.apimemes;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.magnusnorgaard.apimemes.Class.GlobalExtra;
import com.magnusnorgaard.apimemes.Class.StorageSaver;

public class ItemActivity extends AppCompatActivity {

    private GlobalExtra global;
    private StorageSaver storage;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        global = new GlobalExtra(this);
        storage = new StorageSaver(this);

        Intent intent = getIntent();
        String resourceUrl = intent.getStringExtra("ResourceURL");
        String name = intent.getStringExtra("ResourceName");

        if (resourceUrl == null){
            Toast.makeText(this, "Ocurrio un error al cargar el meme...", Toast.LENGTH_LONG).show();
            return;
        } else if (name == null){
            Toast.makeText(this, "Ocurrio un error al cargar el meme...", Toast.LENGTH_LONG).show();
            return;
        }

        ImageView imageFromCardView = findViewById(R.id.itemactivity_imageView);
        VideoView videoFromCardView = findViewById(R.id.itemactivity_videoView);
        ProgressBar progressBarFromCardView = findViewById(R.id.itemactivity_progressBar);
        TextView textFromCardView = findViewById(R.id.itemactivity_textView);
        TextView textFromCardViewType = findViewById(R.id.itemactivity_textViewType);
        Button buttonCopy = findViewById(R.id.itemactivity_button);
        Button buttonDownload = findViewById(R.id.itemactivity_button2);
        Button buttonWhatsapp = findViewById(R.id.itemactivity_button3);

        if(resourceUrl.contains(".jpg") || resourceUrl.contains(".png")){
            setGoneProgressBar(progressBarFromCardView);
            Glide.with(this).load(resourceUrl)
                    .transition(DrawableTransitionOptions.withCrossFade(250)).into(imageFromCardView);
            textFromCardViewType.setText("Type JPG");
            imageFromCardView.setVisibility(View.VISIBLE);
            videoFromCardView.setVisibility(View.GONE);
            buttonWhatsapp.setVisibility(View.VISIBLE);
        } else {
            MediaController mediaController = new MediaController(this);
            videoFromCardView.setMediaController(mediaController);
            videoFromCardView.setVideoURI(Uri.parse(resourceUrl));
            videoFromCardView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    setGoneProgressBar(progressBarFromCardView);
                    mp.start();
                    new CountDownTimer(10, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {}
                        @Override
                        public void onFinish() {
                            mp.stop();
                            mediaController.show();
                        }
                    }.start();
                }
            });
            textFromCardViewType.setText("Type MP4");
            imageFromCardView.setVisibility(View.GONE);
            videoFromCardView.setVisibility(View.VISIBLE);
            buttonWhatsapp.setVisibility(View.GONE);
        }

        if (name.contains(".jpg")){
            if (name.contains(".png")){
                if (name.indexOf(".jpg") < name.indexOf(".png")){
                    textFromCardView.setText(name.substring(0, name.indexOf(".jpg")));
                } else {
                    textFromCardView.setText(name.substring(0, name.indexOf(".png")));
                }
            } else {
                textFromCardView.setText(name.substring(0, name.indexOf(".jpg")));
            }
        } else {
            textFromCardView.setText(name.substring(0, name.indexOf(".mp4")));
        }

        buttonCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.CopyClipboard(resourceUrl, true);
            }
        });
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storage.SaveFile(resourceUrl, name);
            }
        });
        buttonWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.sendMemeToWhatsApp(global.convertToBitmap(
                        imageFromCardView.getDrawable(),
                        imageFromCardView.getDrawable().getIntrinsicWidth(),
                        imageFromCardView.getDrawable().getIntrinsicHeight())
                );
            }
        });
    }

    private void setGoneProgressBar(ProgressBar progressBarFromCardView){
        progressBarFromCardView.setVisibility(View.GONE);
        AlphaAnimation exit = new AlphaAnimation(0f, 1.0f);
        exit.setDuration(500);
        progressBarFromCardView.setAnimation(exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}