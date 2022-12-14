package am.itspace.townrestaurantsweb.serviceWeb.impl;

import am.itspace.townrestaurantscommon.dto.basket.BasketOverview;
import am.itspace.townrestaurantscommon.entity.Basket;
import am.itspace.townrestaurantscommon.entity.Product;
import am.itspace.townrestaurantscommon.entity.User;
import am.itspace.townrestaurantscommon.mapper.BasketMapper;
import am.itspace.townrestaurantscommon.repository.BasketRepository;
import am.itspace.townrestaurantscommon.repository.ProductRepository;
import am.itspace.townrestaurantsweb.serviceWeb.BasketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasketServiceImpl implements BasketService {

    private final BasketMapper basketMapper;
    private final BasketRepository basketRepository;
    private final ProductRepository productRepository;

    @Override
    public Page<BasketOverview> getBaskets(Pageable pageable, User user) {
        List<Basket> basketByUser = basketRepository.findBasketByUser(user);
        Page<Basket> basketPage = new PageImpl<>(basketByUser);
        log.info("Baskets successfully found");
        return basketPage.map(basketMapper::mapToDto);
    }

    @Override
    public List<BasketOverview> getBaskets(User user) {
        log.info("Baskets successfully found");
        return basketMapper.mapToDtoList(basketRepository.findBasketByUser(user));
    }

    public void addProductToBasket(int id, User user) {
        if (user == null) {
            throw new IllegalStateException("Something went wrong, try again!");
        }
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new IllegalStateException("Something went wrong, try again!");
        }
        Product product = productOptional.get();
        Optional<Basket> basketOptional = basketRepository.findBasketByProductAndUser(product, user);
        if (basketOptional.isEmpty()) {
            Basket basket = new Basket();
            basket.setProduct(product);
            basket.setQuantity(1);
            basket.setUser(user);
            basketRepository.save(basket);
            log.info("Product successfully added to basket");
        } else {
            Basket basket = basketOptional.get();
            basket.setQuantity(basket.getQuantity() + 1);
            basketRepository.save(basket);
            log.info("Product successfully added to basket");
        }
    }

    public double totalPrice(User user) {
        double totalPrice = 0;
        List<Basket> basketByUser = basketRepository.findBasketByUser(user);
        if (!basketByUser.isEmpty()) {
            for (Basket basket : basketByUser) {
                Product product = basket.getProduct();
                totalPrice += product.getPrice() * basket.getQuantity();
            }
        }
        log.info("The total price is calculated");
        return totalPrice;
    }


    public void delete(int id, User user) {
        Product product = productRepository.findById(id).orElseThrow();
        Basket basket = basketRepository.findBasketByProductAndUser(product, user).orElseThrow(() -> new IllegalStateException("Something went wrong, try again!"));
        double quantity = basket.getQuantity();
        if (quantity == 1) {
            basket.setQuantity(0);
            basketRepository.delete(basket);
            log.info("The product has been successfully deleted from basket");
        } else {
            quantity = quantity - 1;
            basket.setQuantity(quantity);
            basketRepository.save(basket);
            log.info("The product has been successfully deleted from basket");
        }
    }
}
