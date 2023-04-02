package ru.practicum.shareit.request.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    UserRepository userRepository;

    User firstUser;
    ItemRequest firstItemRequest;
    ItemRequest secondItemRequest;


    @BeforeEach
    void beforeEach() {
        firstUser = User.builder().name("firstUser").email("firstUser@ya.ru").build();
        userRepository.save(firstUser);
        firstItemRequest = ItemRequest.builder().id(1L).requestor(firstUser).created(LocalDateTime.now()).description("Description").build();
        itemRequestRepository.save(firstItemRequest);
        secondItemRequest = ItemRequest.builder().id(2L).requestor(firstUser).created(LocalDateTime.now()).description("secondDescription").build();
        itemRequestRepository.save(secondItemRequest);
    }

    @Test
    void findAllByRequestorIdOrderByCreatedAsc() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(firstUser.getId());
        assertEquals(2, itemRequests.size());
    }

    @Test
    void findAllByRequestorNotLikeOrderByCreatedAsc() {
        List<ItemRequestDto> itemRequests = itemRequestRepository
                .findAllByRequestorNotLikeOrderByCreatedAsc(firstUser, PageRequest.of(1, 1))
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        assertEquals(0, itemRequests.size());
    }
}