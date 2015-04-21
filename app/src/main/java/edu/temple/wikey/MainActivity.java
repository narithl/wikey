package edu.temple.wikey;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

    Connection connection;
    private String ipAddressStr = "", portNumberStr = "";
    private ImageView networkIcon;
    private LinearLayout firstRow, secondRow, thirdRow, fourthRow, fifthRow;
    private Button leftShift, rightShift, leftCtrl, rightCtrl, leftAlt, rightAlt;
    private ImageView arrowLeft, arrowUp, arrowDown, arrowRight;
    private Button leftClick, rightClick;
    private ToggleButton onOff;
    private TextView connectionStatus;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("savedData", MODE_PRIVATE);
        ipAddressStr = sharedPreferences.getString("ipAddress", "");
        portNumberStr = sharedPreferences.getString("portNumber", "");

        //Initialize connection status
        connectionStatus = (TextView)findViewById(R.id.connection_status);

        //Initialize connection
        getConnection();

        //mouseSpeed();
        iniMouse();
        mouseSpeed();

        //Caps Lock
        capsLockButton();

        //Initialize shift, ctrl, and alt keys
        iniShift();
        iniCtrl();
        iniAlt();

        //Initialize all key
        rowOnClick();

        //Initialize ON/OFF switch
        toggleOnOff();
    }

    @Override
    public void onStop(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ipAddress", ipAddressStr);
        editor.putString("portNumber", portNumberStr);
        editor.commit();
        if(connection != null)
            if(connection.hasConnection())
                connection.closeConnection();
        super.onStop();
    }

    /*
    ** Implementing alt key
     */
    public void iniAlt(){
        leftAlt = (Button)findViewById(R.id.alt_left);
        rightAlt = (Button)findViewById(R.id.alt_right);
        altButton(leftAlt);altButton(rightAlt);
    }

    /*
    ** Implementing ctrl key
     */
    public void iniCtrl(){
        leftCtrl = (Button)findViewById(R.id.ctrl_left);
        rightCtrl = (Button)findViewById(R.id.ctrl_right);
        ctrlButton(leftCtrl);ctrlButton(rightCtrl);
    }

    /*
    ** Implementing shift key
     */
    public void iniShift(){
        leftShift = (Button)findViewById(R.id.shift_left);
        rightShift = (Button)findViewById(R.id.shift_right);
        shiftButton(leftShift);shiftButton(rightShift);
    }

    /*
    ** Alt key onclick action
     */
    public void altButton(final Button button){
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button.isActivated()){
                    leftAlt.setActivated(false);
                    rightAlt.setActivated(false);
                    leftAlt.setTextColor(Color.BLACK);
                    rightAlt.setTextColor(Color.BLACK);
                    if(hasConnection())
                        connection.sendKeyboardEvent(18);
                }
                else {
                    leftAlt.setActivated(true);
                    rightAlt.setActivated(true);
                    leftAlt.setTextColor(Color.BLUE);
                    rightAlt.setTextColor(Color.BLUE);
                    if(hasConnection())
                        connection.sendKeyboardEvent(18);
                }
            }
        });
    }

    /*
    ** Ctrl key onclick action
     */
    public void ctrlButton(final Button button){
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button.isActivated()){
                    leftCtrl.setActivated(false);
                    rightCtrl.setActivated(false);
                    leftCtrl.setTextColor(Color.BLACK);
                    rightCtrl.setTextColor(Color.BLACK);
                    if(hasConnection())
                        connection.sendKeyboardEvent(17);
                }
                else {
                    leftCtrl.setActivated(true);
                    rightCtrl.setActivated(true);
                    leftCtrl.setTextColor(Color.BLUE);
                    rightCtrl.setTextColor(Color.BLUE);
                    if(hasConnection())
                        connection.sendKeyboardEvent(17);
                }
            }
        });
    }

    /*
** Shift key onclick action
 */
    public void shiftButton(final Button button){
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button.isActivated()){
                    shiftReleased();
                    leftShift.setActivated(false);
                    rightShift.setActivated(false);
                    leftShift.setTextColor(Color.BLACK);
                    rightShift.setTextColor(Color.BLACK);
                }
                else {
                    shiftPressed();
                    leftShift.setActivated(true);
                    rightShift.setActivated(true);
                    leftShift.setTextColor(Color.BLUE);
                    rightShift.setTextColor(Color.BLUE);
                }
            }
        });
    }

    public void mouseListener(final ImageView imageView, Button button, final String keyEvent) {
        if(imageView != null) {
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasConnection())
                        connection.sendMouseEvent(keyEvent);
                }
            });
        }
        if(button != null)
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(hasConnection())
                        connection.sendMouseEvent(keyEvent);
                }
            });
    }

    /*
    ** Initialize mouse
     */
    public void iniMouse(){
        arrowLeft = (ImageView)findViewById(R.id.left_arrow);
        arrowUp = (ImageView)findViewById(R.id.up_arrow);
        arrowDown = (ImageView)findViewById(R.id.down_arrow);
        arrowRight = (ImageView)findViewById(R.id.right_arrow);
        leftClick = (Button)findViewById(R.id.left_click);
        rightClick = (Button)findViewById(R.id.right_click);
        mouseListener(arrowLeft, null, "l");
        mouseListener(arrowUp, null, "u");
        mouseListener(arrowDown, null, "d");
        mouseListener(arrowRight, null, "r");
        mouseListener(null, leftClick, "left");
        mouseListener(null, rightClick, "right");
    }

    /*
    ** onClickListener for keyboard
    */
    public void keyboardOnClick(Button button, final int eventKey) {
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasConnection())
                    connection.sendKeyboardEvent(eventKey);
            }
        });
    }

    /*
    ** Implementing caps lock button
     */
    public void capsLockButton(){
        final Button button = (Button)findViewById(R.id.capslock);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button.isActivated()){
                    button.setActivated(false);
                    button.setTextColor(Color.BLACK);
                    if(hasConnection())
                        connection.sendKeyboardEvent(20);
                }
                else{
                    button.setActivated(true);
                    button.setTextColor(Color.BLUE);
                    if(hasConnection())
                        connection.sendKeyboardEvent(20);
                }
            }
        });
    }

    /*
    ** Set action for each key
    */
    public void rowOnClick(){
        //Initialize all linear layout rows
        firstRow = (LinearLayout)findViewById(R.id.first_row);
        secondRow = (LinearLayout)findViewById(R.id.second_row);
        thirdRow = (LinearLayout)findViewById(R.id.third_row);
        fourthRow = (LinearLayout)findViewById(R.id.fourth_row);
        fifthRow = (LinearLayout)findViewById(R.id.fifth_row);

        for(int i=0; i<firstRow.getChildCount(); i++){
            Button button = (Button)firstRow.getChildAt(i);
            keyboardOnClick(button, getResources().getIntArray(R.array.vk_first_row)[i]);
        }
        for(int i=0; i<secondRow.getChildCount(); i++){
            Button button = (Button)secondRow.getChildAt(i);
            keyboardOnClick(button, getResources().getIntArray(R.array.vk_second_row)[i]);
        }
        for(int i=1; i<thirdRow.getChildCount(); i++){
            Button button = (Button)thirdRow.getChildAt(i);
            keyboardOnClick(button, getResources().getIntArray(R.array.vk_third_row)[i-1]);
        }
        for(int i=1; i<fourthRow.getChildCount()-1; i++){
            Button button = (Button)fourthRow.getChildAt(i);
            keyboardOnClick(button, getResources().getIntArray(R.array.vk_fourth_row)[i-1]);
        }
        Button windowsKey = (Button)findViewById(R.id.windows);
        windowsKey.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasConnection())
                    connection.sendKeyboardEvent(524);
            }
        });
        Button spaceKey = (Button)findViewById(R.id.space);
        spaceKey.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasConnection())
                    connection.sendKeyboardEvent(32);
            }
        });
        Button leftArrowKey = (Button)findViewById(R.id.left_key);
        leftArrowKey.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasConnection())
                    connection.sendKeyboardEvent(37);
            }
        });
        Button rightArrowKey = (Button)findViewById(R.id.right_key);
        rightArrowKey.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasConnection())
                    connection.sendKeyboardEvent(39);
            }
        });
        Button upArrowKey = (Button)findViewById(R.id.up_key);
        upArrowKey.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasConnection())
                    connection.sendKeyboardEvent(38);
            }
        });
        Button downArrowKey = (Button)findViewById(R.id.down_key);
        downArrowKey.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasConnection())
                    connection.sendKeyboardEvent(40);
            }
        });
    }

    /**
     * When shift key is pressed, some keys' value change
     */
    public void shiftPressed(){
        if(hasConnection())
                connection.sendKeyboardEvent(16);
        for(int i=0; i<firstRow.getChildCount()-1; i++){
            Button keyButton = (Button)firstRow.getChildAt(i);
            keyButton.setText(String.valueOf(getResources().getText(R.string.first_row_shift).charAt(i)));
        }
        for(int i=11, j=0; i<secondRow.getChildCount(); i++, j++){
            Button keyButton = (Button)secondRow.getChildAt(i);
            keyButton.setText(String.valueOf(getResources().getText(R.string.second_row_shift).charAt(j)));
        }
        for(int i=10, j=0; i<thirdRow.getChildCount()-1; i++, j++){
            Button keyButton = (Button)thirdRow.getChildAt(i);
            keyButton.setText(String.valueOf(getResources().getText(R.string.third_row_shift).charAt(j)));
        }
        for(int i=8, j=0; i<fourthRow.getChildCount()-1; i++, j++){
            Button keyButton = (Button)fourthRow.getChildAt(i);
            keyButton.setText(String.valueOf(getResources().getText(R.string.fourth_row_shift).charAt(j)));
        }
    }

    /*
    ** When shift key is released, those keys' value change back
    */
    public void shiftReleased(){
        if(hasConnection())
                connection.sendKeyboardEvent(16);
        for(int i=0; i<firstRow.getChildCount()-1; i++){
            Button keyButton = (Button)firstRow.getChildAt(i);
            keyButton.setText(String.valueOf(getResources().getText(R.string.first_row).charAt(i)));
        }
        for(int i=11,j=0; i<secondRow.getChildCount(); i++,j++){
            Button keyButton = (Button)secondRow.getChildAt(i);
            keyButton.setText(String.valueOf(getResources().getText(R.string.second_row).charAt(j)));
        }
        for(int i=10,j=0; i<thirdRow.getChildCount()-1; i++,j++){
            Button keyButton = (Button)thirdRow.getChildAt(i);
            keyButton.setText(String.valueOf(getResources().getText(R.string.third_row).charAt(j)));
        }
        for(int i=8,j=0; i<fourthRow.getChildCount()-1; i++,j++){
            Button keyButton = (Button)fourthRow.getChildAt(i);
            keyButton.setText(String.valueOf(getResources().getText(R.string.fourth_row).charAt(j)));
        }
    }

    public void mouseSpeed(){
        final Button mouseSpeed = (Button)findViewById(R.id.speed);
        mouseSpeed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mouseSpeed.getText().toString().equals("x1")) {
                    mouseSpeed.setText("x2");
                    if(hasConnection())
                        connection.sendMouseEvent("20");
                }
                else if(mouseSpeed.getText().toString().equals("x2")) {
                    mouseSpeed.setText("x3");
                    if(hasConnection())
                        connection.sendMouseEvent("30");
                }
                else if(mouseSpeed.getText().toString().equals("x3")) {
                    mouseSpeed.setText("x4");
                    if (hasConnection())
                        connection.sendMouseEvent("40");
                }
                else if(mouseSpeed.getText().toString().equals("x4")) {
                    mouseSpeed.setText("x5");
                    if (hasConnection())
                        connection.sendMouseEvent("50");
                }
                else {
                    mouseSpeed.setText("x1");
                    if(hasConnection())
                        connection.sendMouseEvent("10");
                }
            }
        });
    }

    public void toggleOnOff(){
        onOff = (ToggleButton)findViewById(R.id.onoff);
        onOff.setEnabled(false);
        onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    if (connection.hasConnection()) {
                        connection.closeConnection();
                        onOff.setEnabled(false);
                        connectionStatus.setText("No Connection");
                        connectionStatus.setTextColor(Color.RED);
                    }
                }
            }
        });
    }

    public boolean hasConnection(){
        if(connection != null)
            if(connection.hasConnection())
                return true;
        return false;
    }

    /*
    ** Implementing network connection
     */
    public void getConnection() {
        networkIcon = (ImageView) findViewById(R.id.network);
        networkIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());

                alert.setTitle("Enter IP Address and Port Number:\n");

                // Set an EditText view to get user input
                final EditText ipAddress = new EditText(v.getContext());
                ipAddress.setText(ipAddressStr);
                ipAddress.setHint("IP Address");
                final EditText portNumber = new EditText(v.getContext());
                portNumber.setText(portNumberStr);
                portNumber.setHint("Port number");
                final LinearLayout input = new LinearLayout(v.getContext());
                input.setOrientation(LinearLayout.VERTICAL);
                input.addView(ipAddress);
                input.addView(portNumber);
                alert.setView(input);

                alert.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ipAddressStr = ipAddress.getText().toString();
                        portNumberStr = portNumber.getText().toString();
                        if(connection != null)
                            if(connection.hasConnection())
                                connection.closeConnection();
                        connection = new Connection(ipAddressStr, portNumberStr);
                        Thread thread = new Thread(connection);
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }
                        if (connection.hasConnection()) {
                            onOff.setChecked(true);
                            onOff.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                            connectionStatus.setText("Connected");
                            connectionStatus.setTextColor(Color.GREEN);
                            Button mouseSpeed = (Button)findViewById(R.id.speed);
                            connection.sendMouseEvent(String.valueOf(Integer.valueOf(mouseSpeed.getText().toString().substring(1))*10));
                        } else {
                            Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();
            }
        });
    }
}
