package am.itspace.townrestaurantsweb.controller;

import am.itspace.townrestaurantscommon.dto.restaurantCategory.CreateRestaurantCategoryDto;
import am.itspace.townrestaurantscommon.dto.restaurantCategory.RestaurantCategoryOverview;
import am.itspace.townrestaurantsweb.serviceWeb.RestaurantCategoryService;
import am.itspace.townrestaurantsweb.utilWeb.PageUtil;
import am.itspace.townrestaurantsweb.validation.ErrorMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/restaurantsCategory")
public class RestaurantCategoryController {

    private final RestaurantCategoryService restaurantCategoryService;

    @GetMapping
    public String restaurants(@RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "size", defaultValue = "5") int size,
                              ModelMap modelMap) {
        Page<RestaurantCategoryOverview> categories = restaurantCategoryService.findAll(PageRequest.of(page - 1, size));
        modelMap.addAttribute("categories", categories);
        modelMap.addAttribute("pageNumbers", PageUtil.getTotalPages(categories));
        return "restaurantCategory";
    }

    @GetMapping("/add")
    public String addRestaurantPage() {
        return "addRestaurantCategory";
    }

    @PostMapping("/add")
    public String addRestaurant(@ModelAttribute @Valid CreateRestaurantCategoryDto dto, BindingResult bindingResult, ModelMap modelMap) {
        if (bindingResult.hasErrors()) {
            Map<String, Object> errors = ErrorMap.getErrorMessages(bindingResult);
            modelMap.addAttribute("categoryErrors", errors);
            return "addRestaurantCategory";
        }
        restaurantCategoryService.addRestaurantCategory(dto);
        return "redirect:/restaurantsCategory";
    }

    @GetMapping("/delete/{id}")
    public String deleteRestaurantCategory(@PathVariable("id") int id, ModelMap modelMap) {
        try {
            restaurantCategoryService.deleteRestaurantCategory(id);
            return "redirect:/restaurantsCategory";
        } catch (IllegalStateException e) {
            modelMap.addAttribute("errorMessageId", "Something went wrong, Try again!");
            return "restaurantCategory";
        }
    }
}
