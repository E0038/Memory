package org.e38.sergi.memory.controlers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.e38.sergi.memory.R;
import org.e38.sergi.memory.logic.Carta;
import org.e38.sergi.memory.logic.Partida;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GameMemoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String KEY_DIFICULTAT = "DIFICULTAT";

    private GridView memoryGridView;
    private Partida partida;
    private DisplayMetrics metrics;
    private int cardWith, cardHeight;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_memory);
        initConfiguration();
        findViewById(R.id.progressBarGameLoading).setVisibility(View.GONE);
    }

    private void initConfiguration() {
        configurePartida();
        configureGridView();
        startTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();//destruir el timer para evitar que siga vivo despes de ser destruida
    }

    private void startTimer() {
        timer = new Timer(partida.getNivel().getSegundos() * 1000, (TextView) findViewById(R.id.textTimeLeft)).start();
    }

    private void configurePartida() {
        switch (getIntent().getExtras().getInt(KEY_DIFICULTAT)) {
            case MainActivity.FACIL:
                partida = Partida.Dificultat.FACIL.getNewPartida(this);
                break;
            case MainActivity.NORMAL:
                partida = Partida.Dificultat.NORMAL.getNewPartida(this);
                break;
            case MainActivity.DIFICIL:
                partida = Partida.Dificultat.DIFICIL.getNewPartida(this);
                break;
            default:
                Toast.makeText(this, "dificulat invalida", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void configureGridView() {
        memoryGridView = (GridView) findViewById(R.id.gridViewMemory);
        memoryGridView.setNumColumns(partida.getNivel().getCols());

        metrics = new DisplayMetrics();
        ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        cardWith = (int) (metrics.widthPixels / (partida.getNivel().getCols() * 0.9));
        cardHeight = (metrics.heightPixels / (partida.getNivel().getNumCartas() / partida.getNivel().getCols()));
        MemoryAdapter adapter = new MemoryAdapter(this, partida.getCartes());
        memoryGridView.setAdapter(adapter);
        memoryGridView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        partida.click(position);
        refreshItems();
    }

    public void refreshItems() {
        memoryGridView.setAdapter(new MemoryAdapter(this, partida.getCartes()));//refresh
        memoryGridView.refreshDrawableState();
    }

    public void onFinalize() {
        timer.cancel();
        if (!(this.isFinishing() || isDestroyed())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.estado) + (partida.isWined() ? getString(R.string.wined) : getString(R.string.loss)));
            builder.setTitle(R.string.endGame);
            builder.setNeutralButton(R.string.newGameBtt, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    partida = partida.getNivel().getNewPartida(GameMemoryActivity.this);
                    configureGridView();
                    startTimer();
                }
            });
            builder.show();
        }
    }

    private class MemoryAdapter extends ArrayAdapter<Carta> {

        private MemoryAdapter(Context context, Carta[] objects) {
            super(context, R.layout.memory_cell, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(GameMemoryActivity.this);
            View item = inflater.inflate(R.layout.memory_cell, null);
            item.setLayoutParams(new GridView.LayoutParams(cardWith, cardHeight));
            ImageView image = (ImageView) item.findViewById(R.id.imageViewCart);
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            image.setImageDrawable(partida.getCarta(position).getImage());
            return item;
        }
    }

    private class Timer extends CountDownTimer {
        private final DateFormat formater = new SimpleDateFormat("mm:ss");
        private TextView target;

        /**
         * @param millisInFuture The number of millis in the future from the call
         *                       to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                       is called.
         */
        public Timer(long millisInFuture, TextView target) {
            super(millisInFuture, 1000);
            this.target = target;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Date date = new Date(millisUntilFinished);
            target.setText(String.format(getString(R.string.timeLeft), formater.format(date)));
        }

        @Override
        public void onFinish() {
            target.setText(String.format(getString(R.string.timeLeft), "0"));// sino se queda en 1
            partida.finalizar();
        }
    }
}