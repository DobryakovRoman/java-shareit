package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getItems(Long userId);

    ItemDto getItem(Long id);

    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long id, Long userId);

    void removeItem(Long id);

    List<ItemDto> search(String text);
}
