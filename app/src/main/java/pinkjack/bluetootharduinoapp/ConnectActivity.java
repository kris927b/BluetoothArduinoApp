package pinkjack.bluetootharduinoapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class ConnectActivity extends AppCompatActivity {

    EditText edt1;
    EditText edt2;
    EditText edt3;
    EditText edt4;
    public String text;
    public final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice BTDevice;
    BluetoothSocket BTSocket;
    OutputStream BTOutput;
    InputStream BTInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        if (bluetoothAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Bluetooth")
                    .setMessage("Your phone does not support bluetooth, Sorry!")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ConnectActivity.this.finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        edt1 = (EditText) findViewById(R.id.codeField);
        edt1.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt1.setTransformationMethod(PasswordTransformationMethod.getInstance());
        edt2 = (EditText) findViewById(R.id.codeField1);
        edt2.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt2.setTransformationMethod(PasswordTransformationMethod.getInstance());
        edt3 = (EditText) findViewById(R.id.codeField2);
        edt3.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt3.setTransformationMethod(PasswordTransformationMethod.getInstance());
        edt4 = (EditText) findViewById(R.id.codeField3);
        edt4.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt4.setTransformationMethod(PasswordTransformationMethod.getInstance());

        edt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt1.getText().length() == 1) {
                    edt1.clearFocus();
                    edt2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt2.getText().length() == 1) {
                    edt2.clearFocus();
                    edt3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt3.getText().length() == 1) {
                    edt3.clearFocus();
                    edt4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt4.getText().length() == 1) {
                    edt4.clearFocus();
                    SendInfo();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void SendInfo() {
        text = edt1.getText().toString() + edt2.getText().toString() + edt3.getText().toString() + edt4.getText().toString();
        Log.d("text", text);

        final ProgressDialog progressDialog = new ProgressDialog(ConnectActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Validating...");
        progressDialog.show();

        Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
        if (pairedDevice.size() > 0) {
            for (BluetoothDevice device : pairedDevice) {
                if (device.getName().equals("HC-06")) {
                    BTDevice = device;
                    break;
                }
            }
        }

        try {
            BTSocket = BTDevice.createRfcommSocketToServiceRecord(UUID.randomUUID());
        } catch (IOException e) {
            Log.d("Exception", e.getMessage());
        }

        bluetoothAdapter.cancelDiscovery();
        try {
            BTSocket.connect();
        } catch (IOException connectException) {
            Log.d("Connect Exception", connectException.getMessage());
            try {
                BTSocket.close();
            } catch (IOException closeException) {
                Log.d("Close Exception", closeException.getMessage());
            }
        }

        try {
            BTOutput = BTSocket.getOutputStream();
            BTInput = BTSocket.getInputStream();
        } catch (IOException OutputInput) {
            Log.d("OutputInput Exception",OutputInput.getMessage());
        }

        try {
            BTOutput.write(text.getBytes());
        } catch (IOException write) {
            Log.d("Write exception", write.getMessage());
        }

        /*
        Intent intent;
        intent = new Intent(this, Second_activity.class);
        startActivity(intent);
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
