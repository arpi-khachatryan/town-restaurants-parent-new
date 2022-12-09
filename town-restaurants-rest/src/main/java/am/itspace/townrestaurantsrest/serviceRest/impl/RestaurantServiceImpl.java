package am.itspace.townrestaurantsrest.serviceRest.impl;

import am.itspace.townrestaurantscommon.dto.FileDto;
import am.itspace.townrestaurantscommon.dto.FetchRequestDto;
import am.itspace.townrestaurantscommon.dto.restaurant.CreateRestaurantDto;
import am.itspace.townrestaurantscommon.dto.restaurant.EditRestaurantDto;
import am.itspace.townrestaurantscommon.dto.restaurant.RestaurantOverview;
import am.itspace.townrestaurantscommon.dto.restaurantCategory.RestaurantCategoryOverview;
import am.itspace.townrestaurantscommon.entity.Restaurant;
import am.itspace.townrestaurantscommon.entity.RestaurantCategory;
import am.itspace.townrestaurantscommon.mapper.RestaurantCategoryMapper;
import am.itspace.townrestaurantscommon.mapper.RestaurantMapper;
import am.itspace.townrestaurantscommon.repository.RestaurantCategoryRepository;
import am.itspace.townrestaurantscommon.repository.RestaurantRepository;
import am.itspace.townrestaurantscommon.utilCommon.FileUtil;
import am.itspace.townrestaurantsrest.exception.*;
import am.itspace.townrestaurantsrest.exception.Error;
import am.itspace.townrestaurantsrest.serviceRest.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static am.itspace.townrestaurantsrest.exception.Error.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final FileUtil fileUtil;
    private final RestaurantMapper restaurantMapper;
    private final RestaurantCategoryRepository restaurantCategoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final SecurityContextServiceImpl securityContextService;

    @Override
    public RestaurantOverview save(CreateRestaurantDto createRestaurantDto, FileDto fileDto) {
        if (restaurantRepository.existsByName(createRestaurantDto.getName())) {
            log.info("Restaurant with that name already exists {}", createRestaurantDto.getName());
            throw new EntityAlreadyExistsException(Error.RESTAURANT_ALREADY_EXISTS);
        }
        try {
            MultipartFile[] files = fileDto.getFiles();
            for (MultipartFile file : files) {
                if (!file.isEmpty() && file.getSize() > 0) {
                    if (file.getContentType() != null && !file.getContentType().contains("image")) {
                        throw new MyFileNotFoundException(FILE_NOT_FOUND);
                    }
                }
            }
            createRestaurantDto.setPictures(fileUtil.uploadImages(files));
        } catch (IOException e) {
            throw new FileStorageException(FILE_UPLOAD_FAILED);
        }
        log.info("The restaurant was successfully stored in the database {}", createRestaurantDto.getName());
        return restaurantMapper.mapToResponseDto(restaurantRepository.save(restaurantMapper.mapToEntity(createRestaurantDto)));
    }

    @Override
    public byte[] getRestaurantImage(String fileName) {
        try {
            log.info("Images successfully found");
            return FileUtil.getImage(fileName);
        } catch (IOException e) {
            throw new MyFileNotFoundException(FILE_NOT_FOUND);
        }
    }

    @Override
    public List<RestaurantOverview> getAll() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        if (restaurants.isEmpty()) {
            log.info("Restaurant not found");
            throw new EntityNotFoundException(Error.RESTAURANT_NOT_FOUND);
        } else {
            log.info("Restaurant successfully found");
            return restaurantMapper.mapToResponseDtoList(restaurants);
        }
    }

    @Override
    public List<Restaurant> getRestaurantsList(FetchRequestDto dto) {
        PageRequest pageReq
                = PageRequest.of(dto.getPage(), dto.getSize(), Sort.Direction.fromString(dto.getSortDir()), dto.getSort());
        Page<Restaurant> restaurants = restaurantRepository.findByRestaurantEmail(dto.getInstance(), pageReq);
        if (restaurants.isEmpty()) {
            log.info("Restaurant not found");
            throw new EntityNotFoundException(Error.RESTAURANT_NOT_FOUND);
        }
        return restaurants.getContent();
    }

    @Override
    public List<Restaurant> getRestaurantsByUser(FetchRequestDto dto) {
        try {
            List<Restaurant> restaurantsByUser = restaurantRepository.findRestaurantsByUserId(securityContextService.getUserDetails().getUser().getId());
            if (!restaurantsByUser.isEmpty()) {
                for (Restaurant restaurant : restaurantsByUser) {
                    PageRequest pageReq = PageRequest.of(dto.getPage(), dto.getSize(), Sort.Direction.fromString(dto.getSortDir()), dto.getSort());
                    Page<Restaurant> restaurants = restaurantRepository.findByRestaurantEmail(restaurant.getEmail(), pageReq);
                    return restaurants.getContent();
                }
            }
            throw new EntityNotFoundException(Error.RESTAURANT_NOT_FOUND);
        } catch (ClassCastException e) {
            throw new AuthenticationException(NEEDS_AUTHENTICATION);
        }
    }

    @Override
    public RestaurantOverview getById(int id) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Error.RESTAURANT_NOT_FOUND));
        log.info("Restaurant successfully found {}", restaurant.getName());
        return restaurantMapper.mapToResponseDto(restaurant);
    }

    @Override
    public RestaurantOverview update(int id, EditRestaurantDto editRestaurantDto) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Error.RESTAURANT_NOT_FOUND));
        log.info("Restaurant with that id not found");
        String name = editRestaurantDto.getName();
        if (StringUtils.hasText(name)) {
            restaurant.setName(name);
        }
        String email = editRestaurantDto.getEmail();
        if (StringUtils.hasText(email)) {
            restaurant.setEmail(email);
        }
        String phone = editRestaurantDto.getPhone();
        if (StringUtils.hasText(phone)) {
            restaurant.setPhone(phone);
        }
        String address = editRestaurantDto.getAddress();
        if (StringUtils.hasText(address)) {
            restaurant.setAddress(address);
        }
        Double deliveryPrice = editRestaurantDto.getDeliveryPrice();
        if (deliveryPrice != null) {
            restaurant.setDeliveryPrice(deliveryPrice);
        }
        Integer restaurantCategoryId = editRestaurantDto.getRestaurantCategoryId();
        if (restaurantCategoryId != null) {
            restaurant.setRestaurantCategory(restaurantCategoryRepository.getReferenceById(restaurantCategoryId));
        }
        restaurantRepository.save(restaurant);
        log.info("The restaurant was successfully stored in the database {}", restaurant.getName());
        return restaurantMapper.mapToResponseDto(restaurant);
    }

    @Override
    public void delete(int id) {
        if (restaurantRepository.existsById(id)) {
            restaurantRepository.deleteById(id);
            log.info("The restaurant has been successfully deleted");
        } else {
            log.info("Restaurant not found");
            throw new EntityNotFoundException(Error.RESTAURANT_NOT_FOUND);
        }
    }

//    @Override
//    public ImageOverview uploadImage(int restaurantId, MultipartFile multipartFile) {
//        checkNotNull(multipartFile, IMAGE_IS_REQUIRED);
//        Optional<Restaurant> byId = restaurantRepository.findById(restaurantId);
////        User user = findUserById(userId);
//        imageService.uploadImagesToS3("user", userId, user.getImageVersion(), multipartFile, userId);
//
//        user.setImageVersion(user.getImageVersion() + 1);
//        user = userRepository.saveAndFlush(user);
//        return userMapper.mapToUserImageOverview(user);
//    }


//    public ImageOverview uploadImage(UUID userId, MultipartFile multipartFile) {
//        checkNotNull(multipartFile, IMAGE_IS_REQUIRED);
//        User user = findUserById(userId);
//        imageService.uploadImagesToS3("user", userId, user.getImageVersion(), multipartFile, userId);
//
//        user.setImageVersion(user.getImageVersion() + 1);
//        user = userRepository.saveAndFlush(user);
//        return userMapper.mapToUserImageOverview(user);
//    }


//    @Override
//    public void deleteImage(UUID userId) {
//        User user =
//                userRepository
//                        .findById(userId)
//                        .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
//        String className = User.class.getSimpleName().toLowerCase();
//
//        imageService.checkExistenceOfObject(
//                imageService.getImagePath(className, userId, user.getImageVersion(), true), userId);
//        imageService.checkExistenceOfObject(
//                imageService.getImagePath(className, userId, user.getImageVersion(), false), userId);
//        imageService.deleteImagesFromS3("user", userId, user.getImageVersion());
//    }

}
