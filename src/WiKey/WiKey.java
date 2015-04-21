package WiKey;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.IOException;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.Point;
import java.awt.MouseInfo;

public class WiKey {

    private ServerSocket serverSocket;
    private Robot robot;
    private boolean shiftPressed = false;
    private boolean ctrlPressed = false;
    private boolean altPressed = false;
    private int mouseSpeed = 10;
    private static int defaultPort = 6077;
    final private int shiftKeyValue = 16, ctrlKeyValue = 17, altKeyValue = 18;
    
    public static void main(String[] args) {

        if (args.length == 1) {
            defaultPort = Integer.valueOf(args[0]);
        }

        if (isPortInUse(defaultPort)) {
            System.out.println("Port number is already taken.");
        } else {
            try {
                WiKey wiKey = new WiKey(defaultPort);
                wiKey.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isPortInUse(int port) {
        try {
            new ServerSocket(port).close();
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    public WiKey(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                reset();
                System.out.println("Waiting on port " + serverSocket.getLocalPort() + "...");
                Socket server = serverSocket.accept();
                System.out.println("Connected to " + server.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(server.getInputStream());
                while (!server.isClosed()) {
                    if (in.readUTF().equals("mouse")) {
                        mouse(in.readUTF());
                    } else {
                        keyboard(in.readInt());
                    }
                }
                server.close();
            } catch (IOException e) {
                System.out.println("Connection closed!");
            }
        }
    }

    public void changeMouseSpeed(int speed) {
        mouseSpeed = speed;
    }

    public void reset() {
        mouseSpeed = 10;
        robot.keyRelease(shiftKeyValue);
        shiftPressed = false;
        robot.keyRelease(ctrlKeyValue);
        ctrlPressed = false;
        robot.keyRelease(altKeyValue);
        altPressed = false;
    }

    private void keyboard(int keyEvent) {
        switch (keyEvent) {
            case shiftKeyValue:
                if (shiftPressed) {
                    robot.keyRelease(keyEvent);
                    shiftPressed = false;
                } else {
                    robot.keyPress(keyEvent);
                    shiftPressed = true;
                }
                break;
            case ctrlKeyValue:
                if (ctrlPressed) {
                    robot.keyRelease(keyEvent);
                    ctrlPressed = false;
                } else {
                    robot.keyPress(keyEvent);
                    ctrlPressed = true;
                }
                break;
            case altKeyValue:
                if (altPressed) {
                    robot.keyRelease(keyEvent);
                    altPressed = false;
                } else {
                    robot.keyPress(keyEvent);
                    altPressed = true;
                }
                break;
            default:
                robot.keyPress(keyEvent);
                robot.keyRelease(keyEvent);
        }
    }

    private void mouse(String mouseEvent) {
        Point p = new Point();
        switch (mouseEvent) {
            case "l":
                p = MouseInfo.getPointerInfo().getLocation();
                robot.mouseMove(p.x - mouseSpeed, p.y);
                break;
            case "r":
                p = MouseInfo.getPointerInfo().getLocation();
                robot.mouseMove(p.x + mouseSpeed, p.y);
                break;
            case "u":
                p = MouseInfo.getPointerInfo().getLocation();
                robot.mouseMove(p.x, p.y - mouseSpeed);
                break;
            case "d":
                p = MouseInfo.getPointerInfo().getLocation();
                robot.mouseMove(p.x, p.y + mouseSpeed);
                break;
            case "left":
                robot.mousePress(InputEvent.BUTTON1_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
                break;
            case "right":
                robot.mousePress(InputEvent.BUTTON3_MASK);
                robot.mouseRelease(InputEvent.BUTTON3_MASK);
                break;
            default:
                mouseSpeed = Integer.valueOf(mouseEvent);
        }
    }
}



