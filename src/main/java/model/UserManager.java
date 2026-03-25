package model;


import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import network.TCPNetworkLayer;
import org.mindrot.jbcrypt.BCrypt;
import service.TCPVideoGameServer;

public class UserManager implements IUserManager {


    // private HashMap<String, User> users = new HashMap<>();

    private Map<String, User> users = new ConcurrentHashMap<>();


    public UserManager(){

        bootstrapUserList();
    }

    // check if user already exist

    /**
     * Checking is username exist in hashmap
     * @param username is the username being searched
     * @return true if exist and false if it doesnt exist
     */

    public boolean checkIfUserExist(String username){
        boolean match = false;
        if(!users.containsKey(username)) {
            match = true;
        }
        return match;
    }

    // register User

    /**
     * Register a user by adding there username and password to the hashmap
     * @param username is the username being added
     * @param password is the password being added
     * @return true if registered and false if not added
     */

    public boolean registerUser(String username, String password){

        User userToBeRegistered = new User(username,hashPassword(password));

        return register(userToBeRegistered);
    }

    /**
     * Register a user based on if their username key doesn't already exist in hashmap
     * @param u is the user being searched
     * @return true if added and false if not added
     */
    private boolean register(User u){
        boolean added = false;
        if(!users.containsKey(u.getUsername())) {
            added = true;
            users.put(u.getUsername(), u);
        }
        return added;
    }


    // check if passwords are the same

    /**
     * Check if passwords are the same
     * @param password is the password being searched
     * @param confirmPassword is the password being searched
     * @return true if they match and false if they dont match
     */
    public boolean checkIfPasswordsAreTheSame(String password, String confirmPassword){

        boolean match = false;

        if (password.equals(confirmPassword)){

            match = true;
        }

        return match;
    }


    // check if password match regex

    /**
     * Check if password matches regex format
     * @param password is the password being searched
     * @param confirmPassword is the confirm password being searched
     * @return true if match and false if no match
     */
    public boolean checkIfPasswordsMatchRegex(String password, String confirmPassword){

        boolean match = false;

        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";

        if (password.matches(pattern) && confirmPassword.matches(pattern)){

            match = true;
        }


        return match;
    }


    // check if email has correct regex

    /**
     * Check if its the right email format
     * @param email is th email being searched
     * @return truye if matches format and false if no match
     */

    public boolean checkIfEmailMatchRegex(String email){

        boolean match = false;

        String pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        if (email.matches(pattern)){

            match = true;
        }


        return match;
    }


    /// check if user is logged in

    /**
     * Login user based on if username and password match whats in the hashmap
     * @param username is the username being searched
     * @param password is the password being searched
     * @return true if there is a match and false if no match
     */
    public boolean loginUser(String username, String password){

        boolean match = false;

        User u = users.get(username);

        if (u == null){

            match = false;
        }

        if (u != null){
            if (checkPassword(password, u.getPassword())){

                match = true;
            }
        }

        return match;
    }

    // Method to fill the list of quotations with a set of initial quotes
    private void bootstrapUserList()
    {


        users.put("toby@gmail.com", new User("toby@gmail.com", "$2a$12$uyB4h6u16QCLOw.sjy4GmOCralfICrbT93DY8/aZP4F4KwBgstpxy"));
        users.put("sean@gmail.com", new User("sean@gmail.com", "$2a$12$uyB4h6u16QCLOw.sjy4GmOCralfICrbT93DY8/aZP4F4KwBgstpxy"));
        users.put("adam@gmail.com", new User("user2@gmail.com", "$2a$12$uyB4h6u16QCLOw.sjy4GmOCralfICrbT93DY8/aZP4F4KwBgstpxy"));

    }

    private static int workload = 12;

    /**
     * Hash the password based
     * @param password_plaintext is the password being hashed
     * @return hashed password
     */

    public static String hashPassword(String password_plaintext) {
        String salt = BCrypt.gensalt(workload);
        String hashed_password = BCrypt.hashpw(password_plaintext, salt);


        return(hashed_password);
    }

    /**
     * Check password matches hash password
     * @param password_plaintext is the password being searched
     * @param stored_hash is the hashed password being searched
     * @return true if tehy match and false if they don't match
     */

    private static boolean checkPassword(String password_plaintext, String stored_hash) {
        boolean password_verified = false;

        if(null == stored_hash || !stored_hash.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

        password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

        return(password_verified);
    }


}
