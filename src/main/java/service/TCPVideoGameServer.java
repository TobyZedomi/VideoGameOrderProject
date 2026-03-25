package service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import model.*;
import network.TCPNetworkLayer;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.StringJoiner;

@Slf4j
public class TCPVideoGameServer implements Runnable {

    private final Socket clientDataSocket;

    public static ArrayList<TCPVideoGameServer> clients = new ArrayList<>();
    private final TCPNetworkLayer networkLayer;
    private final UserManager userManager;
    private final OrderManager orderManager;
    private final Gson gson = new Gson();

    private String username;
    private boolean loginStatus;

    public TCPVideoGameServer(Socket clientDataSocket, UserManager userManager, OrderManager orderManager) throws IOException {
        this.clientDataSocket = clientDataSocket;
        this.networkLayer = new TCPNetworkLayer(clientDataSocket);
        this.userManager = userManager;
        this.orderManager = orderManager;
        this.username = null;
        this.loginStatus = false;
    }


    @Override
    public void run() {
        boolean validClientSession = true;

        try {
            while (validClientSession) {
                String request = networkLayer.receive();
                System.out.println("Request: " + request);

                JsonObject jsonResponse = null;

                JsonObject jsonRequest = gson.fromJson(request, JsonObject.class);


                if (jsonRequest.has("action")) {
                    String action = jsonRequest.get("action").getAsString();

                    switch (action) {

                        case UserUtilities.REGISTER:
                            jsonResponse = registerUser(jsonRequest, userManager, orderManager);
                            if (jsonResponse.equals(createStatusResponse(UserUtilities.REGISTER_SUCCESSFUL, "Register Successful"))) {
                                loginStatus = true;
                                addClient();
                            }
                            break;

                        case UserUtilities.LOGIN:
                            jsonResponse = loginUser(jsonRequest, userManager, username);
                            if (jsonResponse.equals(createStatusResponse(UserUtilities.LOGIN_SUCCESSFUL, "Login Successful"))) {
                                loginStatus = true;
                                addClient();
                            }
                            break;
                        case UserUtilities.ORDER:
                            jsonResponse = sendOrderForGame(loginStatus, jsonRequest, orderManager);
                            break;
                        case UserUtilities.CANCEL:
                            jsonResponse = cancelOrder(loginStatus, jsonRequest, orderManager);
                            break;
                        case UserUtilities.VIEW:
                            jsonResponse = viewOrder(loginStatus, orderManager);
                            break;
                        case UserUtilities.END:
                            jsonResponse = createStatusResponse(UserUtilities.END, "ENDED");
                            loginStatus = false;
                            removeClient();
                            break;
                        case UserUtilities.EXIT:
                            jsonResponse = createStatusResponse(UserUtilities.EXIT, "EXIT");
                            validClientSession = false;
                            removeClient();
                            break;
                        default:
                            jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                            break;
                    }
                }

                if (jsonResponse == null) {
                    jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                }

                String response = gson.toJson(jsonResponse);
                // Send response
                networkLayer.send(response);

            }
            networkLayer.disconnect();
            removeClient();
        } catch (IOException e) {
            System.out.println("ERROR");
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    private JsonObject registerUser(JsonObject jsonRequest, IUserManager userManager, IOrderManager orderManager) throws InvalidKeySpecException, NoSuchAlgorithmException {
        //String jsonResponse;
        JsonObject jsonResponse = null;
        JsonObject payload = (JsonObject) jsonRequest.get("payload");
        if (payload.size() == 3) {

            String usernameReg = payload.get("username").getAsString();
            String password = payload.get("password").getAsString();
            String confirmPassword = payload.get("confirmPassword").getAsString();

            this.username = usernameReg;

            boolean checkIfUserExist = userManager.checkIfUserExist(usernameReg);

            boolean checkPasswordsMatch = userManager.checkIfPasswordsAreTheSame(password, confirmPassword);

            boolean checkPasswordFormat = userManager.checkIfPasswordsMatchRegex(password, confirmPassword);

            boolean checkEmailFormat = userManager.checkIfEmailMatchRegex(usernameReg);

            if (usernameReg != null) {
                if (!usernameReg.isEmpty()) {
                    if (!password.isEmpty()) {
                        if (password != null) {
                            if (!confirmPassword.isEmpty()) {
                                if (confirmPassword != null) {
                                    if (checkIfUserExist == true) {
                                        if (checkPasswordsMatch == true) {
                                            if (checkPasswordFormat == true) {
                                                if (checkEmailFormat == true) {

                                                    userManager.registerUser(usernameReg, password);
                                                    jsonResponse = createStatusResponse(UserUtilities.REGISTER_SUCCESSFUL, "Register Successful");
                                                    log.info("User {} successfully registered with us ", usernameReg);
                                                } else {
                                                    jsonResponse = createStatusResponse(UserUtilities.INVALID_EMAIL_FORMAT, "Username must be in email format with @ and e.g .com at the end");
                                                    log.info("User {} failed registration", usernameReg);
                                                }
                                            } else {
                                                jsonResponse = createStatusResponse(UserUtilities.INVALID_PASSWORD_FORMAT, "Password format must be 8 or more characters long, have at least 1 capital letter, 1 upper case and 1 special character");
                                                log.info("User {} failed registration", usernameReg);
                                            }

                                        } else {
                                            jsonResponse = createStatusResponse(UserUtilities.PASSWORDS_DONT_MATCH, "Passwords dont match");
                                            log.info("User {} failed registration", usernameReg);
                                        }
                                    } else {
                                        jsonResponse = createStatusResponse(UserUtilities.USER_ALREADY_EXIST, "User already exist");
                                        log.info("User {} failed registration", usernameReg);
                                    }
                                } else {
                                    jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                                }
                            } else {
                                jsonResponse = createStatusResponse(UserUtilities.INVALID, "Cant leave confirm password empty");
                            }
                        } else {
                            jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                        }
                    } else {
                        jsonResponse = createStatusResponse(UserUtilities.INVALID, "Cant leave password empty");
                    }
                } else {
                    jsonResponse = createStatusResponse(UserUtilities.INVALID, "Cant leave username empty");
                }
            } else {
                jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
            }
        } else {
            jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
            log.info("User {} failed registration", username);
        }
        return jsonResponse;
    }

    private JsonObject loginUser(JsonObject jsonRequest, IUserManager userManager, String email) {
        JsonObject jsonResponse = null;
        JsonObject payload = (JsonObject) jsonRequest.get("payload");
        if (payload.size() == 2) {

            String usernameLoggedIn = payload.get("username").getAsString();
            email = usernameLoggedIn;
            username = usernameLoggedIn;
            String password = payload.get("password").getAsString();

            boolean loginUser = userManager.loginUser(email, password);

            if (!usernameLoggedIn.isEmpty()) {
                if (usernameLoggedIn != null) {
                    if (!password.isEmpty()) {
                        if (password != null) {
                            if (loginUser == true) {
                                jsonResponse = createStatusResponse(UserUtilities.LOGIN_SUCCESSFUL, "Login Successful");
                                log.info("User {} successfully logged in ", usernameLoggedIn);
                            } else {
                                jsonResponse = createStatusResponse(UserUtilities.LOGIN_FAILED, "Login Failed");
                                log.info("User {} failed logged in", username);
                            }
                        } else {
                            jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                        }
                    } else {
                        jsonResponse = createStatusResponse(UserUtilities.INVALID, "Cant leave password empty");
                    }
                } else {
                    jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                }
            } else {
                jsonResponse = createStatusResponse(UserUtilities.INVALID, "Cant leave username empty");
            }
        } else {
            jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
            log.info("User {} failed logged in", username);

        }
        return jsonResponse;
    }


    // send game for sale

    private JsonObject sendOrderForGame(boolean loginStatus, JsonObject jsonRequest, IOrderManager orderManager) {

        JsonObject jsonResponse = null;

        if (loginStatus) {

            JsonObject payload = (JsonObject) jsonRequest.get("payload");
            if (payload.size() == 3) {
                try {

                    String sender = username;
                    String buyerOrSeller = payload.get("buyerOrSeller").getAsString();
                    String title = payload.get("title").getAsString();
                    double price = Double.parseDouble(payload.get("price").getAsString());
                    LocalDateTime dateOfOrder = LocalDateTime.now();

                    boolean checkIfPriceIsValid = orderManager.checkIfPriceIsValid(price);
                    boolean checkIfBuyerOrSellerIsValid = orderManager.checkIfBuyerOrSellerIsValid(buyerOrSeller);

                    if (buyerOrSeller != null) {
                        if (!buyerOrSeller.isEmpty()) {
                            if (checkIfBuyerOrSellerIsValid == true) {
                                if (title != null) {
                                    if (!title.isEmpty()) {
                                        if (!dateOfOrder.isAfter(LocalDateTime.now())) {
                                            if (checkIfPriceIsValid == true) {
                                                Order order = new Order(username, buyerOrSeller, title, price, dateOfOrder);

                                                if (buyerOrSeller.equalsIgnoreCase("B")) {

                                                    boolean checkIfSellerExist = orderManager.checkIfSellerOfGameExist(order);

                                                    if (!checkIfSellerExist) {

                                                        MatchResult matchResult = orderManager.videoGameOrder(username, buyerOrSeller, title, price, dateOfOrder);

                                                        if (matchResult != null) {
                                                            jsonResponse = createStatusResponse3(UserUtilities.MATCH, serializeMatchResult(matchResult));

                                                            MatchResult m1 = new MatchResult("S", matchResult.getTitle(), matchResult.getPrice(), username);
                                                            JsonObject response = createStatusResponse3(UserUtilities.MATCH, serializeMatchResult(m1));
                                                            sendMatch(matchResult.getCounterParty(), response);
                                                        } else {
                                                            ArrayList<Order> orders = orderManager.getAllOrders();
                                                            jsonResponse = serializeOrders(orders);
                                                            log.info("User {} got all orders ", username);
                                                        }
                                                    } else {
                                                        ArrayList<Order> orders = orderManager.getAllOrders();
                                                        jsonResponse = serializeOrders(orders);
                                                        log.info("User {} got all orders ", username);
                                                    }
                                                } else if (buyerOrSeller.equalsIgnoreCase("S")) {

                                                    boolean checkIfBuyerExist = orderManager.checkIfBuyerOfOrderExist(order);
                                                    if (!checkIfBuyerExist) {

                                                        MatchResult matchResult = orderManager.videoGameOrder(username, buyerOrSeller, title, price, dateOfOrder);

                                                        if (matchResult != null) {
                                                            jsonResponse = createStatusResponse3(UserUtilities.MATCH, serializeMatchResult(matchResult));

                                                            MatchResult m1 = new MatchResult("B", matchResult.getTitle(), matchResult.getPrice(), username);
                                                            JsonObject response = createStatusResponse3(UserUtilities.MATCH, serializeMatchResult(m1));
                                                            sendMatch(matchResult.getCounterParty(), response);
                                                        } else {

                                                            ArrayList<Order> orders = orderManager.getAllOrders();
                                                            jsonResponse = serializeOrders(orders);
                                                            log.info("User {} got all orders ", username);

                                                        }

                                                    } else {
                                                        ArrayList<Order> orders = orderManager.getAllOrders();
                                                        jsonResponse = serializeOrders(orders);
                                                        log.info("User {} got all orders ", username);
                                                    }
                                                }

                                            } else {
                                                jsonResponse = createStatusResponse(UserUtilities.PRICE_NOT_VALID, "Price entered not valid");
                                                log.info("User {} tried to enter a price but price {} is 0 or below", username, price);
                                            }

                                        } else {
                                            jsonResponse = createStatusResponse(UserUtilities.INVALID_DATE_TIME, "Date is incorrect");
                                        }
                                    } else {
                                        jsonResponse = createStatusResponse(UserUtilities.TITLE_EMPTY, "Must fill in title for game");
                                    }
                                } else {
                                    jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                                }
                            } else {
                                jsonResponse = createStatusResponse(UserUtilities.BUYER_SELLER_NOT_VALID, "Must enter B or S to be a buyer or seller");
                            }
                        } else {
                            jsonResponse = createStatusResponse(UserUtilities.BUYER_SELLER_EMPTY, "Must fill in buyer or seller");
                        }
                    } else {
                        jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                    }

                } catch (NumberFormatException ex) {
                    jsonResponse = createStatusResponse(UserUtilities.NON_NUMERIC_ID, "Price must be a number");
                    log.info("User {} entered a non numeric id", username);
                }
            } else {
                jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
            }
        } else {

            jsonResponse = createStatusResponse(UserUtilities.NOT_LOGGED_IN, "Not logged in");
            log.info("{} is not logged in", username);
        }
        return jsonResponse;
    }


    private JsonObject cancelOrder(boolean loginStatus, JsonObject jsonRequest, IOrderManager orderManager) {

        JsonObject jsonResponse = null;

        if (loginStatus) {

            JsonObject payload = (JsonObject) jsonRequest.get("payload");
            if (payload.size() == 3) {
                try {

                    String buyerOrSeller = payload.get("buyerOrSeller").getAsString();
                    String title = payload.get("title").getAsString();
                    double price = Double.parseDouble(payload.get("price").getAsString());

                    boolean checkIfPriceIsValid = orderManager.checkIfPriceIsValid(price);
                    boolean checkIfBuyerOrSellerIsValid = orderManager.checkIfBuyerOrSellerIsValid(buyerOrSeller);

                    if (buyerOrSeller != null) {
                        if (!buyerOrSeller.isEmpty()) {
                            if (checkIfBuyerOrSellerIsValid == true) {
                                if (title != null) {
                                    if (!title.isEmpty()) {
                                        if (checkIfPriceIsValid == true) {
                                            // cancel order
                                            boolean cancelOrder = orderManager.cancelOrder(username, buyerOrSeller, title, price);

                                            if (cancelOrder == true) {
                                                jsonResponse = createStatusResponse(UserUtilities.CANCELLED, "Cancelled Order");
                                            } else {
                                                jsonResponse = createStatusResponse(UserUtilities.NOT_FOUND, "Not Found");
                                            }
                                        } else {
                                            jsonResponse = createStatusResponse(UserUtilities.PRICE_NOT_VALID, "Price entered not valid");
                                            log.info("User {} tried to enter a price but price {} is 0 or below", username, price);
                                        }

                                    } else {
                                        jsonResponse = createStatusResponse(UserUtilities.TITLE_EMPTY, "Must fill in title for game");
                                    }
                                } else {
                                    jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                                }
                            } else {
                                jsonResponse = createStatusResponse(UserUtilities.BUYER_SELLER_NOT_VALID, "Must enter B or S to be a buyer or seller");
                            }
                        } else {
                            jsonResponse = createStatusResponse(UserUtilities.BUYER_SELLER_EMPTY, "Must fill in buyer or seller");
                        }
                    } else {
                        jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                    }

                } catch (NumberFormatException ex) {
                    jsonResponse = createStatusResponse(UserUtilities.NON_NUMERIC_ID, "Price must be a number");
                    log.info("User {} entered a non numeric id", username);
                }
            } else {
                jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
            }
        } else {

            jsonResponse = createStatusResponse(UserUtilities.NOT_LOGGED_IN, "Not logged in");
            log.info("{} is not logged in", username);
        }
        return jsonResponse;
    }


    // view order

    private JsonObject viewOrder(boolean loginStatus, IOrderManager orderManager) {
        JsonObject jsonResponse;
        if (loginStatus) {
            ArrayList<Order> orders = orderManager.getAllOrders();

            if (!orders.isEmpty()) {
                if (orders != null) {

                    jsonResponse = serializeOrders(orders);
                    log.info("User {} got all orders ", username);


                } else {
                    jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                }
            } else {
                jsonResponse = createStatusResponse(UserUtilities.YOU_HAVE_NO_ORDERS, "You have no orders");
                log.info("User {} wanted to see orders but there is none ", username);
            }
        } else {
            jsonResponse = createStatusResponse(UserUtilities.NOT_LOGGED_IN, "Not logged in");
            log.info("{} is not logged in", username);

        }
        return jsonResponse;
    }


    // clients connected

    private synchronized void addClient() {
        if (!clients.contains(this)) {
            clients.add(this);
        }
    }

    private synchronized void removeClient() {
        clients.remove(this);
    }

    private synchronized void sendToResponseToClient(JsonObject response) {

        networkLayer.send(gson.toJson(response));
    }

    private void sendMatch(String username, JsonObject response) {

        synchronized (clients) {

            for (int i = 0; i < clients.size(); i++) {

                if (clients.get(i).username.equalsIgnoreCase(username)) {

                    clients.get(i).sendToResponseToClient(response);
                }
            }
        }
    }

    public String serializeMatchResult(MatchResult m) {
        if (m == null) {
            throw new IllegalArgumentException("Cannot serialise null Match Result");
        }
        return String.format("BuyerOrSeller: %s, Title: %s, Price: %s, CounterParty: %s", m.getBuyerOrSeller(), m.getTitle(), m.getPrice(), m.getCounterParty());
    }
    public JsonObject serializeOrders(ArrayList<Order> orders) {
        JsonObject jsonResponse = null;

        StringJoiner joiner = new StringJoiner(UserUtilities.EMAIL_DELIMITER2);

        for (Order order : orders) {
            joiner.add(serializeOrder(order));
            jsonResponse = createStatusResponse2(UserUtilities.ORDERS_RETRIEVED_SUCCESSFULLY, joiner.toString());
        }
        return jsonResponse;
    }

    public String serializeOrder(Order m) {
        if (m == null) {
            throw new IllegalArgumentException("Cannot serialise null Order");
        }
        return "BuyerOrSeller: " + m.getBuyerOrSeller() + UserUtilities.EMAIL_DELIMITER + "Title: " + m.getTitle() + UserUtilities.EMAIL_DELIMITER + "Price: " + m.getPrice() + UserUtilities.EMAIL_DELIMITER + "Date Of Order:" + m.getDateOfOrder().toLocalDate();
    }

    private static JsonObject createStatusResponse(String status, String message) {
        JsonObject invalidResponse = new JsonObject();
        invalidResponse.addProperty("status", status);
        invalidResponse.addProperty("message", message);
        return invalidResponse;
    }


    private JsonObject createStatusResponse2(String status, String orders) {
        JsonObject invalidResponse = new JsonObject();
        invalidResponse.addProperty("status", status);
        invalidResponse.addProperty("orders", orders);
        return invalidResponse;
    }


    private JsonObject createStatusResponse3(String status, String matchresult) {
        JsonObject invalidResponse = new JsonObject();
        invalidResponse.addProperty("status", status);
        invalidResponse.addProperty("matchresult", matchresult);
        return invalidResponse;
    }


}