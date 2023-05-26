package Client;

import Connection.*;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private Connection connection;
    private static ClientModel model;
    private static ViewGuiClient gui;
    private volatile boolean isConnected = false;

    public boolean isConnected() {
        return isConnected;
    }
    public void setConnected(boolean connected) {
        isConnected = connected;
    }
    public static void main(String[] args) {
        Client client = new Client();
        model = new ClientModel();
        gui = new ViewGuiClient(client);
        gui.initClientFrame();
        while (true) {
            if (client.isConnected()) {
                client.registerUsername();
                client.receiveMessagesFromServer();
                client.setConnected(false);
            }
        }
    }
    protected void connectToServer() {
        if (!isConnected) {
            while (true) {
                try {
                    String addressServer = gui.getServerAddressFromOptionPane();
                    int port = gui.getPortServerFromOptionPane();
                    Socket socket = new Socket(addressServer, port);
                    connection = new Connection(socket);
                    isConnected = true;
                    gui.addMessage("Service message: You have connected to the server.\n");
                    break;
                } catch (Exception e) {
                    gui.errorDialogWindow("An error has occurred! You may have entered an incorrect server address or port. Try again");
                    break;
                }
            }
        } else gui.errorDialogWindow("You are already connected!");
    }
    protected void registerUsername() {
        while (true) {
            try {
                Message message = connection.receive();

                if (message.getTypeMessage() == MessageType.REQUEST_NAME_USER) {
                    String username = gui.getUsername();
                    connection.send(new Message(MessageType.USER_NAME, username));
                }

                if (message.getTypeMessage() == MessageType.NAME_USED) {
                    gui.errorDialogWindow("The username is already in use. Please choose another one.");
                    String username = gui.getUsername();
                    connection.send(new Message(MessageType.USER_NAME, username));
                }

                if (message.getTypeMessage() == MessageType.NAME_ACCEPTED) {
                    gui.addMessage("Service message: Your username is accepted!\n");
                    model.setUsers(message.getListUsers());
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                gui.errorDialogWindow("An error occurred while registering the username. Please try reconnecting.");
                try {
                    connection.close();
                    isConnected = false;
                    break;
                } catch (IOException ex) {
                    gui.errorDialogWindow("Error closing the connection.");
                }
            }
        }
    }
    protected void sendMessageToServer(String text) {
        try {
            connection.send(new Message(MessageType.TEXT_MESSAGE, text));
        } catch (Exception e) {
            gui.errorDialogWindow("Error when sending a message");
        }
    }
    protected void receiveMessagesFromServer() {
        while (isConnected) {
            try {
                Message message = connection.receive();
                if (message.getTypeMessage() == MessageType.TEXT_MESSAGE) {
                    gui.addMessage(message.getTextMessage());
                }
                if (message.getTypeMessage() == MessageType.USER_ADDED) {
                    model.addUsers(message.getTextMessage());
                    gui.refreshUserList(model.getUsers());
                    gui.addMessage(String.format("Service message: User %s has joined the chat.\n", message.getTextMessage()));
                }
                if (message.getTypeMessage() == MessageType.REMOVED_USER) {
                    model.removeUsers(message.getTextMessage());
                    gui.refreshUserList(model.getUsers());
                    gui.addMessage(String.format("Service message: User %s has left the chat.\n", message.getTextMessage()));
                }
            } catch (Exception e) {
                gui.errorDialogWindow("Error when receiving a message from the server.");
                setConnected(false);
                gui.refreshUserList(model.getUsers());
                break;
            }
        }
    }
    protected void disconnectClient() {
        try {
            if (isConnected) {
                connection.send(new Message(MessageType.DISCONNECT_USER));
                model.getUsers().clear();
                isConnected = false;
                gui.refreshUserList(model.getUsers());
            } else gui.errorDialogWindow("You are already disconnected.");
        } catch (Exception e) {
            gui.errorDialogWindow("Service message: An error occurred while disconnecting.");
        }
    }
}
