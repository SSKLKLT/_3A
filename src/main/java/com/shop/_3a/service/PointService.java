package com.shop._3a.service;

import com.shop._3a.model.PointRecord;
import com.shop._3a.model.User;
import com.shop._3a.store.DataStore;

import java.time.LocalDate;
import java.util.List;

/**
 * 积分服务：负责积分发放、等级计算、签到与流水查询。
 */
public class PointService {

    public static final int BONUS_REGISTER = 100;
    public static final int BONUS_POST = 10;
    public static final int BONUS_COMMENT = 5;
    public static final int BONUS_SIGN_IN = 20;

    private final DataStore store;

    public PointService() {
        this(DataStore.getInstance());
    }

    public PointService(DataStore store) {
        this.store = store;
    }

    /** 积分增减，并写入流水 */
    public synchronized int change(long userId, int delta, String reason) {
        User user = store.findUserById(userId);
        if (user == null) {
            return 0;
        }
        user.setPoints(user.getPoints() + delta);
        store.saveUser(user);
        store.addPointRecord(userId, delta, reason);
        return user.getPoints();
    }

    /** 注册奖励 */
    public void grantRegisterBonus(long userId) {
        change(userId, BONUS_REGISTER, "新人注册奖励");
    }

    /** 发帖奖励 */
    public void grantPostBonus(long userId) {
        change(userId, BONUS_POST, "发布帖子");
    }

    /** 评论奖励 */
    public void grantCommentBonus(long userId) {
        change(userId, BONUS_COMMENT, "发表评论");
    }

    /**
     * 每日签到，每天仅一次。
     *
     * @return true 表示签到成功并发放积分
     */
    public boolean signIn(long userId) {
        User user = store.findUserById(userId);
        if (user == null) {
            return false;
        }
        LocalDate today = store.today();
        if (today.equals(user.getLastSignInDate())) {
            return false;
        }
        user.setLastSignInDate(today);
        store.saveUser(user);
        change(userId, BONUS_SIGN_IN, "每日签到");
        return true;
    }

    public boolean hasSignedInToday(long userId) {
        User user = store.findUserById(userId);
        return user != null && LocalDate.now().equals(user.getLastSignInDate());
    }

    /** 根据积分计算等级头衔 */
    public String levelTitle(int points) {
        if (points >= 5000) {
            return "传奇玩家";
        }
        if (points >= 3000) {
            return "硬核大佬";
        }
        if (points >= 1500) {
            return "资深玩家";
        }
        if (points >= 600) {
            return "进阶玩家";
        }
        if (points >= 200) {
            return "正式会员";
        }
        return "新手上路";
    }

    /** 距离下一等级还差多少积分 */
    public int nextLevelGap(int points) {
        if (points >= 5000) {
            return 0;
        }
        if (points >= 3000) {
            return 5000 - points;
        }
        if (points >= 1500) {
            return 3000 - points;
        }
        if (points >= 600) {
            return 1500 - points;
        }
        if (points >= 200) {
            return 600 - points;
        }
        return 200 - points;
    }

    public List<PointRecord> recordsOf(long userId) {
        return store.pointRecordsOf(userId);
    }
}
