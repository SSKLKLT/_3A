package com.shop._3a.service;

import com.shop._3a.model.Post;
import com.shop._3a.model.PostCard;
import com.shop._3a.model.User;
import com.shop._3a.store.DataStore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * 帖子服务：发帖、检索、详情、删除。
 */
public class PostService {

    private final DataStore store;
    private final PointService pointService;

    public PostService() {
        this(DataStore.getInstance(), new PointService());
    }

    public PostService(DataStore store, PointService pointService) {
        this.store = store;
        this.pointService = pointService;
    }

    /**
     * 发帖，成功后发放积分。
     *
     * @throws IllegalArgumentException 标题或内容不合法
     */
    public Post create(User author, String title, String content, String category) {
        if (author == null) {
            throw new IllegalArgumentException("请先登录");
        }
        String t = title == null ? "" : title.trim();
        String c = content == null ? "" : content.trim();
        if (t.length() < 4 || t.length() > 60) {
            throw new IllegalArgumentException("标题长度需为 4-60 字");
        }
        if (c.length() < 5 || c.length() > 5000) {
            throw new IllegalArgumentException("正文长度需为 5-5000 字");
        }
        String cat = normalizeCategory(category);
        Post post = store.createPost(author, t, c, cat);
        pointService.grantPostBonus(author.getId());
        return post;
    }

    public String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "讨论";
        }
        for (String item : Post.CATEGORIES) {
            if (item.equals(category.trim())) {
                return item;
            }
        }
        return "讨论";
    }

    /** 关键词检索 + 板块筛选 + 排序 */
    public List<Post> list(String keyword, String category, String sort) {
        String kw = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        String cat = category == null || category.isBlank() ? null : category.trim();
        List<Post> result = new ArrayList<>();
        for (Post post : store.allPosts()) {
            if (cat != null && !cat.equals(post.getCategory())) {
                continue;
            }
            if (!kw.isEmpty()) {
                String hay = (post.getTitle() + " " + post.getContent() + " " + post.getAuthorName())
                        .toLowerCase(Locale.ROOT);
                if (!hay.contains(kw)) {
                    continue;
                }
            }
            result.add(post);
        }
        Comparator<Post> byTime = Comparator.comparing(Post::getCreatedAt).reversed();
        if ("hot".equals(sort)) {
            Comparator<Post> byHeat = Comparator
                    .comparingDouble((Post p) -> store.countCommentsOf(p.getId()) + p.getViewCount() * 0.5)
                    .reversed();
            result.sort(byHeat.thenComparing(byTime));
        } else {
            Comparator<Post> byPinned = Comparator.comparing(Post::isPinned).reversed();
            result.sort(byPinned.thenComparing(byTime));
        }
        return result;
    }

    /**
     * 列表页视图：附带评论数与热门标记。
     * 热门判定：评论数 * 2 + 浏览量 >= 10。
     */
    public List<PostCard> cards(String keyword, String category, String sort) {
        List<PostCard> cards = new ArrayList<>();
        for (Post post : list(keyword, category, sort)) {
            int comments = store.countCommentsOf(post.getId());
            PostCard card = new PostCard();
            card.setPost(post);
            card.setCommentCount(comments);
            card.setHot(comments * 2 + post.getViewCount() >= 10);
            User author = store.findUserById(post.getAuthorId());
            if (author != null && author.getAvatar() != null && !author.getAvatar().isBlank()) {
                card.setAuthorAvatar(author.getAvatar());
            }
            cards.add(card);
        }
        return cards;
    }

    public Post find(long id) {
        return store.findPost(id);
    }

    /** 阅读详情，浏览量 +1 */
    public Post view(long id) {
        Post post = store.findPost(id);
        if (post == null) {
            return null;
        }
        post.setViewCount(post.getViewCount() + 1);
        store.savePost(post);
        return post;
    }

    /**
     * 删除帖子，仅作者本人或管理员可操作。
     */
    public boolean delete(long postId, User operator) {
        Post post = store.findPost(postId);
        if (post == null || operator == null) {
            return false;
        }
        if (post.getAuthorId() != operator.getId() && !operator.isAdmin()) {
            return false;
        }
        return store.deletePost(postId);
    }

    public int count() {
        return store.countPosts();
    }
}
