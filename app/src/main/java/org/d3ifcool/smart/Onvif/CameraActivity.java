package org.d3ifcool.smart.Onvif;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pedro.vlc.VlcListener;
import com.pedro.vlc.VlcVideoLibrary;

import java.util.Arrays;

import org.d3ifcool.smart.R;

import ua.polohalo.zoomabletextureview.ZoomableTextureView;

public class CameraActivity extends AppCompatActivity implements VlcListener, View.OnClickListener {

    private VlcVideoLibrary vlcVideoLibrary;
    ImageView pause;
    private ZoomableTextureView surfaceView;
    private String[] options = new String[]{":fullscreen"};
    private TextView txt, cam, offline;
    private String rtspUrl, name;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);

        Intent intent = getIntent();
        name = intent.getExtras().getString("NAME_CAME");
        rtspUrl = intent.getExtras().getString("IP_ADDRESS");

        pause = findViewById(R.id.play_pause);

        pause.setImageResource(R.drawable.play);

        txt = findViewById(R.id.txt_pause);

        txt.setVisibility(View.VISIBLE);

        cam = findViewById(R.id.cam_name);

        pb = findViewById(R.id.progressBar);

        cam.setText(name);

        offline = findViewById(R.id.cam_offline);

        surfaceView = findViewById(R.id.surface);

        vlcVideoLibrary = new VlcVideoLibrary(this, this, surfaceView);
        vlcVideoLibrary.setOptions(Arrays.asList(options));

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vlcVideoLibrary.isPlaying()){

                    vlcVideoLibrary.pause();

                    pause.setImageResource(R.drawable.play);

                    txt.setVisibility(View.VISIBLE);

                    pb.setVisibility(View.GONE);


                }

                else {

                    vlcVideoLibrary.play(rtspUrl);

                    pause.setImageResource(R.drawable.pause);

                    txt.setVisibility(View.GONE);

                    pb.setVisibility(View.VISIBLE);


                }
            }
        });

    }

    @Override
    public void onComplete() {

        pb.setVisibility(View.GONE);

        offline.setVisibility(View.GONE);

        pause.setImageResource(R.drawable.pause);

        Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onError() {
        Toast.makeText(CameraActivity.this, "Error, make sure your endpoint is correct", Toast.LENGTH_SHORT).show();

        vlcVideoLibrary.stop();

        pause.setImageResource(R.drawable.play);

        pb.setVisibility(View.GONE);

        offline.setVisibility(View.VISIBLE);

    }

//    @Override
//    public void onBuffering(MediaPlayer.Event event) {
//
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        vlcVideoLibrary.stop();

    }

    @Override
    public void onClick(View view) {
        if (!vlcVideoLibrary.isPlaying()) {
            vlcVideoLibrary.play(rtspUrl);
        } else {
            vlcVideoLibrary.stop();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        vlcVideoLibrary.stop();

        pb.setVisibility(View.GONE);

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }
}
