package com.shop._3a.service;

import com.shop._3a.model.User;
import com.shop._3a.store.DataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        DataStore.getInstance().reset();
        userService = new UserService();
    }

    @Test
    void registerShouldGrantBonusAndAllowLogin() {
        User user = userService.register("tester", "123456", "测试玩家");

        assertTrue(user.getId() > 0);
        assertEquals(PointService.BONUS_REGISTER, user.getPoints());
        assertNotNull(userService.login("tester", "123456"));
    }

    @Test
    void loginShouldFailWithWrongPasswordOrUnknownUser() {
        userService.register("tester", "123456", "测试玩家");

        assertNull(userService.login("tester", "wrong"));
        assertNull(userService.login("nobody", "123456"));
        assertNull(userService.login("tester", ""));
    }

    @Test
    void registerShouldRejectDuplicateOrInvalidInput() {
        userService.register("tester", "123456", "测试玩家");

        assertThrows(IllegalArgumentException.class, () -> userService.register("tester", "123456", "重复"));
        assertThrows(IllegalArgumentException.class, () -> userService.register("ab", "123456", "太短"));
        assertThrows(IllegalArgumentException.class, () -> userService.register("中文名", "123456", "非法字符"));
        assertThrows(IllegalArgumentException.class, () -> userService.register("tester2", "123", "密码太短"));
    }
}
