package com.shop._3a.service;

import com.shop._3a.model.Comment;
import com.shop._3a.model.Post;
import com.shop._3a.model.User;
import com.shop._3a.store.DataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommentServiceTest {

    private CommentService commentService;
    private PostService postService;
    private UserService userService;
    private User author;
    private Post post;

    @BeforeEach
    void setUp() {
        DataStore.getInstance().reset();
        commentService = new CommentService();
        postService = new PostService();
        userService = new UserService();
        author = userService.register("author", "123456", "作者");
        post = postService.create(author, "艾尔登法环 Boss 打法", "女武神站位与翻滚时机说明", "攻略");
    }

    @Test
    void createShouldGrantBonusAndListByPost() {
        int before = author.getPoints();
        Comment comment = commentService.create(author, post.getId(), "这个打法很实用，谢谢分享");

        assertTrue(comment.getId() > 0);
        assertEquals(1, commentService.countOf(post.getId()));
        assertEquals(before + PointService.BONUS_COMMENT, userService.findById(author.getId()).getPoints());
        assertEquals("这个打法很实用，谢谢分享", commentService.listOf(post.getId()).get(0).getContent());
    }

    @Test
    void createShouldValidateInput() {
        assertThrows(IllegalArgumentException.class, () -> commentService.create(author, post.getId(), "短"));
        assertThrows(IllegalArgumentException.class, () -> commentService.create(author, 9999L, "合法的评论内容"));
        assertThrows(IllegalArgumentException.class, () -> commentService.create(null, post.getId(), "合法的评论内容"));
    }

    @Test
    void deleteShouldRequireOwnershipOrAdmin() {
        User other = userService.register("other", "123456", "路人");
        User admin = DataStore.getInstance().allUsers().stream()
                .filter(User::isAdmin).findFirst().orElseThrow();
        Comment comment = commentService.create(author, post.getId(), "这个打法很实用，谢谢分享");

        assertFalse(commentService.delete(post.getId(), comment.getId(), other));
        assertTrue(commentService.delete(post.getId(), comment.getId(), author));

        Comment second = commentService.create(author, post.getId(), "第二条评论用于测试删除");
        assertTrue(commentService.delete(post.getId(), second.getId(), admin));
        assertEquals(0, commentService.countOf(post.getId()));
    }
}
