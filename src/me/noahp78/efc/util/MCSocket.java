package me.noahp78.efc.util;

import me.noahp78.efc.net.NetworkingState;
import sun.nio.cs.UTF_32;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Created by noahp78 on 29-11-2017.
 */
public class MCSocket {
    ByteBuffer in;
    ByteBuffer out;
    int currentOffsetInPackage = 0;
    private NetworkingState state;
    private SocketChannel socket;


    private static final String statusResponse = "{\n" +
            "    \"version\": {\n" +
            "        \"name\": \"1.12\",\n" +
            "        \"protocol\": 345\n" +
            "    },\n" +
            "    \"players\": {\n" +
            "        \"max\": 100,\n" +
            "        \"online\": 5,\n" +
            "        \"sample\": [\n" +
            "            {\n" +
            "                \"name\": \"thinkofdeath\",\n" +
            "                \"id\": \"4566e69f-c907-48ee-8d71-d7ba5aa00d20\"\n" +
            "            }\n" +
            "        ]\n" +
            "    },\t\n" +
            "    \"description\": {\n" +
            "        \"text\": \"Hello world\"\n" +
            "    },\n" +
            "    \"favicon\": \"data:image/png;base64,<data>\"\n" +
            "}";


    public MCSocket(SocketChannel socket) {
        this.socket = socket;
        this.in = ByteBuffer.allocate(2048);
        this.state = NetworkingState.HANDSHAKE;

        try {
            while (true) {
                int result;
                while ((result = socket.read(in)) > 0) {
                    System.out.println(result + " bytes");
                    //Set in to read
                    in.flip();
                    int length = readVarInt();

                    int packet = readVarInt();
                    byte[] data = in.array();

                    if (packet == 0x00 && state == NetworkingState.HANDSHAKE) {
                        int protoVersion = readVarInt();
                        Console.d("We are talking about a login packet with length of " + length + " and protocol version " + protoVersion);

                        //Determine HostName used
                        int hostnameLength = readVarInt();
                        Console.d("HostnameLength = " + hostnameLength);
                        byte[] hostNameStringData = new byte[hostnameLength];
                        for (int i = currentOffsetInPackage; i < currentOffsetInPackage+hostnameLength; i++) {
                            byte c = data[i];
                            hostNameStringData[i-currentOffsetInPackage] = c;
                        }
                        currentOffsetInPackage+=hostnameLength;
                        String hostname = new String(hostNameStringData);
                        Console.d("Got Hostname " + hostname);

                        //Get Requested Port
                        short port=(short)( ((data[currentOffsetInPackage]&0xFF)<<8) | (data[currentOffsetInPackage+1]&0xFF) );
                        Console.d("port = " + port);
                        //We read 2 bytes, so increase offset
                        currentOffsetInPackage+=2;
                        int state = readVarInt();

                        Console.d("Client Requested State " + state);

                        if(state==1){
                            this.state = NetworkingState.STATUS;
                            //Prepare Response
                        }

                    } else {
                        //We don't care about the first 2 numbers


                        Console.d("We now have a buffer with a message of " + length + " bytes long, packet is " + packet + ", buffer is @ " + in.arrayOffset());
                        Console.d("AD= " + Arrays.toString(in.array()));

                    }

                    in.clear();
                    in.flip();
                    currentOffsetInPackage = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public int readVarInt() throws Exception {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            //Read a byte from the socket

            read = in.get(currentOffsetInPackage);
            currentOffsetInPackage++;
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }


}
