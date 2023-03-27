package ru.practicum.shareit.item.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class CommentRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @Test
    void findAllByItemIdTest() {
        User firstUser = userRepository.save(User.builder().name("firstUser").email("firstUser@ya.ru").build());
        User secondUser = userRepository.save(User.builder().name("secondUser").email("secondUser@ya.ru").build());
        Item item = itemRepository.save(Item.builder().name("firstName").description("Description").available(true).owner(firstUser).build());
        commentRepository.save(Comment.builder().text("Text").item(item).author(secondUser).created(LocalDateTime.now()).build());

        assertThat(commentRepository.findAllByItemId(item.getId()).size(), equalTo(1));
    }
}