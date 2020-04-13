package feuerkoenig.mathKeyboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.github.kexanie.library.MathView;

public class sampleActivity extends AppCompatActivity {
    public Keyboard keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        MathView mv_formula = findViewById(R.id.mv_formula);

        keyboard = findViewById(R.id.kb);
        keyboard.set_view(mv_formula);
    }
}
