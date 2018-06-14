package com.digital.usbserial;


import com.google.api.client.util.Lists;
import com.google.api.gax.paging.Page;
import com.google.cloud.firestore.*;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.annotations.Nullable;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.ardulink.core.serial.jssc.SerialInputStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.google.auth.oauth2.GoogleCredentials;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
public class UsbSerialApplication {

    static SerialConnection serialConnection;

    public static void main(String[] args) throws IOException {

        String uuid = "ufeLO5ycZvPbtXuG4jmEeow7cBV2"; //identificador de usuario de firebase

        // [START fs_initialize_project_id]
        FirestoreOptions firestoreOptions =
                FirestoreOptions.getDefaultInstance().toBuilder()
                        .setProjectId("housemonitorapp")
                        .build();
        Firestore db = firestoreOptions.getService();
        // [END fs_initialize_project_id]

        db.collection("devices");

        List<Device> devices = new ArrayList<>();
        SpringApplication.run(UsbSerialApplication.class, args);

        String[] portNames = SerialPortList.getPortNames();

        for (int i = 0; i < portNames.length; i++) {

            System.out.println("puertos");
            System.out.println(portNames[i]);
        }
        SerialPort serialPort = new SerialPort("/dev/ttyACM0");// ttyUSB0  ttyACM0
        serialConnection = new SerialConnection(serialPort,db);

        try {
            serialPort.openPort();//Open serial port
            System.out.println("puerto abierto " + serialPort.getPortName());

            serialPort.setParams(57600, 8, 1, 0);//Set params
            //int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;//Prepare mask
            //serialPort.setEventsMask(mask);//Set mask
            serialPort.addEventListener(serialConnection);//Add SerialPortEventListener

            db.collection(uuid +"/house/devices").document("maindoor").addSnapshotListener((value, e) -> {
                if (e != null) {
                    System.err.println("Listen failed:" + e);
                    return;
                }


                Device deviceFireStore;


                deviceFireStore = value.toObject(Device.class);
                deviceFireStore.setId(value.getId());
                devices.add(deviceFireStore);


                //System.out.println("device " + value.get("name") + value.get("status"));

                try {
                    //System.out.println(device.getId());
                    if (deviceFireStore.getId().equals("maindoor")) {
                        if (deviceFireStore.getStatus() == 0)
                            serialPort.writeString("{10}");
                        else
                            serialPort.writeString("{11}");

                    }
                } catch (SerialPortException e1) {
                    e1.printStackTrace();
                }
            });
            db.collection(uuid +"/house/devices").document("livingroomligth").addSnapshotListener((value, e) -> {

                if (e != null) {
                    System.err.println("Listen failed:" + e);
                    return;
                }


                Device deviceFireStore;
                deviceFireStore = value.toObject(Device.class);
                deviceFireStore.setId(value.getId());
                devices.add(deviceFireStore);
                //System.out.println("device " + value.get("name") + value.get("status"));
                try {
                    //System.out.println(device.getId());
                    if (deviceFireStore.getId().equals("livingroomligth")) {
                        if (deviceFireStore.getStatus() == 0)
                            serialPort.writeString("{210}");
                        else
                            serialPort.writeString("{211}");

                    }
                } catch (SerialPortException e1) {
                    e1.printStackTrace();
                }


                //System.out.println("device " + device.getName() + device.getStatus());
                //Log.d(TAG, "Current cites in CA: " + device.getId());
                //if ("maindoor".equals(device.getId())) {
                //    mainDoorButton.setChecked(0 != device.getStatus());
                // } else if ("livingroomligth".equals(device.getId())) {
                //     livingRoomSwitch.setChecked(0 != device.getStatus());
                //}

            });

            db.collection(uuid +"/house/devices").document("mainroomligth").addSnapshotListener((value, e) -> {

                if (e != null) {
                    System.err.println("Listen failed:" + e);
                    return;
                }


                Device deviceFireStore;
                deviceFireStore = value.toObject(Device.class);
                deviceFireStore.setId(value.getId());
                devices.add(deviceFireStore);
                //System.out.println("device " + value.get("name") + value.get("status"));
                try {
                    //System.out.println(device.getId());
                    if (deviceFireStore.getId().equals("mainroomligth")) {
                        if (deviceFireStore.getStatus() == 0)
                            serialPort.writeString("{220}");
                        else
                            serialPort.writeString("{221}");

                    }
                } catch (SerialPortException e1) {
                    e1.printStackTrace();
                }


                //System.out.println("device " + device.getName() + device.getStatus());
                //Log.d(TAG, "Current cites in CA: " + device.getId());
                //if ("maindoor".equals(device.getId())) {
                //    mainDoorButton.setChecked(0 != device.getStatus());
                // } else if ("livingroomligth".equals(device.getId())) {
                //     livingRoomSwitch.setChecked(0 != device.getStatus());
                //}

            });


        } catch (SerialPortException ex) {
            System.out.println(ex);
        }

    }
}
