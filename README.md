# 3A 小游戏社区

一个用原生 **Java Servlet + JSP** 实现的轻量级小游戏社区，不依赖任何 Web 框架、不依赖数据库，拉下来就能跑：发帖、评论、签到攒积分、排行榜，外加接了大模型的「AI 攻略」和一只会说话的桌宠。

页面用 JSP + 手写 CSS 完成，配合 canvas 实现动态场景与桌宠交互，适合作为 Servlet 教学示例、课程设计骨架，或二次开发自己的小社区。

![桌宠](src/main/webapp/assets/pet.png)

## 目录

- [功能](#功能)
- [技术栈](#技术栈)
- [目录结构](#目录结构)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [默认账号](#默认账号)
- [积分规则](#积分规则)
- [常见问题](#常见问题)
- [运行测试](#运行测试)
- [说明](#说明)

## 功能

- **用户体系**：注册、登录、退出，基于 `HttpSession` 的会话保持，`AuthFilter` 拦截未登录访问
- **社区帖子**：发帖、查看详情、关键词搜索、板块筛选、最新/最热排序、分页（每页 8 条）
- **评论互动**：帖子下发表评论并展示
- **积分系统**：注册/发帖/评论/每日签到发放积分，每次变动都有流水记录
- **排行榜**：按积分生成榜单与站点概览
- **AI 攻略**：输入游戏名与关注点，由大模型生成结构化攻略（未配密钥时退化为占位数据）
- **桌宠闲聊**：右下角「大肥鱼娘」人设对话，走 `/api/pet-chat`，模型不可用时回落到内置台词
- **后台管理**：管理员查看数据概览、删除帖子与评论

## 技术栈

| 分类 | 选型 |
| --- | --- |
| 语言 | Java 17 |
| Web 容器 | Tomcat 9（Servlet 4.0 / `javax.servlet`） |
| 视图 | JSP + 手写 CSS + 原生 JS（canvas） |
| 构建 | Maven 3.9.x（项目自带 Wrapper，无需预装 Maven） |
| 打包 | WAR |
| 测试 | JUnit 5 |
| 大模型 | 兼容 OpenAI 协议的接口（默认 SiliconFlow，模型 `deepseek-ai/DeepSeek-V4-Flash`） |
| 存储 | 进程内内存数据结构（`DataStore`），**重启即清空** |

## 目录结构

```
_3A
├── src/main/java/com/shop/_3a
│   ├── model/      实体：User、Post、Comment、PointRecord、PostCard
│   ├── store/      DataStore：线程安全的内存仓库 + 演示数据初始化
│   ├── service/    业务层：用户、帖子、评论、积分、排行、AI 攻略、LLM 客户端
│   ├── util/       工具：JSON、时间格式化、密码加盐哈希
│   └── web/        Servlet 与 Filter（含 EncodingFilter 统一 UTF-8）
├── src/main/resources/
│   └── ai-config.properties    本地大模型密钥（已 gitignore，不要提交）
├── src/main/webapp
│   ├── index.jsp               首页
│   ├── assets/                 CSS、JS、图片
│   └── WEB-INF/views/          JSP 视图（posts、post_detail、rank、me、admin…）
├── src/test/java/              Service 与工具类单元测试
├── pom.xml
└── mvnw / mvnw.cmd
```

## 环境要求

| 依赖 | 版本 | 说明 |
| --- | --- | --- |
| JDK | **17**（≥17 即可） | `pom.xml` 里 `maven.compiler.source/target` 指定为 17 |
| Tomcat | **9.0.x** | 必须是 9.x，见[常见问题](#常见问题)第 1 条 |
| Maven | 3.9.x | 用项目自带 `mvnw` 可跳过安装 |
| 网络 | 首次构建需联网 | 需访问 `repo.maven.apache.org` 下载依赖 |

检查本机环境：

```powershell
java -version        # 应显示 17.x
mvnw.cmd -v          # 或 ./mvnw -v
```

> IDEA 用户注意：本项目在 *Project Structure → Project* 里 SDK 选 17，*Language level* 选 17，否则编译报 `invalid target release`。

## 快速开始

### 方式一：命令行构建 + Tomcat 部署（最贴近真实部署）

**1. 打包**

```powershell
# Windows
mvnw.cmd clean package

# macOS / Linux
./mvnw clean package
```

成功后在 `target/` 下得到 `_3A-1.0-SNAPSHOT.war`。

**2. 部署**

把 WAR 复制到 Tomcat 的 `webapps/` 目录：

```powershell
copy target\_3A-1.0-SNAPSHOT.war "D:\apache-tomcat-9.0.x\webapps\"
```

然后启动 Tomcat：

```powershell
# Windows
D:\apache-tomcat-9.0.x\bin\startup.bat

# macOS / Linux
./bin/startup.sh
```

**3. 访问**

WAR 包名决定上下文路径，所以地址是：

```
http://localhost:8080/_3A-1.0-SNAPSHOT/
```

想用更短的 `/_3A/`，有两个办法，任选其一：

- 把 WAR 重命名为 `_3A.war` 再丢进 `webapps/`；
- 或部署后在 `conf/server.xml` 里给该 Context 指定 `path="/_3A"`。

**4. 停止**

```powershell
D:\apache-tomcat-9.0.x\bin\shutdown.bat
```

### 方式二：IDEA 里跑（改代码即时生效）

适合开发调试，改 JSP/Servlet 热更新更快。

1. *File → Project Structure → Project*：SDK 选 **17**，Language level 选 **17**
2. 右上角 *Add Configuration → + → Tomcat Server → Local*
3. **Server** 页：Application server 指向本机 Tomcat 9 安装目录
4. **Deployment** 页：点 `+` → *Artifact* → 选 **`_3A:war exploded`**
5. 把 *Application context* 设为 `/` 或 `/_3A`（决定访问路径）
6. 启动，访问 `http://localhost:8080/_3A/`（或 `/`，取决于第 5 步）

> 若 Deployment 里找不到 artifact，先在 *Project Structure → Artifacts* 里让 IDEA 生成 `_3A:war exploded`。

## 配置说明

项目**没有必须配置的项**，默认即可运行；唯一的可选配置是大模型密钥。

### 1. 大模型（AI 攻略 / 桌宠）— 可选

不配置也能跑：AI 攻略返回占位内容，桌宠使用内置台词，其它功能完全正常。

密钥按以下优先级生效（**后者覆盖前者**）：

| 优先级 | 位置 | 适用场景 |
| --- | --- | --- |
| 1（最低） | classpath 下 `src/main/resources/ai-config.properties` | 仅本机调试，已被 `.gitignore` 排除 |
| 2 | `${catalina.base}/conf/ai-config.properties` | 生产部署，密钥不进 WAR |
| 3 | `~/.3a-ai-config.properties` | 单机多项目共用 |
| 4（最高） | 环境变量 / JVM 系统属性 | 容器化、CI |

配置文件格式：

```properties
ai.base.url=https://api.siliconflow.cn/v1
ai.api.key=sk-xxxxxxxxxxxxxxxx
ai.model=deepseek-ai/DeepSeek-V4-Flash
ai.timeout.seconds=60
```

对应的环境变量名：

| 配置项 | 环境变量 | 默认值 |
| --- | --- | --- |
| `ai.base.url` | `AI_BASE_URL` | `https://api.siliconflow.cn/v1` |
| `ai.api.key` | `AI_API_KEY` | 空（即未启用） |
| `ai.model` | `AI_MODEL` | `deepseek-ai/DeepSeek-V4-Flash` |
| `ai.timeout.seconds` | `AI_TIMEOUT_SECONDS` | `60` |

设置环境变量后需**重启 Tomcat** 才生效：

```powershell
$env:AI_API_KEY = "sk-xxxxxxxx"      # 当前窗口有效
# 或在 IDEA 的 Run Configuration → Environment variables 里填
```

用任意兼容 OpenAI `/chat/completions` 协议的服务都可以，把 `ai.base.url` 和 `ai.model` 换成对应值即可。

### 2. 端口与上下文路径

| 项 | 在哪改 | 默认 |
| --- | --- | --- |
| HTTP 端口 | Tomcat `conf/server.xml` 的 `<Connector port="8080">` | `8080` |
| 上下文路径 | WAR 文件名 / IDEA 的 Application context | `/_3A-1.0-SNAPSHOT`（命令行）/ `/_3A`（IDEA） |
| 会话超时 | `src/main/webapp/WEB-INF/web.xml` 的 `<session-timeout>` | 60 分钟 |
| 编码 | `EncodingFilter` 统一设置 UTF-8 | UTF-8 |

### 3. 存储

当前是内存存储（`DataStore`），启动时由 `AppListener` 调用 `seedIfEmpty()` 灌入演示数据。**重启即重置**。

要接数据库时，保持 `service` 层方法签名不变，替换 `DataStore` 内部实现即可，Servlet 层无需改动。

### 4. 国内构建慢的处理（可选）

首次构建要下载 Maven 与依赖。若卡在下载，可在 `~/.m2/settings.xml` 配阿里云镜像：

```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

## 默认账号

首次启动自动灌入演示数据，可直接登录：

| 用户名 | 密码 | 昵称 | 角色 | 初始积分 |
| --- | --- | --- | --- | --- |
| `admin` | `admin123` | 社区管理员 | 管理员 | 3200 |
| `zelda_fan` | `123456` | 塞尔达老猎人 | 普通用户 | 1860 |
| `wuya` | `123456` | 乌鸦哥 | 普通用户 | 1240 |
| `nova` | `123456` | 新星玩家 | 普通用户 | 760 |

管理员登录后访问 `/admin` 可查看后台。

> 部署到公网前请先改掉这些默认密码——它们写在 `DataStore.seedIfEmpty()` 里，改完重新打包即可。

## 积分规则

| 行为 | 积分 | 常量位置 |
| --- | --- | --- |
| 注册 | +100 | `PointService.BONUS_REGISTER` |
| 发帖 | +10 | `PointService.BONUS_POST` |
| 评论 | +5 | `PointService.BONUS_COMMENT` |
| 每日签到 | +20 | `PointService.BONUS_SIGN_IN` |

## 常见问题

**1. 启动报 `java.lang.NoClassDefFoundError: javax/servlet/...` 或 `jakarta.servlet` 相关错误**

用了 Tomcat 10/11。Tomcat 10 起包名迁移到 `jakarta.servlet`，本项目基于 `javax.servlet`（Servlet 4.0），**必须用 Tomcat 9.0.x**。

**2. 访问首页 404**

大概率是上下文路径不对。以 Tomcat 的 `logs/catalina.*.log` 和 `webapps/` 里解压出的目录名为准；命令行部署通常是 `/_3A-1.0-SNAPSHOT/`，IDEA 里通常是 `/_3A/`。

**3. 中文显示成乱码**

- 浏览器：页面已由 `EncodingFilter` 设为 UTF-8，确认浏览器没强制别的编码
- IDEA 控制台：*Settings → Editor → File Encodings* 全部设为 UTF-8，并在 VM options 加 `-Dfile.encoding=UTF-8`
- Maven 编译：`pom.xml` 已设 `project.build.sourceEncoding=UTF-8`，无需改动

**4. AI 攻略没有真实内容 / 桌宠只回固定台词**

说明密钥没生效。检查顺序：环境变量 `AI_API_KEY` 是否设置 → 外部配置文件路径是否正确 → 改完是否重启了 Tomcat。密钥为空时程序按设计降级，不是报错。

**5. 端口 8080 被占用**

改 Tomcat `conf/server.xml` 里 Connector 的 `port`（如 8081），或先 `netstat -ano | findstr :8080` 找出占用进程。

**6. 重启后数据全没了**

设计如此，内存存储。需要持久化就替换 `DataStore`。

**7. `mvnw` 在 Git Bash / WSL 下没有执行权限**

```bash
chmod +x mvnw
```

## 运行测试

```powershell
mvnw.cmd test
```

测试覆盖 `UserService`、`PostService`、`CommentService`、`PointService`、`RankService`、`AiGuideService`、`JsonUtil`，每个用例通过 `DataStore.reset()` 隔离状态。

## 说明

- 数据在内存中，属演示性质；重启后用户、帖子、积分都会重置。
- 密码存储前做了加盐哈希（`SecurityUtil`），但仍不建议把本项目直接当作生产站点暴露。
- 仓库不包含任何密钥；`src/main/resources/ai-config.properties` 已在 `.gitignore` 中排除。

## 许可证

暂未指定开源许可证。如需他人复用，建议补充 `LICENSE` 文件（如 [MIT](https://choosealicense.com/licenses/mit/)）。