package org.e38.sergi.memory.logic;

import android.content.Context;
import android.graphics.drawable.Drawable;

import org.e38.sergi.memory.R;

import java.io.IOException;
import java.io.Serializable;
import java.util.NoSuchElementException;

public class Carta implements Serializable {
    private transient Drawable realImage;
    private transient Drawable hidedImage;

    private int cartImageId;
    private boolean destapada = false;
    private boolean solved = false;
    private transient Context context;

    public Carta(Context context, int cartImageId) throws NoSuchElementException {
        this.cartImageId = cartImageId;
        this.context = context;
        hidedImage = AndroidVersionUtils.getDrawale(context, R.drawable.back);//metodo para unificar differences entre apis < 21 y apis > 22
        realImage = AndroidVersionUtils.getDrawale(context, cartImageId).mutate();
    }

    public void destarpar() {
        destapada = true;
    }

    public void tapar() {
        if (!solved) {//no es pot tapar una carta solucionada
            destapada = false;
        }
    }

    public boolean isDestapada() {
        return destapada;
    }

    public Drawable getImage() {
        return isDestapada() ? realImage : hidedImage;
    }

    public int getCartImageId() {
        return cartImageId;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved() {
        this.solved = true;
        realImage.setAlpha(0);//work on all vercions
    }

    public void onRestore(Context context) {
        this.context = context;
        hidedImage = AndroidVersionUtils.getDrawale(context, R.drawable.back);
        realImage = AndroidVersionUtils.getDrawale(context, cartImageId).mutate();
        if (solved) {
            setSolved();
        }
    }
}
