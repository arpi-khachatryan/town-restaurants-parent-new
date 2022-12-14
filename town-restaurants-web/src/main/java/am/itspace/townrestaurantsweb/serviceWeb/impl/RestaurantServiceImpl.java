package am.itspace.townrestaurantsweb.serviceWeb.impl;

import am.itspace.townrestaurantscommon.dto.restaurant.CreateRestaurantDto;
import am.itspace.townrestaurantscommon.dto.restaurant.EditRestaurantDto;
import am.itspace.townrestaurantscommon.dto.restaurant.RestaurantOverview;
import am.itspace.townrestaurantscommon.entity.Restaurant;
import am.itspace.townrestaurantscommon.entity.User;
import am.itspace.townrestaurantscommon.mapper.RestaurantMapper;
import am.itspace.townrestaurantscommon.repository.RestaurantCategoryRepository;
import am.itspace.townrestaurantscommon.repository.RestaurantRepository;
import am.itspace.townrestaurantscommon.utilCommon.FileUtil;
import am.itspace.townrestaurantsweb.serviceWeb.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final FileUtil fileUtil;
    private final RestaurantMapper restaurantMapper;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantCategoryRepository restaurantCategoryRepository;

    public List<RestaurantOverview> findAll() {
        List<RestaurantOverview> restaurantOverviews = restaurantMapper.mapToResponseDtoList(restaurantRepository.findAll());
        if (!restaurantOverviews.isEmpty()) {
            log.info("Restaurant successfully found");
        }
        return restaurantOverviews;
    }

    @Override
    public Page<RestaurantOverview> findAllRestaurants(Pageable pageable) {
        Page<Restaurant> restaurants = restaurantRepository.findAll(pageable);
        if (!restaurants.isEmpty()) {
            log.info("Restaurant successfully found");
        }
        return restaurants.map(restaurantMapper::mapToResponseDto);
    }

    @Override
    public Page<RestaurantOverview> getRestaurantsByUser(User user, Pageable pageable) {
        Page<Restaurant> restaurantsByUserId = restaurantRepository.findRestaurantsByUserId(user.getId(), pageable);
        log.info("Restaurant successfully found");
        return restaurantsByUserId.map(restaurantMapper::mapToResponseDto);
    }

    @Override
    public void addRestaurant(CreateRestaurantDto dto, MultipartFile[] files, User user) throws IOException {
        if (restaurantRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            log.info("Restaurant with that email already exists {}", dto.getName());
            throw new IllegalStateException();
        }
        dto.setPictures(fileUtil.uploadImages(files));
        Restaurant restaurant = restaurantMapper.mapToEntity(dto);
        restaurant.setUser(user);
        restaurantRepository.save(restaurant);
        log.info("The restaurant was successfully stored in the database {}", dto.getName());
    }

    @Override
    public byte[] getRestaurantImage(String fileName) {
        try {
            log.info("Images successfully found");
            return fileUtil.getImage(fileName);
        } catch (IOException ex) {
            log.info("Images not found");
            throw new IllegalStateException("Something went wrong, try again!");
        }
    }

    @Override
    public void deleteRestaurant(int id) {
        if (!restaurantRepository.existsById(id)) {
            log.info("Restaurant not found");
            throw new IllegalStateException("Something went wrong, try again!");
        }
        log.info("The restaurant has been successfully deleted");
        restaurantRepository.deleteById(id);
    }

    @Override
    public void editRestaurant(EditRestaurantDto dto, int id) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(() -> new IllegalStateException("Something went wrong, try again!"));
        String name = dto.getName();
        if (StringUtils.hasText(name)) {
            restaurant.setName(name);
        }
        String email = dto.getEmail();
        if (StringUtils.hasText(email)) {
            restaurant.setEmail(email);
        }
        String phone = dto.getPhone();
        if (StringUtils.hasText(phone)) {
            restaurant.setPhone(phone);
        }
        String address = dto.getAddress();
        if (StringUtils.hasText(address)) {
            restaurant.setAddress(address);
        }
        Double deliveryPrice = dto.getDeliveryPrice();
        if (deliveryPrice != null) {
            restaurant.setDeliveryPrice(deliveryPrice);
        }
        Integer restaurantCategoryId = dto.getRestaurantCategoryId();
        if (restaurantCategoryId != null) {
            restaurant.setRestaurantCategory(restaurantCategoryRepository.getReferenceById(restaurantCategoryId));
        }
        log.info("The restaurant was successfully stored in the database {}", restaurant.getName());
        restaurantRepository.save(restaurant);
    }

    @Override
    public RestaurantOverview getRestaurant(int id) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(() -> new IllegalStateException("Restaurant not found!"));
        log.info("Restaurant successfully found {}", restaurant.getName());
        return restaurantMapper.mapToResponseDto(restaurant);
    }
}
