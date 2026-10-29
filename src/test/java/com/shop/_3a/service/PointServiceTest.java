package com.shop._3a.service;

import com.shop._3a.model.User;
import com.shop._3a.store.DataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PointServiceTest {

    private PointService pointService;
    private UserService userService;
    private User user;

    @BeforeEach
    void setUp() {
        DataStore.getInstance().reset();
        pointService = new PointService();
        userService = new UserService();
        user = userService.register("tester", "123456", "测试玩家");
    }

    @Test
    void signInShouldBeAllowedOncePerDay() {
        int before = user.getPoints();

        assertTrue(pointService.signIn(user.getId()));
        assertEquals(before + PointService.BONUS_SIGN_IN, userService.findById(user.getId()).getPoints());
        assertTrue(pointService.hasSignedInToday(user.getId()));

        assertFalse(pointService.signIn(user.getId()));
        assertEquals(before + PointService.BONUS_SIGN_IN, userService.findById(user.getId()).getPoints());
    }

    @Test
    void signInShouldFailForUnknownUser() {
        assertFalse(pointService.signIn(9999L));
    }

    @Test
    void changeShouldRecordAndNeverGoNegative() {
        pointService.change(user.getId(), 50, "测试加分");
        pointService.change(user.getId(), -1000, "测试扣分");

        User current = userService.findById(user.getId());
        assertEquals(0, current.getPoints());
        assertEquals(3, pointService.recordsOf(user.getId()).size());
    }

    @Test
    void levelTitleShouldMapByPoints() {
        assertEquals("新手上路", pointService.levelTitle(0));
        assertEquals("正式会员", pointService.levelTitle(200));
        assertEquals("进阶玩家", pointService.levelTitle(600));
        assertEquals("资深玩家", pointService.levelTitle(1500));
        assertEquals("硬核大佬", pointService.levelTitle(3000));
        assertEquals("传奇玩家", pointService.levelTitle(5000));
        assertEquals(100, pointService.nextLevelGap(100));
        assertEquals(400, pointService.nextLevelGap(200));
        assertEquals(0, pointService.nextLevelGap(6000));
    }
}
