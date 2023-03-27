package ru.practicum.shareit.item.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    User firstUser;


    @BeforeEach
    void beforeEach() {
        firstUser = userRepository.save(User.builder().name("firstUser").email("firstUser@ya.ru").build());
        itemRepository.save(Item.builder().name("firstName").description("Description").available(true).owner(firstUser).build());
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
    }

    @Test
    void getItemsTest() {
        assertEquals(itemRepository.findAll().size(), 1);
    }

    @Test
    void findAllByOwnerIdTest() {
        itemRepository.save(Item.builder().name("name").description("description").available(true).owner(firstUser).build());
        List<Item> items = itemRepository.findAllByOwnerId(firstUser.getId());
        assertEquals(2, items.size());
    }

    @Test
    void findAllByRequestIdTest() {
        User secondUser = userRepository.save(User.builder().name("secondUser").email("secondUser@ya.ru").build());
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequest.builder().description("RequestDescription")
                .requestor(secondUser).created(LocalDateTime.now()).build());
        itemRepository.save(Item.builder().name("ItemName").description("ItemDescription").available(true)
                .owner(firstUser).request(itemRequest).build());
        assertThat(itemRepository.findAllByRequestId(itemRequest.getId()).size(), equalTo(1));
    }
}