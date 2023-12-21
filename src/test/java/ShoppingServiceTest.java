import customer.Customer;
import customer.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import product.Product;
import product.ProductDao;
import shopping.BuyException;
import shopping.Cart;
import shopping.ShoppingService;
import shopping.ShoppingServiceImpl;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестирование класса {@link ShoppingServiceImpl}
 */
public class ShoppingServiceTest {

    public final ProductDao productDao = mock(ProductDao.class);

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
        Mockito.verify(productDao, times(1)).save(firstProduct);
        Mockito.verify(productDao, times(1)).save(secondProduct);
        assertTrue(result);
        assertEquals(9, firstProduct.getCount());
        assertEquals(1, secondProduct.getCount());
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

        assertEquals(mockProducts, products);
    }

    /**
     * Тест покупки товаров из пустой корзины
     */
    @Test
    void testBuyEmptyCart() throws BuyException {
        Customer customer = new Customer(2L, "customer");
        Cart cart = new Cart(customer);
        boolean result = shoppingService.buy(cart);
        Assertions.assertFalse(result);
    }
    /**
     * Проверяет сценарий, когда один покупатель покупает часть товара, а затем другой покупатель пытается купить оставшийся товар.
     *
     * <p>Создается макет {@code ProductDao} и готовятся тестовые данные для двух покупателей. Первый покупатель приобретает
     * часть доступного товара, а затем второй покупатель пытается приобрести оставшийся товар.
     * @throws BuyException если покупка не может быть совершена из-за нехватки товара на складе.
     */
    @Test
    public void testBuyProductsException() throws BuyException {
        ProductDao productDaoMock = mock(ProductDao.class);
        ShoppingService shoppingService = new ShoppingServiceImpl(productDaoMock);
        Product testProduct = new Product();
        testProduct.setName("apple");
        testProduct.addCount(5);
        Customer firstCustomer = new Customer(1L, "firstCustomer");
        Cart firstCustomerCart = new Cart(firstCustomer);
        firstCustomerCart.add(testProduct, 4);
        Customer secondCustomer = new Customer(2L, "secondCustomer");
        Cart secondCustomerCart = new Cart(secondCustomer);
        secondCustomerCart.add(testProduct, 4);
        when(productDaoMock.getByName("apple")).thenReturn(testProduct);
        shoppingService.buy(firstCustomerCart);
        BuyException buyException = assertThrows(BuyException.class, () -> {
            boolean secondCustomerBuy = shoppingService.buy(secondCustomerCart);
            assertFalse(secondCustomerBuy);
        });

        assertEquals("В наличии нет необходимого количества товара apple", buyException.getMessage());
    }

    /**
     * Проверяет сценарий, когда один покупатель покупает часть доступного товара, а затем другой покупатель пытается
     * приобрести оставшийся товар после успешной покупки первого покупателя.
     * Этот тест предполагает, что после успешной покупки первого покупателя,
     * второй покупатель не сможет приобрести больше товара, чем осталось после первой покупки.
     */
    @Test
    public void testBuyProducts_SecondCustomerAttempt() throws BuyException {
        ProductDao productDaoMock = mock(ProductDao.class);
        ShoppingService shoppingService = new ShoppingServiceImpl(productDaoMock);

        Product testProduct = new Product();
        testProduct.setName("apple");
        testProduct.addCount(5);

        Customer firstCustomer = new Customer(1L, "firstCustomer");
        Cart firstCustomerCart = new Cart(firstCustomer);
        firstCustomerCart.add(testProduct, 4);

        Customer secondCustomer = new Customer(2L, "secondCustomer");
        Cart secondCustomerCart = new Cart(secondCustomer);
        secondCustomerCart.add(testProduct, 4);

        when(productDaoMock.getByName("apple")).thenReturn(testProduct);
        shoppingService.buy(firstCustomerCart);
        boolean secondCustomerBuy = shoppingService.buy(secondCustomerCart);
        assertFalse(secondCustomerBuy);
        assertEquals(1, testProduct.getCount());
        verify(productDaoMock, times(1)).save(testProduct);
    }
}
