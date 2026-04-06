import XMLParsing.Movie;
import java.util.HashMap;
import java.util.Map;

/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {
    private final Integer id;
    private final String email;
    private final String password;

    private Map<Movie, Integer> cart;
    private Double totalPrice;

    public User(Integer id, String email, String password) {
        this.email = email;
        this.password = password;
        this.id = id;

        this.cart = new HashMap<Movie, Integer>();
        this.totalPrice = 0.0;
    }

    public Integer getId() {
        return this.id;
    }
    public Double getTotalPrice() {
        return this.totalPrice;
    }
    public Map<Movie, Integer> getCart() {
        return this.cart;
    }

    public void setTotalPrice(Double price) {
        this.totalPrice = price;
    }


    public void addToCart(Movie movie) {
        Integer quantity = this.cart.get(movie);
        if (quantity == null) {
            this.cart.put(movie, 1);
            System.out.println("adding for first time");
        }
        else {
            this.cart.put(movie, quantity + 1);
            System.out.println("adding second time");
        }
    }

    public void decreaseCount(Movie movie) {
        Integer quantity = this.cart.get(movie);
        if (quantity != 0) {
            this.cart.put(movie, quantity - 1);
        }
    }

    public void removeFromCart(Movie movie) {
        this.cart.remove(movie);
    }

}