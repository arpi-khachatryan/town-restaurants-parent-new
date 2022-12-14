package am.itspace.townrestaurantsrest.serviceRest;

import am.itspace.townrestaurantscommon.dto.restaurantCategory.CreateRestaurantCategoryDto;
import am.itspace.townrestaurantscommon.dto.restaurantCategory.EditRestaurantCategoryDto;
import am.itspace.townrestaurantscommon.dto.restaurantCategory.RestaurantCategoryOverview;
import am.itspace.townrestaurantsrest.exception.EntityNotFoundException;

import java.util.List;

public interface RestaurantCategoryService {

    void delete(int id);

    RestaurantCategoryOverview getById(int id) throws EntityNotFoundException;

    RestaurantCategoryOverview save(CreateRestaurantCategoryDto createRestaurantCategoryDto);

    RestaurantCategoryOverview update(int id, EditRestaurantCategoryDto editRestaurantCategoryDto);

    List<RestaurantCategoryOverview> getAllCategories(int pageNo, int pageSize, String sortBy, String sortDir);
}

