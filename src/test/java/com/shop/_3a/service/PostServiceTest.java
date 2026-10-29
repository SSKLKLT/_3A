package com.shop._3a.service;

import com.shop._3a.model.Post;
import com.shop._3a.model.User;
import com.shop._3a.store.DataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostServiceTest {

    private PostService postService;
    private UserService userService;
    private User author;

    @BeforeEach
    void setUp() {
        DataStore.getInstance().reset();
        postService = new PostService();
        userService = new UserService();
        author = userService.register("author", "123456", "作者");
    }

    @Test
    void createShouldGrantBonus() {
        int before = author.getPoints();
        Post post = postService.create(author, "黑神话悟空隐藏关攻略", "这里是正文内容，长度足够。", "攻略");

        assertTrue(post.getId() > 0);
        assertEquals("攻略", post.getCategory());
        assertEquals(before + PointService.BONUS_POST, userService.findById(author.getId()).getPoints());
    }

    @Test
    void createShouldValidateInput() {
        assertThrows(IllegalArgumentException.class, () -> postService.create(author, "短", "内容足够长的正文", "攻略"));
        assertThrows(IllegalArgumentException.class, () -> postService.create(author, "合法标题四个字", "短", "攻略"));
        assertThrows(IllegalArgumentException.class, () -> postService.create(null, "合法标题四个字", "内容足够长的正文", "攻略"));
    }

    @Test
    void listShouldFilterByCategoryAndKeyword() {
        int baseline = postService.list("", null, "new").size();
        int guideBaseline = postService.list("", "攻略", "new").size();

        postService.create(author, "艾尔登法环 Boss 打法", "女武神站位与翻滚时机说明", "攻略");
        postService.create(author, "通关后的剧情讨论", "结局分支到底怎么选比较合理", "讨论");

        assertEquals(baseline + 2, postService.list("", null, "new").size());
        assertEquals(guideBaseline + 1, postService.list("", "攻略", "new").size());
        assertEquals(1, postService.list("女武神", null, "new").size());
        assertEquals(0, postService.list("完全不存在的关键词XYZ", null, "new").size());
    }

    @Test
    void deleteShouldRequireOwnershipOrAdmin() {
        Post post = postService.create(author, "艾尔登法环 Boss 打法", "女武神站位与翻滚时机说明", "攻略");
        User other = userService.register("other", "123456", "路人");
        User admin = DataStore.getInstance().allUsers().stream()
                .filter(User::isAdmin).findFirst().orElseThrow();

        assertFalse(postService.delete(post.getId(), other));
        assertTrue(postService.delete(post.getId(), author));
        assertFalse(postService.delete(9999L, admin));

        Post another = postService.create(author, "第二篇测试帖子", "正文内容足够长度用于通过校验", "闲聊");
        assertTrue(postService.delete(another.getId(), admin));
    }

    @Test
    void viewShouldIncreaseViewCount() {
        Post post = postService.create(author, "艾尔登法环 Boss 打法", "女武神站位与翻滚时机说明", "攻略");

        List<Post> before = postService.list("", null, "hot");
        postService.view(post.getId());
        postService.view(post.getId());

        assertEquals(2, postService.find(post.getId()).getViewCount());
        assertEquals(before.size(), postService.list("", null, "hot").size());
    }
}
