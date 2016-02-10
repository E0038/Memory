package org.e38.sergi.memory.controlers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import org.e38.sergi.memory.R;


public class MainActivity extends AppCompatActivity {
    public static final int FACIL = 1, NORMAL = 2, DIFICIL = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.buttonGameStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dificultat;
                if (((RadioButton) findViewById(R.id.radioButtonFacil)).isChecked())
                    dificultat = FACIL;
                else if (((RadioButton) findViewById(R.id.radioButtonNormal)).isChecked())
                    dificultat = NORMAL;
                else if (((RadioButton) findViewById(R.id.radioButtonDificil)).isChecked())
                    dificultat = DIFICIL;
                else {
                    Toast.makeText(MainActivity.this, "seleciona una dificultat", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, GameMemoryActivity.class);
                intent.putExtra(GameMemoryActivity.KEY_DIFICULTAT, dificultat);
                startActivity(intent);
            }
        });
    }
}
