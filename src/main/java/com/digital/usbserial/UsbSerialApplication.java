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

        // [START fs_initialize_project_id]
        FirestoreOptions firestoreOptions =
                FirestoreOptions.getDefaultInstance().toBuilder()
                        .setProjectId("housemonitorapp")
                        .build();
        Firestore db = firestoreOptions.getService();
        // [END fs_initialize_project_id]

        db.collection("devices");


        SpringApplication.run(UsbSerialApplication.class, args);

        String[] portNames = SerialPortList.getPortNames();

        for (int i = 0; i < portNames.length; i++) {

            System.out.println("puertos");
            System.out.println(portNames[i]);
        }
        SerialPort serialPort = new SerialPort("/dev/ttyACM0");// ttyUSB0  ttyACM0
        serialConnection = new SerialConnection(serialPort);

        try {
            serialPort.openPort();//Open serial port
            System.out.println("puerto abierto " + serialPort.getPortName());

            serialPort.setParams(57600, 8, 1, 0);//Set params
            //int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;//Prepare mask
            //serialPort.setEventsMask(mask);//Set mask
            serialPort.addEventListener(serialConnection);//Add SerialPortEventListener

            db.collection("devices").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value,
                                    @Nullable FirestoreException e) {
                    if (e != null) {
                        System.err.println("Listen failed:" + e);
                        return;
                    }

                    List<Device> devices = new ArrayList<>();
                    Device deviceFireStore;
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("name") != null) {
                            deviceFireStore = doc.toObject(Device.class);
                            deviceFireStore.setId(doc.getId());
                            devices.add(deviceFireStore);
                        }
                    }
                    for (Device device : devices) {
                        try {
                            System.out.println(device.getId());
                            if (device.getId().equals("livingroomligth")) {
                                if (device.getStatus() == 0)
                                    serialPort.writeString("210");
                                else
                                    serialPort.writeString("211");

                            }else if (device.getId().equals("maindoor"))
                                serialPort.writeString("11");
                        } catch (SerialPortException e1) {
                            e1.printStackTrace();
                        }
                        System.out.println("device " + device.getName() + device.getStatus());
                        //Log.d(TAG, "Current cites in CA: " + device.getId());
                        //if ("maindoor".equals(device.getId())) {
                        //    mainDoorButton.setChecked(0 != device.getStatus());
                        // } else if ("livingroomligth".equals(device.getId())) {
                        //     livingRoomSwitch.setChecked(0 != device.getStatus());
                        //}
                    }
                }
            });


        } catch (SerialPortException ex) {
            System.out.println(ex);
        }

    }
}
