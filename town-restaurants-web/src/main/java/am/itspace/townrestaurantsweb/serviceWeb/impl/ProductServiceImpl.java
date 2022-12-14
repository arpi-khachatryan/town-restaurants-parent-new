package am.itspace.townrestaurantsweb.serviceWeb.impl;

import am.itspace.townrestaurantscommon.dto.product.CreateProductDto;
import am.itspace.townrestaurantscommon.dto.product.EditProductDto;
import am.itspace.townrestaurantscommon.dto.product.ProductOverview;
import am.itspace.townrestaurantscommon.entity.*;
import am.itspace.townrestaurantscommon.mapper.ProductMapper;
import am.itspace.townrestaurantscommon.repository.ProductCategoryRepository;
import am.itspace.townrestaurantscommon.repository.ProductRepository;
import am.itspace.townrestaurantscommon.repository.RestaurantRepository;
import am.itspace.townrestaurantscommon.utilCommon.FileUtil;
import am.itspace.townrestaurantsweb.serviceWeb.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final FileUtil fileUtil;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final RestaurantRepository restaurantRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Override
    public Page<ProductOverview> sortProduct(Pageable pageable, String sort, Integer id) {
        Page<Product> products;
        if (id != null) {
            products = productRepository.findProductsByProductCategory_Id(id, pageable);
            return products.map(productMapper::mapToResponseDto);
        }
        products = switch (sort) {
            case "price_asc" -> productRepository.findByOrderByPriceAsc(pageable);
            case "price_desc" -> productRepository.findByOrderByPriceDesc(pageable);
            default -> productRepository.findAll(pageable);
        };
        if (products.isEmpty()) {
            log.info("Products not found!");
        } else {
            log.info("Products successfully found");
        }
        return products.map(productMapper::mapToResponseDto);
    }

    @Override
    public List<ProductOverview> findAll() {
        log.info("Products successfully found");
        return productMapper.mapToOverviewList(productRepository.findAll());
    }

    @Override
    public List<ProductOverview> findAllById(int id) {
        List<Product> products = productRepository.findAllById(id);
        if (products.isEmpty()) {
            throw new IllegalStateException("Products not found!");
        }
        log.info("Products successfully detected");
        return productMapper.mapToOverviewList(products);
    }

    @Override
    public void addProduct(CreateProductDto dto, MultipartFile[] files, User user) {
        try {
            if (StringUtils.hasText(dto.getName()) && dto.getPrice() >= 0) {
                Product product = productMapper.mapToEntity(dto);
                product.setUser(user);
                product.setPictures(fileUtil.uploadImages(files));
                productRepository.save(product);
                log.info("The product was successfully stored in the database {}", dto.getName());
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Something went wrong, try again!");
        }
    }

    @Override
    public void editProduct(EditProductDto dto, int id, MultipartFile[] files) throws IOException {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            log.info("Product not found");
            throw new IllegalStateException("Product not found!");
        }
        Product product = productOptional.get();
        String name = dto.getName();
        if (StringUtils.hasText(name)) {
            product.setName(name);
        }
        String description = dto.getDescription();
        if (StringUtils.hasText(description)) {
            product.setDescription(description);
        }
        Double price = dto.getPrice();
        if (price >= 0) {
            product.setPrice(price);
        }
        Integer restaurantId = dto.getRestaurantId();
        if (restaurantId != null) {
            Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);
            optionalRestaurant.ifPresent(product::setRestaurant);
        }
        Integer productCategoryId = dto.getProductCategoryId();
        if (productCategoryId != null) {
            Optional<ProductCategory> optionalCategory = productCategoryRepository.findById(productCategoryId);
            optionalCategory.ifPresent(product::setProductCategory);
        }
        List<String> pictures = dto.getPictures();
        if (pictures != null) {
            product.setPictures(fileUtil.uploadImages(files));
        }
        log.info("The product was successfully stored in the database {}", product.getName());
        productRepository.save(product);
    }

    @Override
    public byte[] getProductImage(String fileName) {
        try {
            log.info("Image successfully found");
            return fileUtil.getImage(fileName);
        } catch (IOException ex) {
            throw new IllegalStateException("Something went wrong, try again!");
        }
    }

    @Override
    public void deleteProduct(int id, User user) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            log.info("Product not found");
            throw new IllegalStateException("Product not found!");
        }
        if ((user.getRole() == Role.MANAGER) || (user.getId().equals(productOptional.get().getUser().getId()))) {
            productRepository.deleteById(id);
            log.info("The product has been successfully deleted");
        }
    }

    @Override
    public ProductOverview findById(int id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalStateException("Something went wrong, try again!"));
        log.info("Product successfully found");
        return productMapper.mapToResponseDto(product);
    }

    @Override
    public List<ProductOverview> findProductByUser(User user) {
        log.info("Product successfully found");
        return productMapper.mapToOverviewList(productRepository.findProductByUser(user));
    }

    @Override
    public List<ProductOverview> findProductsByRestaurant(int id) {
        List<Product> products = productRepository.findProductsByRestaurant_Id(id);
        if (products.isEmpty()) {
            log.info("Product not found");
//            throw new IllegalStateException("Product not found!");
        }
        log.info("Product successfully found");
        return productMapper.mapToOverviewList(products);
    }
}
