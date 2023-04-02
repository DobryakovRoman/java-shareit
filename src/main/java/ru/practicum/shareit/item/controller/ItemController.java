package ru.practicum.shareit.item.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ItemController {

    final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItems(@RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("Указаны неправильные параметры запроса");
        }
        return itemService.getItems(from, size, userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItem(id, userId);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable Long id,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.updateItem(itemDto, id, userId);
    }

    @DeleteMapping("/{id}")
    public void removeItem(@PathVariable Long id) {
        itemService.removeItem(id);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam String text) {
        return itemService.search(from, size, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(itemId, userId, commentDto);
    }
}
