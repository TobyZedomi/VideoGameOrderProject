package service;

public class UserUtilities {

    public static final String HOSTNAME = "localhost";
    public static final int PORT = 11000;

    // REQUESTS
    public static final String LOGIN = "LOGIN";

    public static final String REGISTER = "REGISTER";

    public static final String EXIT = "EXIT";

    public static final String ORDER = "ORDER";

    public static final String CANCEL = "CANCEL";


    public static final String VIEW = "VIEW";


    public static final String LOGOUT = "LOGOUT";


    public static final String END = "END";


    // Responses

    public static final String LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL";
    public static final String LOGIN_FAILED = "LOGIN_FAILED";

    public static final String PASSWORDS_DONT_MATCH = "PASSWORDS_DONT_MATCH";

    public static final String INVALID_PASSWORD_FORMAT = "INVALID_PASSWORD_FORMAT";

    public static final String INVALID_EMAIL_FORMAT = "INVALID_EMAIL_FORMAT";


    public static final String REGISTER_SUCCESSFUL = "REGISTER_SUCCESSFUL";

    public static final String USER_ALREADY_EXIST = "USER_ALREADY_EXIST";


    public static final String INVALID_DATE_TIME = "INVALID_DATE_TIME";


    public static final String BUYER_SELLER_EMPTY = "BUYER_SELLER_EMPTY";

    public static final String NOT_FOUND = "NOT_FOUND";


    public static final String NOT_LOGGED_IN = "NOT_LOGGED_IN";


    public static final String ORDERS_RETRIEVED_SUCCESSFULLY = "ORDERS_RETRIEVED_SUCCESSFULLY";



    public static final String CANCELLED = "CANCELLED";


    public static final String BUYER_SELLER_NOT_VALID = "BUYER_SELLER_NOT_VALID";
    public static final String NON_NUMERIC_ID= "NON_NUMERIC_ID";
    public static final String TITLE_EMPTY = "TITLE_EMPTY";

    public static final String PRICE_NOT_VALID= "PRICE_NOT_VALID";

    public static final String MATCH= "MATCH";

    public static final String MATCHED =   "{\"status\":\"MATCH\",\"matchresult\":\"BuyerOrSeller: %s, Title: %s, Price: %s, CounterParty: %s\"}";


    public static final String YOU_HAVE_NO_ORDERS = "YOU_HAVE_NO_ORDERS";


    public static final String GOODBYE = "GOODBYE";


    // DELIMITERS
    public static final String DELIMITER = "%%";
    public static final String EMAIL_DELIMITER = ", ";

    public static final String EMAIL_DELIMITER2 = "##";



    // GENERAL MALFORMED RESPONSE:
    public static final String INVALID = "INVALID";

}
