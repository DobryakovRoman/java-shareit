package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getItems(int from, int size, Long userId);

    ItemDto getItem(Long id, Long ownerId);

    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long id, Long userId);

    void removeItem(Long id);

    List<ItemDto> search(int from, int size, String text);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);
}
