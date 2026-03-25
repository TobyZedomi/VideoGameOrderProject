package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface IOrderManager {

    MatchResult videoGameOrder(String username, String buyerOrSeller, String title, double price, LocalDateTime dateOfOrder);

    boolean checkIfSellerOfGameExist(Order order);

    boolean checkIfBuyerOfOrderExist(Order order);

    boolean cancelOrder (String username, String buyerOrSeller, String title, double price);

    boolean checkIfPriceIsValid(double price);

    boolean checkIfBuyerOrSellerIsValid(String buyerOrSeller);

    public ArrayList<Order> getAllOrders();

}
