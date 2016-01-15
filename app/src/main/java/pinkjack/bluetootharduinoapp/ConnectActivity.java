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
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
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
    ProgressDialog progressDialog;
    AlertDialog alert;
    int lockoutTime = 30000;

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
                    progressDialog = new ProgressDialog(ConnectActivity.this, R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Validating...");
                    progressDialog.show();
                    findBT();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void findBT() {
        Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
        if (pairedDevice.size() > 0) {
            for (BluetoothDevice device : pairedDevice) {
                if (device.getName().equals("HC-06")) {
                    BTDevice = device;
                    Toast.makeText(getApplicationContext(),"BTDevice Found", Toast.LENGTH_LONG).show();
                    new ConnectThread().run(BTDevice);
                    break;
                }
            }
        }
    }

    private class ConnectThread extends Thread {
        BluetoothSocket tmp;

        public void run(BluetoothDevice device) {
            Log.d("Yes", "Yes");
            BTDevice = device;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                        tmp = BTDevice.createInsecureRfcommSocketToServiceRecord(uuid);
                        // Cancel discovery because it will slow down the connection
                        bluetoothAdapter.cancelDiscovery();
                        Class<?> clazz = tmp.getRemoteDevice().getClass();
                        Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
                        try {
                            Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                            Object[] params = new Object[]{
                                    Integer.valueOf(1)
                            };
                            BTSocket = (BluetoothSocket) m.invoke(tmp.getRemoteDevice(), params);
                            BTSocket.connect();
                        } catch (NoSuchMethodException ex1) {
                            Log.d("Method", ex1.getMessage());
                        } catch (IllegalAccessException ex2) {
                            Log.d("Access", ex2.getMessage());
                        } catch (InvocationTargetException ex3) {
                            Log.d("Invocation", ex3.getMessage());
                        }
                        Log.d("Yes", "Yes Connect");
                    } catch (IOException connectException) {
                        Log.d("Connect", connectException.getMessage());
                        try {
                            BTSocket.close();
                        } catch (IOException closeException) {
                            Log.d("Closeafterconnectproblm", closeException.getMessage());
                        }
                    }
                    try {
                        SendInfo();
                    } catch (IOException ex) {
                        Log.d("NO", ex.getMessage());
                    }
                }
            }).start();
        }
    }

    public void SendInfo() throws IOException {
        BTOutput = BTSocket.getOutputStream();
        text = edt1.getText().toString() + edt2.getText().toString() + edt3.getText().toString() + edt4.getText().toString();
        Log.d("text", text);
        BTOutput.write(text.getBytes());
        listenforResponse();
        }

    public void listenforResponse() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                int bytes;
                String response = null;
                try {
                    BTInput = BTSocket.getInputStream();
                } catch (IOException inex) {
                    Log.d("Input", inex.getMessage());
                }
                while (true) {
                    try {
                        bytes = BTInput.read(buffer);
                        response = new String(buffer, 0, bytes);
                    } catch (IOException readex) {
                        Log.d("Read", readex.getMessage());
                        break;
                    }
                }

                switch (response) {
                    case "true":
                        closeBTconnection();
                        break;
                    case "false":
                        setLockOut();
                        break;
                }
            }
        }).start();
    }

    public void setLockOut() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                alert = new AlertDialog.Builder(ConnectActivity.this)
                        .setTitle("Wrong Pin!")
                        .setMessage("Your alarm system will be locked in " + lockoutTime / 1000 + " sec")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        for (int Time = lockoutTime; lockoutTime >= 0; Time--) {
                            if (Time == 0) {
                                alert.dismiss();
                                edt1.setText("");
                                edt2.setText("");
                                edt3.setText("");
                                edt4.setText("");
                                break;
                            } else {
                                alert.setMessage("Your alarm system will be locked in " + Time + " sec");
                            }
                        }
                    }
                }, 0, 1000);
            }
        }).start();
    }

    public void closeBTconnection() {
        try {
            BTSocket.close();
            Toast.makeText(getApplicationContext(),"BTSocket closed!", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            /*
            Intent intent;
            intent = new Intent(this, Second_activity.class);
            startActivity(intent);
            */
        } catch (IOException close) {
            Log.d("Close", close.getMessage());
        }
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

