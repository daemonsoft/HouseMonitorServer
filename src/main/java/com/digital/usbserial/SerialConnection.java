package com.digital.usbserial;

import com.google.cloud.firestore.Firestore;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.util.Date;

public class SerialConnection implements SerialPortEventListener {

    String uuid = "ufeLO5ycZvPbtXuG4jmEeow7cBV2"; //identificador de usuario de firebase
    private SerialPort serialPort;
    private Firestore db;
    String command = "";

    public SerialConnection(SerialPort serialPort, Firestore db) {
        this.serialPort = serialPort;
        this.db = db;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        Comsuption comsuption;

        if (event.isRXCHAR()) {//If data is available
            //if (event.getEventValue() == 10) {//Check bytes count in the input buffer
            //Read data, if 10 bytes available
            try {
                command = command + serialPort.readString();

                //System.out.println(command);
                command = command.replace("\n", "").replace("\r", "");
                for (String sub: command.split("\\{")) {
                    sub = sub.replace("{", "").replace("}", "");
                    //System.out.println(sub);
                    comsuption = new Comsuption();
                    comsuption.setDate(new Date());

                    try {
                        if (sub.startsWith("P")) {
                            comsuption.setValue(Double.parseDouble(sub.replace("P", "")));
                            db.collection(uuid + "/house/energy-comsuption").document().create(comsuption);
                            command = "";
                        } else if (sub.startsWith("F")) {
                            comsuption.setValue(Double.parseDouble(sub.replace("F", "")));
                            db.collection(uuid + "/house/water-comsuption").document().create(comsuption);
                            command = "";
                        }
                    } catch (NumberFormatException nfe) {

                        //System.out.println("bad comsuption");
                    }
                }
//                    byte buffer[] = serialPort.readBytes(10);
//                    byte[] bytes = new byte[buffer.length];
//
//                    for (int i = 0, len = bytes.length; i < len; i++) {
//                        System.out.println((char)buffer[i]);
//                    }
            } catch (SerialPortException ex) {
                System.out.println(ex);
            }
            //}
        } else if (event.isCTS()) {//If CTS line has changed state
            if (event.getEventValue() == 1) {//If line is ON
                System.out.println("CTS - ON");
            } else {
                System.out.println("CTS - OFF");
            }
        } else if (event.isDSR()) {///If DSR line has changed state
            if (event.getEventValue() == 1) {//If line is ON
                System.out.println("DSR - ON");
            } else {
                System.out.println("DSR - OFF");
            }
        }
    }

}
