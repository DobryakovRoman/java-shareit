package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.booking.service.BookingStatus;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {

    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final BookingRepository bookingRepository;
    final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItems(Long userId) {
        List<ItemDto> itemDtoList = itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
        itemDtoList.forEach(itemDto -> {
            itemDto.setLastBooking(bookingRepository.findAllByItemIdOrderByStartAsc(itemDto.getId()).isEmpty() ?
                    null : BookingMapper.toBookingShortDto(
                    bookingRepository.findAllByItemIdOrderByStartAsc(itemDto.getId()).get(0)));
            itemDto.setNextBooking(itemDto.getLastBooking() == null ?
                    null : BookingMapper.toBookingShortDto(
                    bookingRepository.findAllByItemIdAndStartAfterOrderByStartAsc(itemDto.getId(),
                            LocalDateTime.now()).get(0)));
            itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId())
                    .stream()
                    .map(ItemDto::toCommentDtoShort)
                    .collect(Collectors.toList()));
        });
        return itemDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItem(Long id, Long ownerId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id: " + id));
        ItemDto itemDto = ItemMapper.toDto(item);
        itemDto.setComments(commentRepository.findAllByItemId(id)
                .stream()
                .map(ItemDto::toCommentDtoShort)
                .collect(Collectors.toList()));
        if (item.getOwner().getId().equals(ownerId)) {
            itemDto.setLastBooking(bookingRepository.findAllByItemIdAndStartBeforeAndStatusEqualsOrderByStartDesc(id,
                    LocalDateTime.now(),
                    BookingStatus.APPROVED).isEmpty() ?
                    null :
                    BookingMapper.toBookingShortDto(bookingRepository.findAllByItemIdAndStartBeforeAndStatusEqualsOrderByStartDesc(id,
                            LocalDateTime.now(),
                            BookingStatus.APPROVED).get(0)));
            itemDto.setNextBooking(bookingRepository.findAllByItemIdAndStartAfterAndStatusEqualsOrderByStartAsc(itemDto.getId(),
                    LocalDateTime.now(),
                    BookingStatus.APPROVED).isEmpty() ?
                    null :
                    BookingMapper.toBookingShortDto(
                            bookingRepository.findAllByItemIdAndStartAfterAndStatusEqualsOrderByStartAsc(itemDto.getId(),
                                    LocalDateTime.now(),
                                    BookingStatus.APPROVED).get(0)
                    ));
        }
        return itemDto;
    }

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("%s %s %d %s",
                        "Невозможно добавить вещь. ", "Пользователь с id:", userId, "не найден")));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        itemRepository.save(item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long id, Long userId) {
        Item item = itemRepository.findById(id)
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
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public void removeItem(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<ItemDto> search(String text) {
        List<ItemDto> resultItems = new ArrayList<>();
        if (text.isBlank()) {
            return resultItems;
        }
        for (Item item : itemRepository.findAll()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase())
                    && item.getAvailable()) {
                resultItems.add(ItemMapper.toDto(item));
            }
        }
        return resultItems;
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("%s %d %s",
                        "Невозможно создать комментарий - пользователь с id", userId, "не найден")));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("%s %d %s",
                        "Невозможно создать комментарий - вещь с id", itemId, "не найдена")));
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(
                userId,
                itemId,
                BookingStatus.APPROVED,
                LocalDateTime.now()).isEmpty()) {
            throw new BadRequestException(String.format("%s %d %s %d",
                    "Невозможно создать комментарий - аренда вещи", itemId, "ещё не завершена пользователем", userId));
        }
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
