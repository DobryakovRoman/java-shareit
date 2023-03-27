package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestServiceImplTest {

    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    ItemRequestServiceImpl itemRequestServiceimpl;

    User firstUser;
    User secondUser;
    Item item;
    ItemDto itemDto;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;

    @BeforeEach
    void beforeEach() {
        firstUser = User.builder().id(1L).name("firstUser").email("firstUser@ya.ru").build();
        secondUser = User.builder().id(2L).name("secondUser").email("secondUser@ya.ru").build();
        itemRequest = ItemRequest.builder().id(1L).created(LocalDateTime.now()).description("Description").build();
        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        item = Item.builder().id(1L).name("firstName").description("Description").available(true).owner(firstUser).build();
        itemDto = ItemMapper.toDto(item);
    }

    @Test
    void addItemRequest() {
        itemRequestDto.setItems(new ArrayList<>());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));

        ItemRequestDto result = itemRequestServiceimpl.addItemRequest(2L, itemRequestDto);

        verify(itemRequestRepository).save(any());
        assertNotNull(result);
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        assertEquals(itemRequestDto.getItems(), result.getItems());
    }

    @Test
    void getItemRequestsByUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(any())).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestServiceimpl.getItemRequestsByUser(firstUser.getId());

        verify(itemRepository, times(1)).findAllByRequestId(anyLong());
        assertEquals(itemRequestDto.getId(), result.get(0).getId());
    }

    @Test
    void getItemRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(itemRequestRepository.findAllByRequestorNotLikeOrderByCreatedAsc(any(), any())).thenReturn(Page.empty());

        List<ItemRequestDto> result = itemRequestServiceimpl.getItemRequests(1, 1, firstUser.getId());

        verify(itemRequestRepository).findAllByRequestorNotLikeOrderByCreatedAsc(any(), any());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getItemRequestById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(itemRequestRepository.findById(any())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestDto result = itemRequestServiceimpl.getItemRequestById(itemRequest.getId(), firstUser.getId());

        verify(itemRequestRepository).findById(any());
        assertEquals(itemRequestDto.getId(), result.getId());
    }
}