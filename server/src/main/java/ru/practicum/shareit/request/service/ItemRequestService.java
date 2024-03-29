package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;


public interface ItemRequestService {

    ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getItemRequestsByUser(Long userId);

    List<ItemRequestDto> getAll(int from, int size, Long userId);

    ItemRequestDto getById(Long requestId, Long userId);
}
