package com.example.lesson8musicplayer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;


import android.os.Bundle;

import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.io.IOException;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    // ----- Class constants -----------------------------------------------
    /**
     * Maximum number of simultaneous audio streams
     * <p>
     * Максимальное количество одновременно воспроизводимых звуковых потоков
     */
    private final static int SOUNDPOOL_MAX_STREAMS = 5;

    /**
     * Filename with Preference Data
     * <p>
     * Имя файла с Данными Предпочтений
     */
    private final static String APP_SHARED_PREFERENCES = "my_appl_preferences";

    /*
     * Keys for Shared Preferences
     *
     * Ключи для Данных Предпочтений
     * ---------------------------------------------------------------------
     */
    private final static String PREF_BELL_LEFT_VOLUME = "bellLeftVolume";
    private final static String PREF_BELL_RIGHT_VOLUME = "bellRightVolume";
    private final static String PREF_BELL_RATE = "bellRate";
    private final static String PREF_HAMMER_LEFT_VOLUME = "hammerLeftVolume";
    private final static String PREF_HAMMER_RIGHT_VOLUME = "hammerRightVolume";
    private final static String PREF_HAMMER_RATE = "hammerRate";


    // ----- Class members -------------------------------------------------
    /**
     * Reference to android.media.SoundPool object
     * <p>
     * Ссылка на объект android.media.SoundPool
     */
    private SoundPool SP;

    /**
     * soundID for audio file bell_sound.mp3
     * <p>
     * Идентификатор soundID для аудиофайла bell_sound.mp3
     */
    private int bellSoundID;

    /**
     * Audio stream identifier for bell_sound.mp3
     * <p>
     * Идентификатор звукового потока для bell_sound.mp3
     */
    private int bellStreamID;

    /**
     * Left speaker volume for bell_sound.mp3
     * <p>
     * Громкость левого динамика для bell_sound.mp3
     */
    private float bellLeftVolume = 0.5f;


    /**
     * Right speaker volume for bell_sound.mp3
     * <p>
     * Громкость правого динамика для bell_sound.mp3
     */
    private float bellRightVolume = 0.5f;

    /**
     * Playback rate for bell_sound.mp3
     * <p>
     * Скорость воспроизведения для bell_sound.mp3
     */
    private float bellRate = 1.0f;

    /**
     * soundID for audio file hammer_blows.mp3
     * <p>
     * Идентификатор soundID для аудиофайла hammer_blows.mp3
     */
    private int hammerSoundID;

    /**
     * Audio stream identifier for hammer_blows.mp3
     * <p>
     * Идентификатор звукового потока для hammer_blows.mp3
     */
    private int hammerStreamID;

    /**
     * Left speaker volume for hammer_blows.mp3
     * <p>
     * Громкость левого динамика для hammer_blows.mp3
     */
    private float hammerLeftVolume = 0.5f;


    /**
     * Right speaker volume for hammer_blows.mp3
     * <p>
     * Громкость правого динамика для hammer_blows.mp3
     */
    private float hammerRightVolume = 0.5f;

    /**
     * Playback rate for hammer_blows.mp3
     * <p>
     * Скорость воспроизведения для hammer_blows.mp3
     */
    private float hammerRate = 1.0f;

    /**
     * AlertDialog for the configuration of audio streams
     * <p>
     * AlertDialog для конфигурирования параметров звуковых потоков
     */
    private AlertDialog dialog;

    /**
     * Content view for AlertDialog
     * <p>
     * Виджет с содержимым для AlertDialog
     */
    private View dialogView;

    /**
     * To round off real numbers (two decimal places)
     * <p>
     * Для округления вещественных чисел (два знака после запятой)
     */
    private DecimalFormat decimalFormat = new DecimalFormat("##0.00");

    // ----- Class methods -------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----- Toolbar -------------------------------------------------------
        Toolbar toolBar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolBar);
        toolBar.setTitle("Music player");
        //	toolBar.setSubtitle("android.media.SoundPool");
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setSubtitleTextColor(Color.WHITE);

        // ----- Collapsing Title Text Color -----------------------------------
        CollapsingToolbarLayout mainCollapsing = this.findViewById(R.id.mainCollapsing);
        mainCollapsing.setCollapsedTitleTextColor(Color.WHITE);
        mainCollapsing.setExpandedTitleColor(Color.WHITE);

        /*
         * Восстановление настроек звуковых потоков из файла Данных Предпочтений
         */
        // ----- Получение объекта SharedPreferences ---------------------------
        SharedPreferences sharedPreferences = this.getSharedPreferences(
                MainActivity.APP_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);

        // ----- Bell Sound ----------------------------------------------------
        this.bellLeftVolume = sharedPreferences.getFloat(
                MainActivity.PREF_BELL_LEFT_VOLUME, 0.5f);
        this.bellRightVolume = sharedPreferences.getFloat(
                MainActivity.PREF_BELL_RIGHT_VOLUME, 0.5f);
        this.bellRate = sharedPreferences.getFloat(
                MainActivity.PREF_BELL_RATE, 1.0f);

        // ----- Hammer Blows ---------------------------------------------------
        this.hammerLeftVolume = sharedPreferences.getFloat(
                MainActivity.PREF_HAMMER_LEFT_VOLUME, 0.5f);
        this.hammerRightVolume = sharedPreferences.getFloat(
                MainActivity.PREF_HAMMER_RIGHT_VOLUME, 0.5f);
        this.hammerRate = sharedPreferences.getFloat(
                MainActivity.PREF_HAMMER_RATE, 1.0f);


        // ----- Create SoundPool object ---------------------------------------
        // ----- Создание объекта SoundPool ------------------------------------
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(MainActivity.SOUNDPOOL_MAX_STREAMS);
            AudioAttributes AA = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            builder.setAudioAttributes(AA);

            this.SP = builder.build();
        } else {
            this.SP = new SoundPool(
                    MainActivity.SOUNDPOOL_MAX_STREAMS,
                    AudioManager.STREAM_MUSIC,
                    0);
        }

        /*
         * Loading audio files to a SoundPool object
         *
         * Загрузка аудиофайлов в объект SoundPool
         * ---------------------------------------------------------------------
         */
        // ----- Loading bell_sound.mp3 from assets ----------------------------
        // ----- Загрузка аудиофайла bell_sound.mp3 из каталога assets ---------
        try {
            this.bellSoundID = this.SP.load(getAssets().openFd("bell_sound.mp3"), 1);
        } catch (IOException e) {
            Toast.makeText(this,
                    "Error loading bell_sound.mp3 : " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

            findViewById(R.id.ivOne).setVisibility(View.INVISIBLE);
        }

        // ----- Loading hammer_blows.mp3 from /res/raw ------------------------
        // ----- Загрузка аудиофайла hammer_blows.mp3 из каталога /res/raw -----
        this.hammerSoundID = this.SP.load(this, R.raw.hammer_blows, 1);

        /*
         * Creating AlertDialog for the configuration of audio streams
         *
         * Создание AlertDialog для конфигурации звуковых потоков
         * ---------------------------------------------------------------------
         */
        // ----- Create a widget set for the Configuration Dialog --------------
        // ----- Создание набора виджетов для Диалога конфигурации -------------
        this.dialogView = this.getLayoutInflater().inflate(R.layout.config_dialog, null);

        // ----- Bell Sound ----------------------------------------------------
        final TextView tvBellLeftVolume = this.dialogView.findViewById(R.id.tvBellLeftVolume);
        tvBellLeftVolume.setText(String.valueOf(this.bellLeftVolume));

        final TextView tvBellRightVolume = this.dialogView.findViewById(R.id.tvBellRightVolume);
        tvBellRightVolume.setText(String.valueOf(this.bellRightVolume));

        final TextView tvBellRate = this.dialogView.findViewById(R.id.tvBellRate);
        tvBellRate.setText(String.valueOf(this.bellRate));

        // ----- Hammer Blows --------------------------------------------------
        final TextView tvHammerLeftVolume = this.dialogView.findViewById(R.id.tvHammerLeftVolume);
        tvHammerLeftVolume.setText(String.valueOf(this.hammerLeftVolume));

        final TextView tvHammerRightVolume = this.dialogView.findViewById(R.id.tvHammerRightVolume);
        tvHammerRightVolume.setText(String.valueOf(this.hammerRightVolume));

        final TextView tvHammerRate = this.dialogView.findViewById(R.id.tvHammerRate);
        tvHammerRate.setText(String.valueOf(this.hammerRate));

        // ----- SeekBar.OnSeekBarChangeListener -------------------------------
        SeekBar.OnSeekBarChangeListener SBCL = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(@NonNull SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getId() == R.id.sbBellLeftVolume) {
                    float value = progress / 100f;
                    tvBellLeftVolume.setText(String.valueOf(value));
                } else if (seekBar.getId() == R.id.sbBellRightVolume) {
                    float value = progress / 100f;
                    tvBellRightVolume.setText(String.valueOf(value));
                } else if (seekBar.getId() == R.id.sbBellRate) {
                    float value = 0.5f + (progress * 0.015f);
                    tvBellRate.setText(decimalFormat.format(value));
                } else if (seekBar.getId() == R.id.sbHammerLeftVolume) {
                    float value = progress / 100f;
                    tvHammerLeftVolume.setText(String.valueOf(value));
                } else if (seekBar.getId() == R.id.sbHammerRightVolume) {
                    float value = progress / 100f;
                    tvHammerRightVolume.setText(String.valueOf(value));
                } else if (seekBar.getId() == R.id.sbHammerRate) {
                    float value = 0.5f + (progress * 0.015f);
                    tvHammerRate.setText(decimalFormat.format(value));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        // ----- Bell Sound ----------------------------------------------------
        final SeekBar sbBellLeftVolume = this.dialogView.findViewById(R.id.sbBellLeftVolume);
        sbBellLeftVolume.setProgress((int) (this.bellLeftVolume * 100));
        sbBellLeftVolume.setOnSeekBarChangeListener(SBCL);

        final SeekBar sbBellRightVolume = this.dialogView.findViewById(R.id.sbBellRightVolume);
        sbBellRightVolume.setProgress((int) (this.bellRightVolume * 100));
        sbBellRightVolume.setOnSeekBarChangeListener(SBCL);

        final SeekBar sbBellRate = this.dialogView.findViewById(R.id.sbBellRate);
        sbBellRate.setProgress((int) ((this.bellRate - 0.5f) / 0.015f));
        sbBellRate.setOnSeekBarChangeListener(SBCL);

        // ----- Hammer Blows --------------------------------------------------
        final SeekBar sbHammerLeftVolume = this.dialogView.findViewById(R.id.sbHammerLeftVolume);
        sbHammerLeftVolume.setProgress((int) (this.hammerLeftVolume * 100));
        sbHammerLeftVolume.setOnSeekBarChangeListener(SBCL);

        final SeekBar sbHammerRightVolume = this.dialogView.findViewById(R.id.sbHammerRightVolume);
        sbHammerRightVolume.setProgress((int) (this.hammerRightVolume * 100));
        sbHammerRightVolume.setOnSeekBarChangeListener(SBCL);

        final SeekBar sbHammerRate = this.dialogView.findViewById(R.id.sbHammerRate);
        sbHammerRate.setProgress((int) ((this.hammerRate - 0.5f) / 0.015f));
        sbHammerRate.setOnSeekBarChangeListener(SBCL);


        // ----- AlertDialog.Builder -------------------------------------------
        AlertDialog.Builder builder = new AlertDialog.Builder(
                this,
                android.R.style.Theme_DeviceDefault_Light_Dialog);

        builder.setView(this.dialogView);


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // ----- Bell Sound ----------------------------------------------------
                sbBellLeftVolume.setProgress((int) (MainActivity.this.bellLeftVolume * 100));
                sbBellRightVolume.setProgress((int) (MainActivity.this.bellRightVolume * 100));
                sbBellRate.setProgress((int) ((MainActivity.this.bellRate - 0.5f) / 0.015f));

                // ----- Hammer Blows --------------------------------------------------
                sbHammerLeftVolume.setProgress((int) (MainActivity.this.hammerLeftVolume * 100));
                sbHammerRightVolume.setProgress((int) (MainActivity.this.hammerRightVolume * 100));
                sbHammerRate.setProgress((int) ((MainActivity.this.hammerRate - 0.5f) / 0.015f));

            }
        });

        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // ----- Bell Sound ----------------------------------------------------
                MainActivity.this.bellLeftVolume = sbBellLeftVolume.getProgress() / 100f;
                MainActivity.this.bellRightVolume = sbBellRightVolume.getProgress() / 100f;
                MainActivity.this.bellRate = 0.5f + sbBellRate.getProgress() * 0.015f;

                // ----- Hammer Blows --------------------------------------------------
                MainActivity.this.hammerLeftVolume = sbHammerLeftVolume.getProgress() / 100f;
                MainActivity.this.hammerRightVolume = sbHammerRightVolume.getProgress() / 100f;
                MainActivity.this.hammerRate = 0.5f + sbHammerRate.getProgress() * 0.015f;

            }
        });


        this.dialog = builder.create();
    }

    public void btnClick(View v) {
        if (v.getId() == R.id.ivOne) {
            this.bellStreamID = this.SP.play(
                    this.bellSoundID,
                    this.bellLeftVolume,
                    this.bellRightVolume,
                    1,
                    0,
                    this.bellRate);
        } else if (v.getId() == R.id.ivTwo) {
            this.hammerStreamID = this.SP.play(
                    this.hammerSoundID,
                    this.hammerLeftVolume,
                    this.hammerRightVolume,
                    1,
                    0,
                    this.hammerRate);
        }

    }

    public void fabClick(View v) {
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();

        // ----- Get the SharedPreferences -------------------------------------
        // ----- Получение объекта SharedPreferences ---------------------------
        SharedPreferences sharedPreferences = this.getSharedPreferences(
                MainActivity.APP_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        // ----- Save Bell Sound Preferences -----------------------------------
        // ----- Сохранение настроек для Bell Sound ----------------------------
        editor.putFloat(MainActivity.PREF_BELL_LEFT_VOLUME, this.bellLeftVolume);
        editor.putFloat(MainActivity.PREF_BELL_RIGHT_VOLUME, this.bellRightVolume);
        editor.putFloat(MainActivity.PREF_BELL_RATE, this.bellRate);

        // ----- Save Hammer Blows Preferences ---------------------------------
        // ----- Сохранение настроек для Hammer Blows --------------------------
        editor.putFloat(MainActivity.PREF_HAMMER_LEFT_VOLUME, this.hammerLeftVolume);
        editor.putFloat(MainActivity.PREF_HAMMER_RIGHT_VOLUME, this.hammerRightVolume);
        editor.putFloat(MainActivity.PREF_HAMMER_RATE, this.hammerRate);

        editor.apply();

    }
}