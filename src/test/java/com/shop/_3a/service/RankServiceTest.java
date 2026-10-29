package com.shop._3a.service;

import com.shop._3a.model.Post;
import com.shop._3a.model.User;
import com.shop._3a.store.DataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RankServiceTest {

    private RankService rankService;
    private UserService userService;
    private PostService postService;

    @BeforeEach
    void setUp() {
        DataStore.getInstance().reset();
        rankService = new RankService();
        userService = new UserService();
        postService = new PostService();
    }

    @Test
    void pointsRankShouldSortByPointsDesc() {
        User low = userService.register("low", "123456", "低分");
        User high = userService.register("high", "123456", "高分");
        DataStore store = DataStore.getInstance();
        high.setPoints(5000);
        store.saveUser(high);
        low.setPoints(300);
        store.saveUser(low);

        List<RankService.RankItem> items = rankService.pointsRank(0);

        assertEquals(high.getId(), items.get(0).getUser().getId());
        assertEquals(1, items.get(0).getRank());
        assertEquals("传奇玩家", items.get(0).getLevelTitle());
        assertEquals(1, items.get(0).getRank());
        assertTrue(items.get(1).getScore() <= items.get(0).getScore());
    }

    @Test
    void activeRankShouldCountRecentPostsAndComments() {
        User author = userService.register("author", "123456", "作者");
        Post post = postService.create(author, "活跃度测试帖子标题", "正文内容足够长度用于通过校验", "讨论");

        List<RankService.RankItem> items = rankService.activeRank(10);
        boolean found = items.stream()
                .anyMatch(item -> item.getUser().getId() == author.getId() && item.getScore() >= 1);

        assertTrue(found);
        assertEquals(1, items.get(0).getRank());
        assertTrue(post.getId() > 0);
    }

    @Test
    void overviewShouldReturnCommunityStats() {
        User author = userService.register("author", "123456", "作者");
        postService.create(author, "统计测试帖子标题", "正文内容足够长度用于通过校验", "讨论");

        Map<String, Integer> overview = rankService.overview();

        assertEquals(5, overview.get("用户数"));
        assertEquals(5, overview.get("帖子数"));
        assertEquals(3, overview.get("评论数"));
    }
}
