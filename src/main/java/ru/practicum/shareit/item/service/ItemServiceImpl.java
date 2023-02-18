package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {

    final ItemStorage itemStorage;
    final UserStorage userStorage;

    @Override
    public List<ItemDto> getItems(Long userId) {
        return itemStorage.getItems().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(Long id) {
        Item item = itemStorage.getItem(id)
                .orElseThrow(() -> new NotFoundException(String.format("%s %d %s", "Вещь с id:", id, "не найдена")));
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        User user = userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException(String.format("%s %s %d %s",
                        "Невозможно добавить вещь. ", "Пользователь с id:", userId, "не найден")));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toDto(itemStorage.addItem(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long id, Long userId) {
        Item item = itemStorage.getItem(id)
                .orElseThrow(() -> new NotFoundException(String.format("%s %d %s", "Вещь с id:", id, "не найдена")));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("%s %d %s",
                    "Невозможно обновить вещь - у пользователя с id:", userId, "Вещь не найдена"));
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toDto(itemStorage.updateItem(item));
    }

    @Override
    public void removeItem(Long id) {
        getItem(id);
        itemStorage.removeItem(id);
    }

    @Override
    public List<ItemDto> search(String text) {
        List<ItemDto> resultItems = new ArrayList<>();
        if (text.isBlank()) {
            return resultItems;
        }
        for (Item item : itemStorage.getItems()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase())
                    && item.getAvailable()) {
                resultItems.add(ItemMapper.toDto(item));
            }
        }
        return resultItems;
    }
}
