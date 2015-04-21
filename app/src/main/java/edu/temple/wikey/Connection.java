package edu.temple.wikey;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Narith on 4/3/2015.
 */
public class Connection implements Runnable{

    private Socket socket;
    private String ipAddress;
    private String portNumber;
    private OutputStream outputStream;
    private DataOutputStream out;

    public Connection(String ip, String port){
        ipAddress = ip;
        portNumber = port;
    }

    @Override
    public void run(){
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ipAddress, Integer.parseInt(portNumber)), 3000);
            outputStream = socket.getOutputStream();
            out = new DataOutputStream(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasConnection(){
        return socket.isConnected();
    }

    public void sendMouseEvent(String message) {
        try{
            out.writeUTF("mouse");
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendKeyboardEvent(int keyEvent){
        try {
            out.writeUTF("keyboard");
            out.writeInt(keyEvent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try {
            socket.close();
        }catch(IOException ie){ie.printStackTrace();}
    }
}