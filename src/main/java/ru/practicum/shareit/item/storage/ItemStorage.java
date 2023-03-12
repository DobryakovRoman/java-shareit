package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    List<Item> getItems();

    Optional<Item> getItem(Long id);

    Item addItem(Item item);

    Item updateItem(Item item);

    void removeItem(Long id);
}
