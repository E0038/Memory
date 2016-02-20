package org.e38.sergi.memory.controlers;

import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

/**
 * Created by sergi on 2/20/16.
 */
public abstract class AbstractMemoryActivity extends AppCompatActivity {

    public abstract GridView getMemoryGridView();

    public abstract void refreshItems();

    public abstract void onPartidaEnd();
}
