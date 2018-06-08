package com.digital.usbserial;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialConnection implements SerialPortEventListener {

    private SerialPort serialPort;

    public SerialConnection(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR()) {//If data is available
            //if (event.getEventValue() == 10) {//Check bytes count in the input buffer
                //Read data, if 10 bytes available
                try {
                    System.out.println(serialPort.readString());
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
