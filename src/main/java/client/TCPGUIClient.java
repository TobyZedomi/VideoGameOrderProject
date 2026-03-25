package client;


import com.google.gson.JsonObject;
import network.TCPNetworkLayer;
import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TCPGUIClient {

    // Provide networking functionality

    static JFrame f;

    //lists
    static JList b;

    private JLabel emailListLabel;

    private JLabel matchedLabel;


    private TCPNetworkLayer network;

    private ClientThread clientThread;
    private Thread thread;

    Gson gson = new Gson();

    // GUI components
    private final HashMap<String, Container> guiContainers = new HashMap<>();
    // Main gui window
    private JFrame mainFrame;
    // Main Font setting
    private Font font = new Font("Arial", Font.PLAIN, 16);

    // Panel for initial view
    private JPanel initialView;
    // Display for initial options - labels, text fields and button
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JTextField usernameField;
    private JTextField passwordField;
    private JButton loginButton;

    private JButton registerButton;


    // Panel for logged-in view
    private JPanel homePageView;
    private JButton homePageButton;

    private JButton logOut;

    private JButton goBackToHomePage;

    private JButton sendToOrderPageButton;


    private JButton sendToCancelOrderPageButton;


    private JButton viewOrdersPageButton;
    private JPanel registerView;
    private JButton registerViewButton;

    private JLabel usernameLabel1;

    private JTextField usernameTextField1;

    private JLabel passwordLabel1;


    private JTextField passwordTextField1;


    private JLabel confirmPasswordLabel1;


    private JTextField confirmPasswordTextField1;



    // put video game for sale

    private JPanel sendVideoGameOrderView;
    private JButton sendVideoGameOrderViewButton;

    private JLabel buyerOrSellerLabel;

    private JTextField buyerOrSellerTextField;
    private JLabel titleLabel;

    private JTextField titleTextField;

    private JLabel priceLabel;

    private JTextField priceTextField;


    // CancelOrder

    private JPanel cancelOrderView;
    private JButton cancelOrderViewButton;

    private JLabel cancelBuyerOrSellerLabel;

    private JTextField cancelBuyerOrSellerTextField;
    private JLabel cancelTitleLabel;

    private JTextField cancelTitleTextField;

    private JLabel cancelPriceLabel;

    private JTextField cancelPriceTextField;



    // view order




    // Use constructor to establish the components (parts) of the GUI
    public TCPGUIClient() {

        // Set up the main window
        configureMainWindow();

        // Set up the initial panel (the initial view on the system)
        // This takes in the username and password of the user
        configureInitialPanel();

        // Set up second panel
        configureHomePageView();

        // register view

        configureRegisterView();

        configureSendVideoOrderPanel();

        configureCancelOrderPanel();


    }

    private static GridBagConstraints getGridBagConstraints(int col, int row, int width) {
        // Create a constraints object to manage component placement within a frame/panel
        GridBagConstraints gbc = new GridBagConstraints();
        // Set it to fill horizontally (component will expand to fill width)
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Add padding around the component (Pad by 5 on all sides)
        gbc.insets = new Insets(5, 5, 5, 5);

        // Set the row position to the supplied value
        gbc.gridx = col;
        // Set the column position to the supplied value
        gbc.gridy = row;
        // Set the component's width to the supplied value (in columns)
        gbc.gridwidth = width;
        return gbc;
    }

    private void configureMainWindow() {
        // Create the main frame - this is the main window
        mainFrame = new JFrame("Basic Sample GUI");
        mainFrame.setSize(500, 400);
        // Set what should happen when the X button is clicked on the window
        // This approach will dispose of the main window but not shut down the program
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Set the layout manager used for the main window
        mainFrame.setLayout(new CardLayout());

        // Add a listener to the overall window that reacts when window close action is requested
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainFrame.dispose();
                System.exit(0);
            }
        });

        // Register the main window as a container in the system
        guiContainers.put("mainFrame", mainFrame);
    }

    // Set up initial panel (initial view)
    private void configureInitialPanel() {
        // Create and configure the config panel
        // This will provide a view to take in the user credentials
        // Use a GridBag layout so we have a grid to work with, but there's some flexibility (button can span columns)
        initialView = new JPanel(new GridBagLayout());
        // Register this panel as a container in the system
        guiContainers.put("initialView", initialView);

        // Create text fields and associated labels to take in username and password
        // Username info
        usernameLabel = new JLabel("Username: ");
        usernameField = new JTextField(15);

        // Password info
        passwordLabel = new JLabel("Password: ");
        passwordField = new JTextField(15);

        // Create a button to log in user
        loginButton = new JButton("Log in");
        // Specify what the button should DO when clicked:

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });


        // Create a button to register user
        registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });



        // Add credential components to initial view panel in specific positions within the gridbag
        // Add username label and text field on first row (y = 0)
        initialView.add(usernameLabel, getGridBagConstraints(0, 0, 1));
        initialView.add(usernameField, getGridBagConstraints(1, 0, 1));
        // Add password label and text field on second row (y = 1)
        initialView.add(passwordLabel, getGridBagConstraints(0, 1, 1));
        initialView.add(passwordField, getGridBagConstraints(1, 1, 1));

        // Add button on third row (y = 2) spanning two columns (width = 2)
        initialView.add(loginButton, getGridBagConstraints(0, 2, 2));

        initialView.add(registerButton, getGridBagConstraints(0, 3, 2));


        // Add empty space on fourth row (y = 3) spanning two columns (width = 2)
        initialView.add(new JPanel(), getGridBagConstraints(0, 4, 2));

    }

    private void configureHomePageView(){


        // Create and configure the config panel
        // This will provide a view to take in the user credentials
        // Use a GridBag layout so we have a grid to work with, but there's some flexibility (button can span columns)
        homePageView = new JPanel(new GridBagLayout());
        // Register this panel as a container in the system
        guiContainers.put("homePageView", homePageView);


        // send email


        sendToOrderPageButton = new JButton("Order Video Game");
        // Specify what the button should DO when clicked:
        sendToOrderPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendOrderPage();
            }
        });



        // cancel Order


        sendToCancelOrderPageButton= new JButton("Cancel Order");
        // Specify what the button should DO when clicked:
        sendToCancelOrderPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendToCancelOrderPage();
            }
        });




        // send email

        viewOrdersPageButton = new JButton("View Orders");
        // Specify what the button should DO when clicked:
        viewOrdersPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewAllOrders();
            }
        });


        // logout

        logOut = new JButton("Log Out");
        // Specify what the button should DO when clicked:
        logOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logOutUser();
            }
        });

        homePageView.add(sendToOrderPageButton, getGridBagConstraints(0, 2, 2));
        homePageView.add(viewOrdersPageButton, getGridBagConstraints(0, 3, 2));
        homePageView.add(sendToCancelOrderPageButton, getGridBagConstraints(0, 4, 2));
        homePageView.add(logOut, getGridBagConstraints(0, 5, 2));
    }

    private void showInitialView(){
        // Add config panel to the main window and make it visible
        mainFrame.add(initialView);
        mainFrame.setVisible(true);
    }

    private void showHomePageView(){

        // Add config panel to the main window and make it visible
        // mainFrame.remove(0);

        mainFrame.add(homePageView);
        mainFrame.setVisible(true);
    }




    // register View

    private void configureRegisterView(){
        // Create and configure the config panel
        // This will provide a view to take in the user credentials
        // Use a GridBag layout so we have a grid to work with, but there's some flexibility (button can span columns)
        registerView = new JPanel(new GridBagLayout());
        // Register this panel as a container in the system
        guiContainers.put("registerView", registerView);

        // Create text fields and associated labels to take in username and password
        // Username info
        usernameLabel1 = new JLabel("username: ");
        usernameTextField1 = new JTextField(15);

        passwordLabel1 = new JLabel("password: ");
        passwordTextField1 = new JTextField(15);


        confirmPasswordLabel1 = new JLabel("Confirm Password: ");
        confirmPasswordTextField1 = new JTextField(15);


        // Create a button to log in user
        registerViewButton = new JButton("Register");
        // Specify what the button should DO when clicked:
        registerViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setRegisterButton();
            }
        });


        logOut = new JButton("Go Back To Login");
        // Specify what the button should DO when clicked:
        logOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToLogin();
            }
        });

        // Add credential components to count view panel in specific positions within the gridbag
        // Add username label and text field on first row (y = 0)
        registerView.add(usernameLabel1, getGridBagConstraints(0, 0, 1));
        registerView.add(usernameTextField1, getGridBagConstraints(1, 0, 1));
        // Add password label and text field on second row (y = 1)
        registerView.add(passwordLabel1, getGridBagConstraints(0, 1, 1));
        registerView.add(passwordTextField1, getGridBagConstraints(1, 1, 1));

        //confirm password

        registerView.add(confirmPasswordLabel1, getGridBagConstraints(0, 2, 1));
        registerView.add(confirmPasswordTextField1, getGridBagConstraints(1, 2, 1));


        // Add button on third row (y = 2) spanning two columns (width = 2)
        registerView.add(registerViewButton, getGridBagConstraints(0, 3, 2));

        // Add button on third row (y = 2) spanning two columns (width = 2)
        registerView.add(logOut, getGridBagConstraints(0, 4, 2));
    }


    private void showRegisterView(){

        // Add config panel to the main window and make it visible
        // mainFrame.remove(0);

        mainFrame.add(registerView);
        mainFrame.setVisible(true);
    }





    /// put a video game up for sale


    private void configureSendVideoOrderPanel(){
        // Create and configure the config panel
        // This will provide a view to take in the user credentials
        // Use a GridBag layout so we have a grid to work with, but there's some flexibility (button can span columns)
        sendVideoGameOrderView = new JPanel(new GridBagLayout());
        // Register this panel as a container in the system
        guiContainers.put("sendVideoGameOrderView", sendVideoGameOrderView);

        // Create text fields and associated labels to take in username and password
        // Username info
        buyerOrSellerLabel = new JLabel("Buyer or Seller: ");
        buyerOrSellerTextField = new JTextField(15);

        titleLabel = new JLabel("Game Title: ");
        titleTextField = new JTextField(15);


        priceLabel = new JLabel("Price: ");
        priceTextField = new JTextField(15);



        // Create a button to log in user
        sendVideoGameOrderViewButton = new JButton("Send Video Game Order");
        // Specify what the button should DO when clicked:
        sendVideoGameOrderViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendVideoGameOrder();
            }
        });


        goBackToHomePage = new JButton("Go Back To Home Page");
        // Specify what the button should DO when clicked:
        goBackToHomePage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToHomePageSendVideoGameOrder();
            }
        });


        logOut = new JButton("LogOut");
        // Specify what the button should DO when clicked:
        logOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logOutSendVideoGameOrder();
            }
        });


        sendVideoGameOrderView.add(buyerOrSellerLabel, getGridBagConstraints(0, 0, 1));
        sendVideoGameOrderView.add(buyerOrSellerTextField, getGridBagConstraints(1, 0, 1));

        sendVideoGameOrderView.add(titleLabel, getGridBagConstraints(0, 1, 1));
        sendVideoGameOrderView.add(titleTextField, getGridBagConstraints(1, 1, 1));

        sendVideoGameOrderView.add(priceLabel, getGridBagConstraints(0, 2, 1));
        sendVideoGameOrderView.add(priceTextField, getGridBagConstraints(1, 2, 1));


        // Add button on third row (y = 2) spanning two columns (width = 2)
        sendVideoGameOrderView.add(sendVideoGameOrderViewButton, getGridBagConstraints(0, 3, 2));

        // Add button on third row (y = 2) spanning two columns (width = 2)
        sendVideoGameOrderView.add(goBackToHomePage, getGridBagConstraints(0, 4, 2));
        sendVideoGameOrderView.add(logOut, getGridBagConstraints(0, 5, 2));
    }


    private void showSendVideoGameOrderView(){

        // Add config panel to the main window and make it visible
        // mainFrame.remove(0);

        mainFrame.add(sendVideoGameOrderView);
        mainFrame.setVisible(true);
    }



    // cancel order


    private void configureCancelOrderPanel(){
        // Create and configure the config panel
        // This will provide a view to take in the user credentials
        // Use a GridBag layout so we have a grid to work with, but there's some flexibility (button can span columns)
        cancelOrderView = new JPanel(new GridBagLayout());
        // Register this panel as a container in the system
        guiContainers.put("cancelOrderView", cancelOrderView);

        // Create text fields and associated labels to take in username and password
        // Username info
        cancelBuyerOrSellerLabel = new JLabel("Buyer or Seller: ");
        cancelBuyerOrSellerTextField = new JTextField(15);

        cancelTitleLabel = new JLabel("Game Title: ");
        cancelTitleTextField = new JTextField(15);


        cancelPriceLabel = new JLabel("Price: ");
        cancelPriceTextField = new JTextField(15);



        // Create a button to log in user
        cancelOrderViewButton = new JButton("Cancel Order");
        // Specify what the button should DO when clicked:
        cancelOrderViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelOrder();
            }
        });


        goBackToHomePage = new JButton("Go Back To Home Page");
        // Specify what the button should DO when clicked:
        goBackToHomePage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToHomePageCancelOrder();
            }
        });


        logOut = new JButton("LogOut");
        // Specify what the button should DO when clicked:
        logOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logOutSendVideoGameOrder();
            }
        });


        cancelOrderView.add(cancelBuyerOrSellerLabel, getGridBagConstraints(0, 0, 1));
        cancelOrderView.add(cancelBuyerOrSellerTextField, getGridBagConstraints(1, 0, 1));

        cancelOrderView.add(cancelTitleLabel, getGridBagConstraints(0, 1, 1));
        cancelOrderView.add(cancelTitleTextField, getGridBagConstraints(1, 1, 1));

        cancelOrderView.add(cancelPriceLabel, getGridBagConstraints(0, 2, 1));
        cancelOrderView.add(cancelPriceTextField, getGridBagConstraints(1, 2, 1));


        // Add button on third row (y = 2) spanning two columns (width = 2)
        cancelOrderView.add(cancelOrderViewButton, getGridBagConstraints(0, 3, 2));

        // Add button on third row (y = 2) spanning two columns (width = 2)
        cancelOrderView.add(goBackToHomePage, getGridBagConstraints(0, 4, 2));
        cancelOrderView.add(logOut, getGridBagConstraints(0, 5, 2));
    }


    private void showCancelOrderView(){

        // Add config panel to the main window and make it visible
        // mainFrame.remove(0);

        mainFrame.add(cancelOrderView);
        mainFrame.setVisible(true);
    }


    public void start() throws IOException {
        network = new TCPNetworkLayer(AuthUtils.SERVER_HOST, AuthUtils.SERVER_PORT);
        network.connect();

        clientThread = new ClientThread(network, this);
        thread = new Thread(clientThread);
        thread.start();
        // Add the initial panel to the main window and display the interface
        showInitialView();
    }

    /*
     * All methods below this point provide application logic
     */
    private void loginUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        JsonObject payload = new JsonObject();
        payload.addProperty("username", username);
        payload.addProperty("password", password);

        // Create the overall request object
        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.LOGIN);
        requestJson.add("payload", payload);

        String request = gson.toJson(requestJson);
        network.send(request);



    }


    private void registerUser(){

        mainFrame.remove(initialView);
        showRegisterView();
    }


    private void sendOrderPage(){

        mainFrame.remove(homePageView);
        showSendVideoGameOrderView();
    }


    private void sendToCancelOrderPage(){

        mainFrame.remove(homePageView);
        showCancelOrderView();
    }


    /////////////////////////////////////////////////////////////////////////////// LOGOUT OF GUI
    private void logOutUser(){

        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.END);

        String request = gson.toJson(requestJson);
        network.send(request);
    }


    private void logOutSendVideoGameOrder(){

        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.END);

        String request = gson.toJson(requestJson);
        network.send(request);
    }



    ////////////////////////////////////////////////////////////////////////////////////// GO BACK TO PREVIOUS PAGES
    private void goBackToLogin(){

        mainFrame.remove(registerView);
        showInitialView();
    }


    private void goBackToHomePageSendVideoGameOrder(){

        mainFrame.remove(sendVideoGameOrderView);
        showHomePageView();
    }

    private void goBackToHomePageCancelOrder(){

        mainFrame.remove(cancelOrderView);
        showHomePageView();
    }


    private void goBackToHomePageOrderList(){

        f.dispose();
        showHomePageView();
    }



    //////////////////////////////////////////// METHODS TO COMMUNICATE WITH SERVER


    private void setRegisterButton(){
        String username = usernameTextField1.getText();
        String password = passwordTextField1.getText();
        String confirmPassword = confirmPasswordTextField1.getText();


        JsonObject payload = new JsonObject();
        payload.addProperty("username", username);
        payload.addProperty("password", password);
        payload.addProperty("confirmPassword", confirmPassword);


        // Create the overall request object
        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.REGISTER);
        requestJson.add("payload", payload);

        String request = gson.toJson(requestJson);
        network.send(request);

    }




    // send video game order

    public static String [] grow(String [] data, int numExtraSlots){
        String [] larger = new String[data.length + numExtraSlots];
        for(int i = 0; i < data.length; i++){
            larger[i] = data[i];
        }
        return larger;
    }
    private void sendVideoGameOrder(){

        String buyerOrSeller = buyerOrSellerTextField.getText();
        String title = titleTextField.getText();
        String price = priceTextField.getText();


        JsonObject payload = new JsonObject();
        payload.addProperty("buyerOrSeller", buyerOrSeller);
        payload.addProperty("title", title);
        payload.addProperty("price", price);

        // Create the overall request object
        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.ORDER);
        requestJson.add("payload", payload);

        String request = gson.toJson(requestJson);
        network.send(request);
    }




    private void cancelOrder(){

        String buyerOrSeller = cancelBuyerOrSellerTextField.getText();
        String title = cancelTitleTextField.getText();
        String price = cancelPriceTextField.getText();


        JsonObject payload = new JsonObject();
        payload.addProperty("buyerOrSeller", buyerOrSeller);
        payload.addProperty("title", title);
        payload.addProperty("price", price);

        // Create the overall request object
        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.CANCEL);
        requestJson.add("payload", payload);

        String request = gson.toJson(requestJson);
        network.send(request);



    }


    // view all orders

    private void viewAllOrders(){

        // Create the overall request object
        JsonObject requestJson = new JsonObject();
        // Add the request type/action and payload
        requestJson.addProperty("action", AuthUtils.VIEW);

        String request = gson.toJson(requestJson);
        network.send(request);


    }


    /// handle messages


    public void responseMessagesFromServer(String status, JsonObject jsonResponse){

        // If the response matches the expected success message, treat user as authenticated
        if (status.equals(AuthUtils.LOGIN_SUCCESSFUL)) {

            String result = jsonResponse.get("message").getAsString();

            JOptionPane.showMessageDialog(initialView, result, "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            mainFrame.remove(initialView);
            showHomePageView();

            usernameField.setText("");
            passwordField.setText("");

            return;
        } else if (status.equalsIgnoreCase(AuthUtils.REGISTER_SUCCESSFUL)) {

            String result = jsonResponse.get("message").getAsString();


            JOptionPane.showMessageDialog(initialView, result, "Register Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            mainFrame.remove(registerView);
            showHomePageView();

            usernameTextField1.setText("");
            passwordTextField1.setText("");
            confirmPasswordTextField1.setText("");

            return;

        } else if (status.equals(AuthUtils.PRICE_NOT_VALID) || status.equals(AuthUtils.INVALID_DATE_TIME) || status.equals(AuthUtils.TITLE_EMPTY) || status.equals(AuthUtils.INVALID) || status.equals(AuthUtils.BUYER_SELLER_NOT_VALID) || status.equals(AuthUtils.BUYER_SELLER_EMPTY) || status.equals(AuthUtils.NOT_LOGGED_IN) || status.equals(AuthUtils.NON_NUMERIC_ID)) {


            String result1 = jsonResponse.get("message").getAsString();

            JOptionPane.showMessageDialog(initialView, result1, "Error",
                    JOptionPane.ERROR_MESSAGE);

          //  mainFrame.remove(sendVideoGameOrderView);
           // showSendVideoGameOrderView();

            return;

        } else if (status.equalsIgnoreCase(AuthUtils.MATCH)){

            String result = jsonResponse.get("matchresult").getAsString();

            //create a new frame
            f = new JFrame("frame");
            //create a panel
            JPanel p =new JPanel();

            matchedLabel = new JLabel("Matched: ");
            p.add(matchedLabel, getGridBagConstraints(0, 0, 1));

            String [] orderArray = new String[]{result};
            b = new JList(orderArray);
            b.setSelectedIndex(0);
            p.add(b);
            f.add(p);
            f.setSize(500,400);

            goBackToHomePage = new JButton("Go Back To Home Page");
            // Specify what the button should DO when clicked:
            goBackToHomePage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    goBackToHomePageSendVideoGameOrder();
                }
            });
            p.add(goBackToHomePage, getGridBagConstraints(0, 2, 2));
            f.show();

            buyerOrSellerTextField.setText("");
            titleTextField.setText("");
            priceTextField.setText("");
        }else if (status.equalsIgnoreCase(AuthUtils.ORDERS_RETRIEVED_SUCCESSFULLY)){

            String result = jsonResponse.get("orders").getAsString();

            String[] orders = result.split("##");

            //create a new frame
            f = new JFrame("frame");

            //create a panel
            JPanel p =new JPanel();

            emailListLabel = new JLabel("List of all Video Game orders");
            p.add(emailListLabel, getGridBagConstraints(0, 0, 1));

            String [] orderArray = grow(orders, orders.length);
            b = new JList(orderArray);
            b.setSelectedIndex(0);
            p.add(b);
            f.add(p);
            f.setSize(500,400);

            goBackToHomePage = new JButton("Go Back To Home Page");
            // Specify what the button should DO when clicked:
            goBackToHomePage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    goBackToHomePageSendVideoGameOrder();
                }
            });
            p.add(goBackToHomePage, getGridBagConstraints(0, 2, 2));
            f.show();
        } else if (status.equalsIgnoreCase(AuthUtils.VIEW)){

            String result = jsonResponse.get("orders").getAsString();

            String[] emails = result.split("##");

            //create a new frame
            f = new JFrame("frame");

            //create a panel
            JPanel p =new JPanel();

            emailListLabel = new JLabel("List of all Orders");
            p.add(emailListLabel, getGridBagConstraints(0, 0, 1));

            String [] emailArray = grow(emails, emails.length);
            b = new JList(emailArray);
            b.setSelectedIndex(0);
            p.add(b);
            f.add(p);
            f.setSize(500,400);

            goBackToHomePage = new JButton("Go Back To Home Page");
            // Specify what the button should DO when clicked:
            goBackToHomePage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    goBackToHomePageOrderList();
                }
            });
            p.add(goBackToHomePage, getGridBagConstraints(0, 2, 2));
            f.show();

        } else if (status.equals(AuthUtils.NOT_FOUND)){

            String result1 = jsonResponse.get("message").getAsString();

            JOptionPane.showMessageDialog(initialView, result1, "Error",
                    JOptionPane.ERROR_MESSAGE);

            mainFrame.remove(cancelOrderView);
            showCancelOrderView();
        }
        else if (status.equals(AuthUtils.CANCELLED)){
            String result = jsonResponse.get("message").getAsString();

            JOptionPane.showMessageDialog(initialView, result, "Get content of retrieved emails",
                    JOptionPane.INFORMATION_MESSAGE);
            mainFrame.remove(cancelOrderView);
            showCancelOrderView();

            cancelBuyerOrSellerTextField.setText("");
            cancelTitleTextField.setText("");
            cancelPriceTextField.setText("");


        } else if (status.equals(AuthUtils.END)){

            String result1 = jsonResponse.get("message").getAsString();

            JOptionPane.showMessageDialog(initialView, result1, "LogOut Email System",
                    JOptionPane.INFORMATION_MESSAGE);

            mainFrame.remove(sendVideoGameOrderView);
            mainFrame.remove(homePageView);
            mainFrame.remove(cancelOrderView);

            showInitialView();

        }else if (status.equals(AuthUtils.EXIT)){

            String result1 = jsonResponse.get("message").getAsString();

            JOptionPane.showMessageDialog(initialView, result1, "Exiting System",
                    JOptionPane.INFORMATION_MESSAGE);
        }else{
            String result = jsonResponse.get("message").getAsString();

            JOptionPane.showMessageDialog(initialView, result, "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }



/// TECHNICALS


    private void setStandardFonts(){
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("OptionPane.messageFont", font);
        UIManager.put("OptionPane.buttonFont", font);
    }

    private void updateContainers() {
        for (Container c : guiContainers.values()) {
            for (Component component : c.getComponents()) {
                // Set the font in the component
                component.setFont(font);
            }
            // Revalidate and repaint the container
            c.revalidate();
            c.repaint();
        }
    }
    // GUI runner
    public static void main(String[] args) {
        // Create an instance of the GUI
        TCPGUIClient TCPGUIClient = new TCPGUIClient();
        // Start the GUI - this will trigger the application to be made visible
        try{
            TCPGUIClient.start();
        }catch(UnknownHostException e){
            System.out.println("Hostname could not be found. Please contact system administrator");
        }catch(SocketException e){
            System.out.println("Socket exception occurred. Please try again later.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
