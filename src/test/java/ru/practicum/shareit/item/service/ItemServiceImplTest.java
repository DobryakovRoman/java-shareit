package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingStatus;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @InjectMocks
    ItemServiceImpl itemServiceImpl;

    User firstUser;
    User secondUser;
    Item item;
    ItemDto itemDto;
    Booking booking;
    Comment comment;

    @BeforeEach
    void beforeEach() {
        firstUser = User.builder().id(1L).name("firstUser").email("firstUser@ya.ru").build();
        secondUser = User.builder().id(2L).name("secondUser").email("secondUser@ya.ru").build();
        item = Item.builder().id(1L).name("firstName").description("Description").available(true).owner(firstUser).build();
        itemDto = ItemMapper.toDto(item);
        comment = Comment.builder().text("Text").item(item).author(firstUser).created(LocalDateTime.now()).build();
        booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .build();
    }

    @Test
    void getItemsTest() {
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(new PageImpl<>(List.of(item)));
        itemDto.setComments(new ArrayList<>());

        List<ItemDto> result = itemServiceImpl.getItems(0, 10, firstUser.getId());

        verify(itemRepository, times(1)).findAllByOwnerId(firstUser.getId(), Pageable.ofSize(10));
        assertEquals(itemDto.getId(), result.get(0).getId());
    }

    @Test
    void getItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDto result = itemServiceImpl.getItem(item.getId(), firstUser.getId());

        verify(itemRepository, times(1)).findById(anyLong());
        assertEquals(itemDto.getId(), result.getId());
    }

    @Test
    void addItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        itemServiceImpl.addItem(itemDto, firstUser.getId());

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(captor.capture());

        Item savedItem = captor.getValue();
        assertEquals(itemDto.getName(), savedItem.getName());
        assertEquals(itemDto.getDescription(), savedItem.getDescription());
        assertEquals(itemDto.getAvailable(), savedItem.getAvailable());
        assertEquals(firstUser, savedItem.getOwner());
    }

    @Test
    void updateItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        itemServiceImpl.updateItem(itemDto, item.getId(), firstUser.getId());

        verify(itemRepository).save(any());
    }

    @Test
    void removeItem() {
        doNothing().when(itemRepository).deleteById(item.getId());

        itemServiceImpl.removeItem(item.getId());

        verify(itemRepository).deleteById(item.getId());
    }

    @Test
    void searchTest() {
        when(itemRepository.findAll()).thenReturn(List.of(item));

        List<ItemDto> result = itemServiceImpl.search(0, 10, "Description");

        verify(itemRepository).findAll();
        assertEquals(item.getDescription(), result.get(0).getDescription());
    }

    @Test
    void addComment() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(any(), any(), any(), any())).thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);
        CommentDto commentDto = itemServiceImpl.addComment(firstUser.getId(), item.getId(), CommentMapper.toCommentDto(comment));

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());

        assertEquals(CommentMapper.toCommentDto(comment).getId(), commentDto.getId());
    }
}