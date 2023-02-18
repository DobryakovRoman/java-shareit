package ru.practicum.shareit.item.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemInMemoryStorage implements ItemStorage {

    final Map<Long, Item> items = new HashMap<>();
    Long id = 0L;

    @Override
    public List<Item> getItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Optional<Item> getItem(Long id) {
        if (items.get(id) == null) {
            return Optional.empty();
        }
        return Optional.of(items.get(id));
    }

    @Override
    public Item addItem(Item item) {
        item.setId(++id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void removeItem(Long id) {
        items.remove(id);
    }
}
