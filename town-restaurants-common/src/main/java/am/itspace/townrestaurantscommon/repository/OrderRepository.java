package am.itspace.townrestaurantscommon.repository;

import am.itspace.townrestaurantscommon.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderRepository extends JpaRepository<Order, Integer>, PagingAndSortingRepository<Order, Integer> {
    Page<Order> findByUserId(int id, Pageable pageable);

}
