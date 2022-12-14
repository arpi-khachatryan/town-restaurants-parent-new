package am.itspace.townrestaurantsweb.controller;

import am.itspace.townrestaurantscommon.dto.event.CreateEventDto;
import am.itspace.townrestaurantscommon.dto.event.EditEventDto;
import am.itspace.townrestaurantscommon.dto.event.EventOverview;
import am.itspace.townrestaurantsweb.serviceWeb.EventService;
import am.itspace.townrestaurantsweb.serviceWeb.RestaurantService;
import am.itspace.townrestaurantsweb.utilWeb.PageUtil;
import am.itspace.townrestaurantsweb.validation.ErrorMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final RestaurantService restaurantService;

    @GetMapping
    public String events(@RequestParam(value = "page", defaultValue = "1") int page,
                         @RequestParam(value = "size", defaultValue = "6") int size,
                         ModelMap modelMap) {
        Page<EventOverview> allEventsPage = eventService.findAll(PageRequest.of(page - 1, size));
        modelMap.addAttribute("events", allEventsPage);
        modelMap.addAttribute("pageNumbers", PageUtil.getTotalPages(allEventsPage));
        modelMap.addAttribute("restaurants", restaurantService.findAll());
        modelMap.addAttribute("eventsByRestaurant", eventService.sortEventsByRestaurant());
        return "events";
    }

    @GetMapping("/manager")
    public String managerEvents(@RequestParam(value = "page", defaultValue = "1") int page,
                         @RequestParam(value = "size", defaultValue = "8") int size,
                         ModelMap modelMap) {

        Page<EventOverview> allEventsPage = eventService.findAll(PageRequest.of(page - 1, size));
        modelMap.addAttribute("events", allEventsPage);
        modelMap.addAttribute("pageNumbers", PageUtil.getTotalPages(allEventsPage));
        return "manager/events";
    }

    @GetMapping("/{id}")
    public String events(@PathVariable("id") int id, ModelMap modelMap) {
        modelMap.addAttribute("event", eventService.findById(id));
        return "event";
    }

    @GetMapping(value = "/getImages", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@RequestParam("fileName") String fileName) throws IOException {
        return eventService.getEventImage(fileName);
    }

    @GetMapping("/add")
    public String addEventPage(ModelMap modelMap) {
        modelMap.addAttribute("restaurants", restaurantService.findAll());
        return "manager/addEvent";
    }

    @PostMapping("/add")
    public String addEvent(@RequestParam("eventImage") MultipartFile[] files,
                           @ModelAttribute @Valid CreateEventDto dto, BindingResult bindingResult,ModelMap modelMap) throws IOException {
        if (bindingResult.hasErrors()) {
            Map<String, Object> errors = ErrorMap.getErrorMessages(bindingResult);
            modelMap.addAttribute("eventErrors", errors);
            modelMap.addAttribute("restaurants", restaurantService.findAll());
            return "/manager/addEvent";
        }
        eventService.save(dto, files);
        return "redirect:/events/manager";
    }

    @GetMapping("/edit/{id}")
    public String editEventPage(@PathVariable("id") int id, ModelMap modelMap) {
        try {
            modelMap.addAttribute("event", eventService.findById(id));
            modelMap.addAttribute("restaurants", restaurantService.findAll());
            return "manager/editEvent";
        } catch (IllegalStateException ex) {
            modelMap.addAttribute("errorMessageEdit", ex.getMessage());
            return "redirect:/events/manager";
        }
    }

    @PostMapping("/edit/{id}")
    public String editEvent(@PathVariable("id") int id, @ModelAttribute EditEventDto dto,
                            @RequestParam("eventImage") MultipartFile[] files,ModelMap modelMap) throws IOException {
        eventService.editEvent(dto, id, files);
        return "redirect:/events/manager";
    }

    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable("id") int id, ModelMap modelMap) {
        try {
            eventService.deleteEvent(id);
            return "redirect:/events/manager";
        } catch (IllegalStateException e) {
            modelMap.addAttribute("errorMessageId", "Something went wrong, Try again!");
            return "manager/events";
        }
    }
}