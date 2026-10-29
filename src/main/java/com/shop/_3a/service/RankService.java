package com.shop._3a.service;

import com.shop._3a.model.Comment;
import com.shop._3a.model.Post;
import com.shop._3a.model.User;
import com.shop._3a.store.DataStore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 排行服务：积分榜与活跃榜。
 */
public class RankService {

    private final DataStore store;
    private final PointService pointService;

    public RankService() {
        this(DataStore.getInstance(), new PointService());
    }

    public RankService(DataStore store, PointService pointService) {
        this.store = store;
        this.pointService = pointService;
    }

    /**
     * 积分榜：按积分降序，积分相同则按加入时间升序。
     */
    public List<RankItem> pointsRank(int limit) {
        List<User> users = new ArrayList<>(store.allUsers());
        users.sort(Comparator
                .comparingInt(User::getPoints).reversed()
                .thenComparing(User::getCreatedAt));
        List<RankItem> items = new ArrayList<>();
        int rank = 0;
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (i == 0 || user.getPoints() != users.get(i - 1).getPoints()) {
                rank = i + 1;
            }
            items.add(new RankItem(rank, user, pointService.levelTitle(user.getPoints()), user.getPoints()));
            if (limit > 0 && items.size() >= limit) {
                break;
            }
        }
        return items;
    }

    /**
     * 活跃榜：统计最近 7 天发帖 + 评论数。
     */
    public List<RankItem> activeRank(int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        Map<Long, Integer> score = new LinkedHashMap<>();
        for (Post post : store.allPosts()) {
            if (post.getCreatedAt() != null && post.getCreatedAt().isAfter(since)) {
                score.merge(post.getAuthorId(), 1, Integer::sum);
            }
        }
        for (Comment comment : store.allComments()) {
            if (comment.getCreatedAt() != null && comment.getCreatedAt().isAfter(since)) {
                score.merge(comment.getAuthorId(), 1, Integer::sum);
            }
        }
        List<RankItem> items = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : score.entrySet()) {
            User user = store.findUserById(entry.getKey());
            if (user == null) {
                continue;
            }
            items.add(new RankItem(0, user, pointService.levelTitle(user.getPoints()), entry.getValue()));
        }
        Comparator<RankItem> byActivity = Comparator.comparingInt(RankItem::getScore).reversed();
        Comparator<RankItem> byPoints = Comparator
                .comparingInt((RankItem item) -> item.getUser().getPoints()).reversed();
        items.sort(byActivity.thenComparing(byPoints));
        int rank = 0;
        for (int i = 0; i < items.size(); i++) {
            if (i == 0 || items.get(i).getScore() != items.get(i - 1).getScore()) {
                rank = i + 1;
            }
            items.get(i).setRank(rank);
        }
        if (limit > 0 && items.size() > limit) {
            return new ArrayList<>(items.subList(0, limit));
        }
        return items;
    }

    /** 社区总览统计 */
    public Map<String, Integer> overview() {
        Map<String, Integer> data = new LinkedHashMap<>();
        data.put("用户数", store.countUsers());
        data.put("帖子数", store.countPosts());
        data.put("评论数", store.countComments());
        return data;
    }

    /** 榜单条目 */
    public static class RankItem {
        private int rank;
        private final User user;
        private final String levelTitle;
        private final int score;

        public RankItem(int rank, User user, String levelTitle, int score) {
            this.rank = rank;
            this.user = user;
            this.levelTitle = levelTitle;
            this.score = score;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public User getUser() {
            return user;
        }

        public String getLevelTitle() {
            return levelTitle;
        }

        public int getScore() {
            return score;
        }

        /** 榜单数值的展示文案 */
        public String getMetric() {
            return score + "";
        }
    }
}
