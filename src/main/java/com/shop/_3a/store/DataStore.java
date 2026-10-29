package com.shop._3a.store;

import com.shop._3a.model.Comment;
import com.shop._3a.model.PointRecord;
import com.shop._3a.model.Post;
import com.shop._3a.model.User;
import com.shop._3a.util.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内存数据仓库。
 * <p>
 * 当前阶段不接数据库，用线程安全的内存结构承载全部业务数据，
 * 后续替换为 MyBatis/JPA 时，只需保持 Service 层接口不变。
 */
public final class DataStore {

    private static final DataStore INSTANCE = new DataStore();

    private final Map<Long, User> users = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Map<String, Long> usernameIndex = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Map<Long, Post> posts = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Map<Long, List<Comment>> comments = Collections.synchronizedMap(new LinkedHashMap<>());
    private final List<PointRecord> pointRecords = Collections.synchronizedList(new ArrayList<>());

    private final AtomicLong userSeq = new AtomicLong();
    private final AtomicLong postSeq = new AtomicLong();
    private final AtomicLong commentSeq = new AtomicLong();
    private final AtomicLong recordSeq = new AtomicLong();

    private boolean seeded;

    private DataStore() {
    }

    public static DataStore getInstance() {
        return INSTANCE;
    }

    /** 初始化演示数据，可重复调用（幂等） */
    public synchronized void seedIfEmpty() {
        if (seeded && !users.isEmpty()) {
            return;
        }
        seeded = true;

        User admin = register("admin", "admin123", "社区管理员", "🛡️", User.ROLE_ADMIN);
        User zelda = register("zelda_fan", "123456", "塞尔达老猎人", "🗡️", User.ROLE_USER);
        User wuya = register("wuya", "123456", "乌鸦哥", "🐦", User.ROLE_USER);
        User nova = register("nova", "123456", "新星玩家", "✨", User.ROLE_USER);

        admin.setPoints(3200);
        zelda.setPoints(1860);
        wuya.setPoints(1240);
        nova.setPoints(760);

        createPost(zelda, "《艾尔登法环》DLC 全 BOSS 打法速通思路",
                "1. 优先提升庇护等级；2. 双持大剑配合战灰；3. 召唤骨灰拉扯仇恨。", "攻略");
        createPost(wuya, "通关 30 小时后，聊聊《博德之门 3》的叙事设计",
                "分支任务的因果链做得很扎实，但第四章节奏略拖。", "评测");
        createPost(nova, "新手入坑 3A，先玩哪一款比较合适？",
                "预算有限，想先挑一两部耐玩的，大家有什么推荐。", "讨论");
        createPost(admin, "社区版规与积分说明（必读）",
                "友善交流，禁止引战；发帖 +10 积分，评论 +5 积分，每日签到 +20 积分。", "闲聊");

        List<Post> all = new ArrayList<>(posts.values());
        addComment(nova.getId(), all.get(0).getId(), "受教了，庇护等级真的关键。");
        addComment(wuya.getId(), all.get(0).getId(), "补充：带火焰伤害的武器对付腐败系更快。");
        addComment(zelda.getId(), all.get(2).getId(), "推荐先玩《荒野大镖客 2》，节奏慢但沉浸感强。");
    }

    /** 仅测试使用：清空并重新灌入演示数据 */
    public synchronized void reset() {
        users.clear();
        usernameIndex.clear();
        posts.clear();
        comments.clear();
        pointRecords.clear();
        userSeq.set(0);
        postSeq.set(0);
        commentSeq.set(0);
        recordSeq.set(0);
        seeded = false;
        seedIfEmpty();
    }

    // ---------------- User ----------------

    public synchronized User register(String username, String password, String nickname, String avatar, String role) {
        String salt = SecurityUtil.salt();
        User user = new User();
        user.setId(userSeq.incrementAndGet());
        user.setUsername(username);
        user.setSalt(salt);
        user.setPasswordHash(SecurityUtil.hashPassword(password, salt));
        user.setNickname(nickname);
        user.setAvatar(avatar == null || avatar.isEmpty() ? "🎮" : avatar);
        user.setRole(role == null ? User.ROLE_USER : role);
        user.setCreatedAt(LocalDateTime.now());
        users.put(user.getId(), user);
        usernameIndex.put(username.toLowerCase(), user.getId());
        return user;
    }

    public User findUserById(long id) {
        return users.get(id);
    }

    public User findUserByName(String username) {
        if (username == null) {
            return null;
        }
        Long id = usernameIndex.get(username.toLowerCase());
        return id == null ? null : users.get(id);
    }

    public List<User> allUsers() {
        return new ArrayList<>(users.values());
    }

    public synchronized void saveUser(User user) {
        users.put(user.getId(), user);
    }

    // ---------------- Post ----------------

    public synchronized Post createPost(User author, String title, String content, String category) {
        Post post = new Post();
        post.setId(postSeq.incrementAndGet());
        post.setAuthorId(author.getId());
        post.setAuthorName(author.getNickname());
        post.setTitle(title);
        post.setContent(content);
        post.setCategory(category);
        post.setCreatedAt(LocalDateTime.now());
        posts.put(post.getId(), post);
        comments.put(post.getId(), Collections.synchronizedList(new ArrayList<>()));
        return post;
    }

    public Post findPost(long id) {
        return posts.get(id);
    }

    public List<Post> allPosts() {
        return new ArrayList<>(posts.values());
    }

    public synchronized void savePost(Post post) {
        posts.put(post.getId(), post);
    }

    public synchronized boolean deletePost(long id) {
        comments.remove(id);
        return posts.remove(id) != null;
    }

    // ---------------- Comment ----------------

    public synchronized Comment addComment(long authorId, long postId, String content) {
        User author = users.get(authorId);
        Post post = posts.get(postId);
        if (author == null || post == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setId(commentSeq.incrementAndGet());
        comment.setPostId(postId);
        comment.setAuthorId(authorId);
        comment.setAuthorName(author.getNickname());
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comments.computeIfAbsent(postId, k -> Collections.synchronizedList(new ArrayList<>())).add(comment);
        return comment;
    }

    public List<Comment> commentsOf(long postId) {
        List<Comment> list = comments.get(postId);
        return list == null ? new ArrayList<>() : new ArrayList<>(list);
    }

    public synchronized boolean deleteComment(long postId, long commentId) {
        List<Comment> list = comments.get(postId);
        if (list == null) {
            return false;
        }
        return list.removeIf(c -> c.getId() == commentId);
    }

    public int countCommentsOf(long postId) {
        List<Comment> list = comments.get(postId);
        return list == null ? 0 : list.size();
    }

    public List<Comment> allComments() {
        List<Comment> all = new ArrayList<>();
        for (List<Comment> list : new ArrayList<>(comments.values())) {
            all.addAll(new ArrayList<>(list));
        }
        return all;
    }

    // ---------------- PointRecord ----------------

    public synchronized PointRecord addPointRecord(long userId, int delta, String reason) {
        PointRecord record = new PointRecord();
        record.setId(recordSeq.incrementAndGet());
        record.setUserId(userId);
        record.setDelta(delta);
        record.setReason(reason);
        record.setCreatedAt(LocalDateTime.now());
        pointRecords.add(record);
        return record;
    }

    public List<PointRecord> pointRecordsOf(long userId) {
        List<PointRecord> result = new ArrayList<>();
        for (PointRecord record : new ArrayList<>(pointRecords)) {
            if (record.getUserId() == userId) {
                result.add(record);
            }
        }
        result.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return result;
    }

    // ---------------- 统计 ----------------

    public int countPosts() {
        return posts.size();
    }

    public int countUsers() {
        return users.size();
    }

    public int countComments() {
        int total = 0;
        for (List<Comment> list : new ArrayList<>(comments.values())) {
            total += list.size();
        }
        return total;
    }

    public LocalDate today() {
        return LocalDate.now();
    }
}
