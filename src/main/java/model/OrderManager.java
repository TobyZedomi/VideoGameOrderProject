package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderManager implements IOrderManager {

    private Map<String, ArrayList<Order>> buyOrders = new ConcurrentHashMap<>();
    private Map<String, ArrayList<Order>> sellOrders = new ConcurrentHashMap<>();
    public OrderManager() {

        bootstrapBuyOrderList();

        bootstrapSellOrderList();
    }

    // videoGameOrder

    public MatchResult videoGameOrder(String username, String buyerOrSeller, String title, double price, LocalDateTime dateOfOrder) {

        Order order = new Order(username, buyerOrSeller, title, price, dateOfOrder);

        MatchResult matchResult = null;

        if (buyerOrSeller.equalsIgnoreCase("B")) {
            matchResult = buyOrder(order);

        } else if (buyerOrSeller.equalsIgnoreCase("S")) {
            matchResult = orderToBeSold(order);
        }

        return matchResult;
    }


    private MatchResult buyOrder(Order order) {

        ArrayList<Order> sellersOfVideoGame = sellOrders.get(order.getTitle());

        Order bestSeller = null;
        int index = -1;

        for (int i = 0; i < sellersOfVideoGame.size(); i++) {

            Order sellerOfVideoGame = sellersOfVideoGame.get(i);

            if (order.getPrice() >= sellerOfVideoGame.getPrice()) {

                if (bestSeller == null) {
                bestSeller = sellerOfVideoGame;
                index = i;
            } else if (sellerOfVideoGame.getPrice() < bestSeller.getPrice() ) {

                    bestSeller = sellerOfVideoGame;
                    index = i;
                } else if (sellerOfVideoGame.getPrice() == bestSeller.getPrice() && sellerOfVideoGame.getDateOfOrder().isBefore(bestSeller.getDateOfOrder())) {

                    bestSeller = sellerOfVideoGame;
                    index = i;

                }
            }
        }

        if (bestSeller == null || index == -1){
            addBuyerOfGame(order);
            return null;
        }

        sellersOfVideoGame.remove(index);

        if (sellersOfVideoGame.isEmpty()) {
            sellOrders.remove(order.getTitle());
        }

        MatchResult matchResult = new MatchResult("B", bestSeller.getTitle(), bestSeller.getPrice(), bestSeller.getUsername());

        return matchResult;
    }


    private void addBuyerOfGame(Order order){
        if (buyOrders.containsKey(order.getTitle())) {

            ArrayList<Order> buyers = buyOrders.get(order.getTitle());
            buyers.add(order);
        } else {
            ArrayList<Order> buyers = new ArrayList<>();
            buyers.add(order);
            buyOrders.put(order.getTitle(), buyers);
        }
    }


    // check if seller for the game you want even exist, if not return true and put un buyOrders map

    public boolean checkIfSellerOfGameExist(Order order) {

        boolean doesntExist = false;

        ArrayList<Order> sellersOfGames = sellOrders.get(order.getTitle());

        if (sellersOfGames == null || sellersOfGames.isEmpty()) {
            addBuyerOfGame(order);
            doesntExist = true;
        }

        return doesntExist;
    }


    private MatchResult orderToBeSold(Order orderFromSeller) {

        ArrayList<Order> buyersOfGames = buyOrders.get(orderFromSeller.getTitle());

        Order bestBuyer = null;
        int index = -1;

        for (int i = 0; i < buyersOfGames.size(); i++) {

            Order buyerOfGame = buyersOfGames.get(i);

            if (buyerOfGame.getPrice() >= orderFromSeller.getPrice()) {
                if (bestBuyer == null) {
                    bestBuyer = buyerOfGame;
                    index = i;
                } else if ( buyerOfGame.getPrice() > bestBuyer.getPrice()) {

                    bestBuyer = buyerOfGame;

                    index = i;
                } else if (buyerOfGame.getPrice() == bestBuyer.getPrice() && buyerOfGame.getDateOfOrder().isBefore(bestBuyer.getDateOfOrder())) {

                    bestBuyer = buyerOfGame;
                    index = i;
                }
            }
        }

        if (bestBuyer == null || index == -1){
            addSellerOfGame(orderFromSeller);
            return null;
        }

        buyersOfGames.remove(index);

        if (buyersOfGames.isEmpty()) {
            buyOrders.remove(orderFromSeller.getTitle());
        }


        MatchResult matchResult = new MatchResult("S", orderFromSeller.getTitle(), bestBuyer.getPrice(), bestBuyer.getUsername());

        return matchResult;
    }


    private void addSellerOfGame(Order order){
            if (sellOrders.containsKey(order.getTitle())) {

                ArrayList<Order> sellers = sellOrders.get(order.getTitle());
                sellers.add(order);
            } else {
                ArrayList<Order> sellers = new ArrayList<>();
                sellers.add(order);
                sellOrders.put(order.getTitle(), sellers);
            }
    }


    public boolean checkIfBuyerOfOrderExist(Order order) {

        boolean doesntExist = false;

        ArrayList<Order> buyersOfGames = buyOrders.get(order.getTitle());

        if (buyersOfGames == null || buyersOfGames.isEmpty()) {

            addSellerOfGame(order);
            doesntExist = true;
        }

        return doesntExist;
    }


    // cancel order

    public boolean cancelOrder(String username, String buyerOrSeller, String title, double price) {

        if (buyerOrSeller.equalsIgnoreCase("B")) {
            return cancelVideoGameBuyerOrder(username, buyerOrSeller, title, price);
        } else if (buyerOrSeller.equalsIgnoreCase("S")) {
            return cancelVideoGameSellerOrder(username, buyerOrSeller, title, price);
        }
        return false;

    }


    private boolean cancelVideoGameBuyerOrder(String username, String buyerOrSeller, String title, double price) {

        boolean cancel = false;

        ArrayList<Order> userBuyOrders = buyOrders.get(title);


        if (userBuyOrders == null) {

            return false;
        }

        for (int i = 0; i < userBuyOrders.size(); i++) {

            Order orderFromBuyer = userBuyOrders.get(i);

            if (orderFromBuyer.getTitle().equals(title) && orderFromBuyer.getUsername().equals(username) && orderFromBuyer.getPrice() == price && orderFromBuyer.getBuyerOrSeller().equals(buyerOrSeller)) {

                userBuyOrders.remove(i);

                if (userBuyOrders.isEmpty()) {
                    buyOrders.remove(title);
                }

                cancel = true;
            } else {

                return false;
            }
        }

        return cancel;
    }


    private boolean cancelVideoGameSellerOrder(String username, String buyerOrSeller, String title, double price) {

        boolean cancel = false;

        ArrayList<Order> userSellerOrders = sellOrders.get(title);


        if (userSellerOrders == null) {

            return false;
        }

        for (int i = 0; i < userSellerOrders.size(); i++) {

            Order orderFromSeller = userSellerOrders.get(i);

            if (orderFromSeller.getTitle().equals(title) && orderFromSeller.getUsername().equals(username) && orderFromSeller.getPrice() == price && orderFromSeller.getBuyerOrSeller().equals(buyerOrSeller)) {

                userSellerOrders.remove(i);

                if (userSellerOrders.isEmpty()) {
                    sellOrders.remove(title);
                }

                cancel = true;
            } else {
                return false;
            }
        }

        return cancel;
    }


    // display all orders

    public ArrayList<Order> getAllOrders() {

        ArrayList<Order> orders = new ArrayList<>();

        for (ArrayList<Order> orderArrayList : buyOrders.values()) {
            for (int i = 0; i < orderArrayList.size();i++){
                orders.add(orderArrayList.get(i));
            }
        }

        for (ArrayList<Order> orderArrayList : sellOrders.values()) {
            for (int i = 0; i < orderArrayList.size();i++){
                orders.add(orderArrayList.get(i));
            }
        }

        return orders;
    }


    // check price

    public boolean checkIfPriceIsValid(double price) {

        boolean good = false;

        if (price > 0) {

            good = true;
        }

        return good;
    }

    // check if buyer or seller is valid

    public boolean checkIfBuyerOrSellerIsValid(String buyerOrSeller) {

        boolean match = false;

        if (buyerOrSeller.equalsIgnoreCase("S") || buyerOrSeller.equalsIgnoreCase("B")) {

            match = true;
        }

        return match;
    }


    private void bootstrapSellOrderList() {

        ArrayList<Order> orders = new ArrayList();
        orders.add(new Order("toby", "S", "GTA 5", 50, LocalDateTime.of(2026, 02, 9, 5, 34)));
        orders.add(new Order("sean", "S", "GTA 5", 100, LocalDateTime.of(2026, 02, 9, 5, 34)));

        ArrayList<Order> orders1 = new ArrayList();
        orders1.add(new Order("sean", "S", "NBA 2K", 10, LocalDateTime.of(2026, 02, 9, 4, 34)));
        orders1.add(new Order("adam", "S", "NBA 2K", 10, LocalDateTime.of(2026, 02, 2, 14, 44)));


        ArrayList<Order> orders2 = new ArrayList();
        orders2.add(new Order("adam", "S", "Fortnite", 70, LocalDateTime.of(2026, 02, 9, 5, 34)));

        sellOrders.put("GTA 5", orders);
        sellOrders.put("NBA 2K", orders1);
        sellOrders.put("Fortnite", orders2);
    }


    private void bootstrapBuyOrderList() {

        ArrayList<Order> orders = new ArrayList();
        orders.add(new Order("toby", "B", "Roblox", 50, LocalDateTime.of(2026, 02, 9, 5, 34)));
        orders.add(new Order("sean", "B", "Roblox", 150, LocalDateTime.of(2026, 02, 9, 5, 34)));

        ArrayList<Order> orders1 = new ArrayList();
        orders1.add(new Order("sean", "B", "Spiderman", 50, LocalDateTime.of(2026, 02, 9, 3, 34)));
        orders1.add(new Order("adam", "B", "Spiderman", 50, LocalDateTime.of(2026, 02, 2, 13, 12)));

        ArrayList<Order> orders2 = new ArrayList();
        orders2.add(new Order("adam", "B", "Minecraft", 70, LocalDateTime.of(2026, 02, 9, 5, 34)));

        buyOrders.put("Roblox", orders);
        buyOrders.put("Spiderman", orders1);
        buyOrders.put("Minecraft", orders2);
    }


}
