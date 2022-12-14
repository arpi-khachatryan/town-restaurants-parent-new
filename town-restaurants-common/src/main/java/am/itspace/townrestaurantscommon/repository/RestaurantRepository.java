package am.itspace.townrestaurantscommon.repository;

import am.itspace.townrestaurantscommon.entity.Restaurant;
import am.itspace.townrestaurantscommon.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface RestaurantRepository extends JpaRepository<Restaurant, Integer>, PagingAndSortingRepository<Restaurant, Integer> {

    boolean existsByName(String name);

    Restaurant findByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);

    Page<Restaurant> findRestaurantsByUserId(int id, Pageable pageable);

    Page<Restaurant> findRestaurantsByUser(User user, Pageable pageable);
}
