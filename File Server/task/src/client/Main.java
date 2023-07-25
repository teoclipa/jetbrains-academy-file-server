package client;

import server.SerializeObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    private static final int PORT = 34522;
    private static final String SERV_IP = "127.0.0.1";
    public static Path path;

    static {
        path = Path.of(System.getProperty("user.dir"), "src", "client", "data");
    }

    public static void main(String[] args) {
        Message response;
        Message sentMsg;
        try (Scanner scan = new Scanner(System.in); Socket socket = new Socket(SERV_IP, PORT); DataOutputStream output = new DataOutputStream(socket.getOutputStream()); DataInputStream input = new DataInputStream(socket.getInputStream())) {
            sentMsg = createMessage(scan, output);
            sendMsg(output, sentMsg);
            System.out.println("The request was sent.");
            response = receiveMessage(input);
            System.out.println(response.response);
            if (sentMsg.cmd.equals("GET") && !response.response.equals("The response says that this file is not found!"))
                SerializeObject.downloadFile("client", response, path);
        } catch (Exception e) {
            System.out.println("Connection refused.");
        }
    }

    private static Message receiveMessage(DataInputStream input) throws IOException {
        int length = input.readInt();
        byte[] message = new byte[length];
        input.readFully(message, 0, message.length);
        return (Message) SerializeObject.bytesToObject(message);
    }

    private static Message createMessage(Scanner scan, DataOutputStream output) {
        Message sentMsg = new Message();
        sentMsg.cmd = getUsersInput(scan, "Enter action (1 - get a file, 2 - create a file, 3 - delete a file): ");
        sentMsg.cmd = sentMsg.cmd.trim().toLowerCase(Locale.ROOT);
        switch (sentMsg.cmd) {
            case "exit":
                sentMsg.cmd = "exit";
                sendMsg(output, sentMsg);
                System.exit(0);
            case "1":
            case "3":
                if (sentMsg.cmd.equals("1")) sentMsg.cmd = "GET";
                else sentMsg.cmd = "DELETE";
                indexOrName(scan, sentMsg);
                break;
            case "2":
                sentMsg.cmd = "PUT";
                sentMsg.filename = getUsersInput(scan, "Enter name of the file: ");
                sentMsg.newName = getUsersInput(scan, "Enter name of the file to be saved on server: ");
                SerializeObject.sendFile(sentMsg, path);
                if (sentMsg.response.equals("The response says that this file is not found!")) {
                    System.out.println("File is not found!");
                    System.exit(0);
                }
                break;
            default:
                System.out.println("Incorrect request.");
                System.exit(0);
        }
        return sentMsg;
    }

    private static void sendMsg(DataOutputStream output, Message sentMsg) {
        try {
            output.writeInt(sentMsg.getBytes().length);
            output.write(sentMsg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getUsersInput(Scanner scan, String prompt) {
        System.out.print(prompt);
        return scan.nextLine();
    }

    private static void indexOrName(Scanner scan, Message sentMsg) {
        String res;
        res = getUsersInput(scan, "Do you want to " + sentMsg.cmd.toLowerCase(Locale.ROOT) + " the file by name or by id (1 - name, 2 - id): ");
        while (!res.equals("1") && !res.equals("2")) {
            System.out.println("Incorrect input.");
            res = getUsersInput(scan, "Do you want to " + sentMsg.cmd.toLowerCase(Locale.ROOT) + "the file by name or by id (1 - name, 2 - id): ");
        }
        sentMsg.nameOrId = Integer.parseInt(res);
        if (sentMsg.nameOrId == 2) {
            sentMsg.id = Integer.parseInt(getUsersInput(scan, "Enter id: "));
        } else {
            sentMsg.filename = getUsersInput(scan, "Enter name of the file: ");
        }
    }
}