package org.e38.sergi.memory.logic;

import android.os.AsyncTask;

import org.e38.sergi.memory.R;
import org.e38.sergi.memory.controlers.GameMemoryActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Partida implements Serializable {


    public static final int ESTADO_ENCURSO = 0,
            ESTADO_TERMINADA = 1,// finalizado por el usario pero no perdida, no esta en uso
            ESTADO_GANADA = 2, ESTADO_PERDIDA = 3;
    private final Object clickLocker = new Object();
    private Integer estado;
    private List<Carta> cartas;
    private Dificultat nivel;
    private List<Integer> solvedIdx;
    private List<Integer> cardClickIdx;
    private GameMemoryActivity activity;
    private int[] cartasIds = new int[]{//max: 24
            R.drawable.c0, R.drawable.c1,
            R.drawable.c2, R.drawable.c3,
            R.drawable.c4, R.drawable.c5,
            R.drawable.c6, R.drawable.c7,
            R.drawable.c8, R.drawable.c9,
            R.drawable.c10, R.drawable.c11
    };

    private Partida(Dificultat dificultat, GameMemoryActivity activity) {
        this.nivel = dificultat;
        estado = ESTADO_ENCURSO;
        this.activity = activity;
        cartas = new ArrayList<>();
        solvedIdx = new ArrayList<>(nivel.getNumCartas());
        cardClickIdx = new ArrayList<>(2);
        repartirCartar();
    }

    public Carta[] getCartes() {
        Carta[] carts = cartas.toArray(new Carta[cartas.size()]);
        return carts;
    }

    public Carta getCarta(int position) {
        return cartas.get(position);
    }

    public Dificultat getNivel() {
        return nivel;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    private void repartirCartar() {
        for (int i = 0; i < nivel.numCartas; i++) {
            cartas.add(new Carta(activity, cartasIds[i / 2]));
        }
        Collections.shuffle(cartas);
    }

    public void finalizar() {
        if (estado == ESTADO_ENCURSO) {
            estado = isWined() ? ESTADO_GANADA : ESTADO_PERDIDA;
        }
        activity.onPartidaEnd();
    }

    public void stop() {
        setEstado(ESTADO_TERMINADA);
    }

    /**
     * calcula si la partida esta ganada
     * Este metodo ningun asigna el estado aunque este ganada
     *
     * @return
     */
    public synchronized boolean isWined() {
        return solvedIdx.size() == cartas.size();
    }

    public Integer getEstado() {
        return estado;
    }

    public void click(final int position) {
        cartas.get(position).destarpar();// destapa la carta
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                synchronized (clickLocker) {// wait others clicks , ya no hace falte
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //esto se ejecuta en el mainThread
                __click(position);
                if (isWined()) {
                    finalizar();
                }
            }
        }.execute();//esto hace que los clicks se procesen después de la actualizar el grid, cuando el mainThread vuelva a estar libre
    }


    private void __click(int position) {
        //no destarpar ninguna carta aqui ya an sido destapadas en el metodo publico
        if (solvedIdx.contains(position)) {
        } else if (cardClickIdx.size() < 1) {//primera carta
            cardClickIdx.add(position);
        } else {//segunda
            segundaCarta(position);
        }
    }

    private final android.os.Handler segundaCartaHandler = new android.os.Handler();

    private void segundaCarta(int position) {
        if (cardClickIdx.get(0) != position) {// si la carta es diferente sino la ignoramos
            segundaCartaHandler.removeCallbacksAndMessages(null);//eliminar todos los pendientes
            Carta primera = cartas.get(cardClickIdx.get(0));
            Carta segona = cartas.get(position);
            if (primera.getCartImageId() == segona.getCartImageId()) {
                primera.setSolved();
                segona.setSolved();
                solvedIdx.add(cartas.indexOf(primera));
                solvedIdx.add(cartas.indexOf(segona));
            } else {
                primera.tapar();
                segona.tapar();
                //actualizar a los 2s
                segundaCartaHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.refreshItems();
                    }
                }, 2000);
            }
            cardClickIdx.clear();
        }
    }

    public enum Dificultat {
        FACIL(12, 200, 3),
        NORMAL(20, 120, 4),
        DIFICIL(24, 90, 4);
        //nota: numCartas tiene que ser par y divisile por el numero de columnas
        private int numCartas, segundos, cols;

        Dificultat(int numCartas, int segundos, int cols) {
            this.numCartas = numCartas;
            this.segundos = segundos;
            this.cols = cols;
        }

        public int getNumCartas() {
            return numCartas;
        }

        public Partida getNewPartida(GameMemoryActivity activity) {
            return new Partida(this, activity);
        }

        public int getSegundos() {
            return segundos;
        }

        public int getCols() {
            return cols;
        }
    }
}