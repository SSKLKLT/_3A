package com.shop._3a.service;

import com.shop._3a.model.User;
import com.shop._3a.store.DataStore;
import com.shop._3a.util.SecurityUtil;

/**
 * 用户服务：注册、登录、资料查询。
 */
public class UserService {

    private final DataStore store;
    private final PointService pointService;

    public UserService() {
        this(DataStore.getInstance(), new PointService());
    }

    public UserService(DataStore store, PointService pointService) {
        this.store = store;
        this.pointService = pointService;
    }

    /**
     * 注册新用户。
     *
     * @throws IllegalArgumentException 参数不合法或用户名已存在
     */
    public User register(String username, String password, String nickname) {
        String name = trim(username);
        String pwd = password == null ? "" : password;
        validate(name, pwd);
        if (store.findUserByName(name) != null) {
            throw new IllegalArgumentException("用户名已被占用");
        }
        String nick = (nickname == null || nickname.isBlank()) ? name : nickname.trim();
        User user = store.register(name, pwd, nick, "🎮", User.ROLE_USER);
        pointService.grantRegisterBonus(user.getId());
        return user;
    }

    /**
     * 登录校验。
     *
     * @return 登录成功返回用户，失败返回 null
     */
    public User login(String username, String password) {
        String name = trim(username);
        if (name.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
        User user = store.findUserByName(name);
        if (user == null) {
            return null;
        }
        return SecurityUtil.matches(password, user.getSalt(), user.getPasswordHash()) ? user : null;
    }

    public User findById(long id) {
        return store.findUserById(id);
    }

    private void validate(String username, String password) {
        if (username.length() < 3 || username.length() > 16) {
            throw new IllegalArgumentException("用户名长度需为 3-16 位");
        }
        if (!username.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException("用户名只能包含字母、数字和下划线");
        }
        if (password.length() < 6 || password.length() > 32) {
            throw new IllegalArgumentException("密码长度需为 6-32 位");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
