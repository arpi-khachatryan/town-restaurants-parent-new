package am.itspace.townrestaurantsrest.serviceRest.impl;

import am.itspace.townrestaurantscommon.dto.reserve.CreateReserveDto;
import am.itspace.townrestaurantscommon.dto.reserve.EditReserveDto;
import am.itspace.townrestaurantscommon.dto.reserve.ReserveOverview;
import am.itspace.townrestaurantscommon.entity.*;
import am.itspace.townrestaurantscommon.mapper.ReserveMapper;
import am.itspace.townrestaurantscommon.repository.ReserveRepository;
import am.itspace.townrestaurantscommon.repository.RestaurantRepository;
import am.itspace.townrestaurantsrest.exception.AuthenticationException;
import am.itspace.townrestaurantsrest.exception.EntityAlreadyExistsException;
import am.itspace.townrestaurantsrest.exception.EntityNotFoundException;
import am.itspace.townrestaurantsrest.exception.Error;
import am.itspace.townrestaurantsrest.serviceRest.ReserveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static am.itspace.townrestaurantsrest.exception.Error.NEEDS_AUTHENTICATION;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReserveServiceImpl implements ReserveService {

    private final ReserveMapper reserveMapper;
    private final ReserveRepository reserveRepository;
    private final RestaurantRepository restaurantRepository;
    private final SecurityContextServiceImpl securityContextService;

    @Override
    public ReserveOverview save(CreateReserveDto createReserveDto) {
        if (reserveRepository.existsByPhoneNumber(createReserveDto.getPhoneNumber())) {
            log.info("Reserve already exists {}", createReserveDto.getPhoneNumber());
            throw new EntityAlreadyExistsException(Error.RESERVE_ALREADY_EXISTS);
        } else {
            Reserve reserve = reserveMapper.mapToEntity(createReserveDto);
            reserve.setStatus(ReserveStatus.PENDING);
            log.info("The reserve was successfully stored in the database {}", createReserveDto.getPhoneNumber());
            return reserveMapper.mapToOverview(reserveRepository.save(reserve));
        }
    }

    @Override
    public List<ReserveOverview> getAllReserves(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Reserve> reserves = reserveRepository.findAll(pageable);
        if (reserves.isEmpty()) {
            log.info("Reserve not found");
            throw new EntityNotFoundException(Error.RESERVE_NOT_FOUND);
        }
        List<Reserve> listOfReserves = reserves.getContent();
        log.info("Reserve successfully found");
        return new ArrayList<>(reserveMapper.mapToOverviewList(listOfReserves));
    }

    @Override
    public List<ReserveOverview> getByRole() {
        try {
            User user = securityContextService.getUserDetails().getUser();
            List<Reserve> reserves = reserveRepository.findAll();
            if (reserves.isEmpty()) {
                log.info("Reserve not found");
                throw new EntityNotFoundException(Error.RESERVE_NOT_FOUND);
            }
            if (user.getRole() == Role.MANAGER) {
                log.info("Reserve successfully found");
                return reserveMapper.mapToOverviewList(reserves);
            } else if (user.getRole() == Role.RESTAURANT_OWNER) {
                return reserveForOwner(user);
            } else {
                List<Reserve> reserveByUser = reserveRepository.findByUser(user);
                if (reserveByUser.isEmpty()) {
                    log.info("Reserve not found");
                    throw new EntityNotFoundException(Error.RESERVE_NOT_FOUND);
                }
                return reserveMapper.mapToOverviewList(reserveByUser);
            }
        } catch (ClassCastException e) {
            throw new AuthenticationException(NEEDS_AUTHENTICATION);
        }
    }

    @Override
    public ReserveOverview update(int id, EditReserveDto editReserveDto) {
        Reserve reserve = reserveRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Error.RESERVE_NOT_FOUND));
        log.info("Reserve with that id not found");
        String reservedDate = editReserveDto.getReservedDate();
        if (reservedDate != null) {
            reserve.setReservedDate(LocalDate.parse(reservedDate));
        }
        String reservedTime = editReserveDto.getReservedTime();
        if (reservedDate != null) {
            reserve.setReservedTime(LocalTime.parse(reservedTime));
        }
        int peopleCount = editReserveDto.getPeopleCount();
        if (peopleCount >= 0) {
            reserve.setPeopleCount(peopleCount);
        }
        String phoneNumber = editReserveDto.getPhoneNumber();
        if (phoneNumber != null) {
            reserve.setPhoneNumber(phoneNumber);
        }
        String status = editReserveDto.getStatus();
        if (status != null) {
            reserve.setStatus(ReserveStatus.valueOf(status));
        }
        reserveRepository.save(reserve);
        log.info("The reserve was successfully stored in the database {}", reserve.getPhoneNumber());
        return reserveMapper.mapToOverview(reserve);
    }

    @Override
    public void delete(int id) {
        if (reserveRepository.existsById(id)) {
            reserveRepository.deleteById(id);
            log.info("The reserve has been successfully deleted");
        } else {
            log.info("Reserve not found");
            throw new EntityNotFoundException(Error.RESTAURANT_NOT_FOUND);
        }
    }

    private List<ReserveOverview> reserveForOwner(User user) {
        List<Reserve> reservesForOwner = new ArrayList<>();
        List<Restaurant> restaurants = restaurantRepository.findAll();
        if (!restaurants.isEmpty()) {
            for (Restaurant restaurant : restaurants) {
                if (restaurant != null && restaurant.getUser().getId().equals(user.getId())) {
                    List<Reserve> reserveByRestaurant = reserveRepository.findByRestaurantId(restaurant.getId());
                    reservesForOwner.addAll(reserveByRestaurant);
                }
                log.info("Reserve successfully detected");
                return reserveMapper.mapToOverviewList(reservesForOwner);
            }
        }
        log.info("Reserve not found");
        throw new EntityNotFoundException(Error.RESERVE_NOT_FOUND);
    }
}
