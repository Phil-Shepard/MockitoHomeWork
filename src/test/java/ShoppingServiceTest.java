import customer.Customer;
import customer.CustomerDao;
import customer.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import shopping.Cart;
import shopping.ShoppingService;
import shopping.ShoppingServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ShoppingServiceTest {
    private final CustomerDao productDaoMock = Mockito.mock(CustomerDao.class);
    private final Cart cart = Mockito.mock(Cart.class);
    private final ShoppingServiceImpl shoppingService =
            new ShoppingServiceImpl(cart);
    @Test
    public void testAddCart() throws Exception {
        when(customerDaoMock.save(any(Customer.class)))
                .thenReturn(Boolean.TRUE);


    }

    @Test
    public void testAddCart() throws Exception {
        when(customerDaoMock.save(any(Customer.class)))
                .thenReturn(Boolean.TRUE);


    }
}
