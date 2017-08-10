package saleh.ma.mostafa.gmail.com.appdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import saleh.ma.mostafa.gmail.com.advancedplacepicker.activities.AdvancedPlacePicker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_open_picker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(AdvancedPlacePicker.buildIntent(MainActivity.this, false), 4);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4 && resultCode == RESULT_OK) {
            String address = data.getParcelableExtra(AdvancedPlacePicker.ADDRESS).toString();
            Toast.makeText(this, address, Toast.LENGTH_LONG).show();
        }
    }
}
