package ru.practicum.shareit.request.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestController {

    final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("Указаны неправильные параметры запроса");
        }
        return itemRequestService.getItemRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable Long requestId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}
