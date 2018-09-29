package gnu.io.examples;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TwoWaySerialComm {
    private static final String PORT = "COM3";

    private TwoWaySerialComm() {
        super();
    }

    private void connect(String portName) throws Exception {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();

                (new Thread(new SerialReader(in))).start();
                (new Thread(new SerialWriter(out))).start();

            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
    }

    /** */
    public static class SerialReader implements Runnable {
        InputStream in;

        SerialReader(InputStream in) {
            this.in = in;
        }

        public void run() {
            System.out.println("Will read some bytes from the serial.");

            try {
                byte[] buffer = new byte[1024];
                int len;
                while ((in.available() > 0) && ((len = in.read(buffer)) > -1)) {
                    System.out.print(new String(buffer, 0, len));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("\nFinished reading bytes bytes from the serial.");
        }
    }

    /** */
    public static class SerialWriter implements Runnable {
        OutputStream out;

        SerialWriter(OutputStream out) {
            this.out = out;
        }

        public void run() {
            try {
                int c;
                while ((c = System.in.read()) > -1) {
                    System.out.printf("Sending %d to the serial port.\n", c);
                    this.out.write(c);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("\nFinished writing to the serial port.");
        }
    }

    public static void main(String[] args) {
        try {
            (new TwoWaySerialComm()).connect(PORT);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}