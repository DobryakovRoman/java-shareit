package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestServiceImpl implements ItemRequestService {
    final ItemRequestRepository itemRequestRepository;
    final UserRepository userRepository;
    final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("%s %d", "Невозможно создать запрос - не найден пользователь с id", userId)));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getItemRequestsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("%s %d", "Невозможно найти запросы пользователя с id", userId)));
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(userId)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        itemRequestDtos
                .forEach((i) -> i.setItems(
                        itemRepository.findAllByRequestId(i.getId())
                                .stream()
                                .map(ItemMapper::toShortDto)
                                .collect(Collectors.toList())));
        return itemRequestDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getItemRequests(int from, int size, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("%s %d", "Невозможно найти запросы пользователя с id", userId)));
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findAllByRequestorNotLikeOrderByCreatedAsc(user,
                        PageRequest.of(from, size))
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        itemRequestDtos
                .forEach((i) -> i.setItems(
                        itemRepository.findAllByRequestId(i.getId())
                                .stream()
                                .map(ItemMapper::toShortDto)
                                .collect(Collectors.toList())));
        return itemRequestDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getItemRequestById(Long requestId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("%s %d", "Невозможно найти запрос пользователя с id", userId)));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("%s %d", "Невозможно найти запрос с id", requestId)));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequestDto.getId())
                .stream()
                .map(ItemMapper::toShortDto)
                .collect(Collectors.toList()));
        return itemRequestDto;
    }
}
