package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import client.Message;

public class Main {
    private static final int PORT = 34522;

    public static void main(String[] args) {
        ServerSocket servSock = null;
        try {
            servSock = new ServerSocket(PORT);
            System.out.println("Server started!");
        } catch (IOException e) {
            System.out.println("Exception during socket creation.");
            System.exit(1);
        }
        listen(servSock);
    }

    private static void listen (ServerSocket servSock) {
        Message receivedMsg;
        Message sentMsg = null;
        FileStorage files = new FileStorage();

        while (true) {
            try (
                    Socket socket = servSock.accept();
                    DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
                    DataInputStream inStream = new DataInputStream(socket.getInputStream())
            ) {
                int length = inStream.readInt();
                byte[] message = new byte[length];
                inStream.readFully(message, 0, message.length);
                receivedMsg = (Message) SerializeObject.bytesToObject(message);
                receivedMsg.printMsg();
                if (receivedMsg.cmd.equals("exit")) {
                    cmdExit(servSock, files);
                } else {
                    sentMsg = files.parseRequest(receivedMsg);
                }
                assert sentMsg != null;
                sendMsg(outStream, sentMsg);
            } catch (IOException e) {
                System.out.println("Exception during listening.");
                e.printStackTrace();
            }
        }

    }

    private static void cmdExit(ServerSocket servSock,FileStorage files){
        try {
            servSock.close();
        } catch (IOException e) {
            System.out.println("Exception during closing server.");
        }
        files.saveFiles();
        System.exit(0);
    }
    private static void sendMsg(DataOutputStream output, Message sentMsg) {
        try {
            output.writeInt(sentMsg.getBytes().length);
            output.write(sentMsg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}