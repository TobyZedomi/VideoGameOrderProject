package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import network.TCPNetworkLayer;

import javax.swing.*;

public class ClientThread implements Runnable {


    protected TCPNetworkLayer networkLayer;
    protected TCPGUIClient tcpguiClient;

    protected Gson gson;

    public ClientThread(TCPNetworkLayer networkLayer, TCPGUIClient tcpguiClient) {

        this.networkLayer = networkLayer;
        this.tcpguiClient = tcpguiClient;
        this.gson = new Gson();
    }

    public void run(){

        if (networkLayer != null){

            try{
                while (true){
                    String response = networkLayer.receive();

                    if (response == null){
                        break;
                    }

                    JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

                    if (jsonResponse == null || !jsonResponse.has("status")){
                        continue;
                    }

                    String status = jsonResponse.get("status").getAsString();

                    SwingUtilities.invokeLater(() ->{
                        tcpguiClient.responseMessagesFromServer(status,jsonResponse);
                    });

                    System.out.println(response);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
