package me.noahp78.efc;

import me.noahp78.efc.util.Console;
import me.noahp78.efc.util.MCSocket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Created by noahp78 on 29-11-2017.
 */
public class EFC {
    public static void main(String[] args) {
        //Simple
        try{
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

            serverSocketChannel.socket().bind(new InetSocketAddress(25565));

            while(true){
                SocketChannel socketChannel =
                        serverSocketChannel.accept();
                MCSocket mcSocket = new MCSocket(socketChannel);


                //do something with socketChannel...
            }



        }catch(Exception e){
            Console.d("Failed to listen on port 25565");
            e.printStackTrace();
        }


    }
}
