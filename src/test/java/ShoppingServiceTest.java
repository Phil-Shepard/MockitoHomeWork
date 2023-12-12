import customer.Customer;
import customer.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import product.Product;
import product.ProductDao;
import shopping.BuyException;
import shopping.Cart;
import shopping.ShoppingServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Тестирование класса {@link ShoppingServiceImpl}
 */
public class ShoppingServiceTest {

    public final ProductDao productDao = Mockito.mock(ProductDao.class);

    private final ShoppingServiceImpl shoppingService = new ShoppingServiceImpl(productDao);


    /**
     * Тест покупки продуктов
     */
    @Test
    void testBuyProducts() throws BuyException {
        Product firstProduct = new Product();
        firstProduct.setName("eggs");
        firstProduct.addCount(10);

        Product secondProduct = new Product();
        secondProduct.setName("sugar");
        secondProduct.addCount(2);

        Customer customer = new Customer(3L, "3");
        Cart cart = new Cart(customer);
        cart.add(firstProduct, 1);
        cart.add(secondProduct, 1);

        boolean result = shoppingService.buy(cart);
        Mockito.verify(productDao, Mockito.times(1)).save(firstProduct);
        Mockito.verify(productDao, Mockito.times(1)).save(secondProduct);
        Assertions.assertTrue(result);
        Assertions.assertEquals(9, firstProduct.getCount());
        Assertions.assertEquals(1, secondProduct.getCount());
    }


    /**
     * Тест метода получения всех продуктов, находящихся в корзине
     */
    @Test
    public void testGetAllProducts() {
        Product product1 = new Product();
        product1.setName("apples");
        product1.addCount(10);

        Product product2 = new Product();
        product2.setName("pens");
        product2.addCount(2);

        List<Product> mockProducts = Arrays.asList(product1, product2);

        when(productDao.getAll()).thenReturn(mockProducts);

        List<Product> products = shoppingService.getAllProducts();

        Assertions.assertEquals(mockProducts, products);
    }

    /**
     * Тест метода получения продукта по имени
     */
    @Test
    public void testGetProductByName() {
        Product product1 = new Product();
        product1.setName("apples");
        product1.addCount(10);

        String productName = product1.getName();
        when(productDao.getByName(productName)).thenReturn(product1);

        Product product = shoppingService.getProductByName(productName);

        Assertions.assertEquals(product1, product);
    }

    /**
     * Тест получения корзины
     */
    @Test
    void testGetCart() {
        Customer customer = new Customer(1, "1");
        Assertions.assertNotNull(shoppingService.getCart(customer));
    }

    /**
     * Тест покупки товаров из пустой корзины
     */
    @Test
    void testBuyEmptyCart() throws BuyException {
        Customer customer = new Customer(2L, "2");
        Cart cart = new Cart(customer);
        boolean result = shoppingService.buy(cart);
        Assertions.assertFalse(result);
    }
}
