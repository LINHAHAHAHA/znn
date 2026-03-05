# 📱 直接生成APK安装到手机

## 🎯 最简单的方法（3步搞定）

### 步骤1：双击运行脚本

在项目目录找到并双击：**`生成APK.bat`**

### 步骤2：等待编译

- 首次运行需要10-15分钟（下载依赖）
- 后续编译只需1-2分钟
- 保持网络连接

### 步骤3：安装到手机

编译完成后会自动打开文件夹，找到：
```
app-debug.apk
```

**安装方式：**

**方式A - 直接安装（推荐）**
1. 将 `app-debug.apk` 复制到手机（通过QQ、微信、数据线等）
2. 在手机上找到APK文件
3. 点击安装
4. 如果提示"禁止安装未知应用"，点击设置允许

**方式B - 使用ADB安装**
1. 手机连接电脑，开启USB调试
2. 在项目目录打开命令行
3. 执行：`adb install app\build\outputs\apk\debug\app-debug.apk`

---

## ⚠️ 常见问题

### Q1: 双击脚本后提示"未找到Java"？
**解决方法：**
1. 下载安装JDK 17：https://www.oracle.com/java/technologies/downloads/#java17
2. 或者使用Android Studio（自带JDK）

### Q2: 编译失败，提示网络错误？
**解决方法：**
1. 检查网络连接
2. 使用Android Studio（有更好的网络配置）
3. 配置国内镜像（见下方）

### Q3: 手机无法安装APK？
**解决方法：**
1. 小米手机：设置 → 应用设置 → 应用管理 → 右上角三个点 → 允许安装未知应用
2. 选择你用来安装的应用（如文件管理器）→ 允许

### Q4: 没有Android Studio，命令行也失败？
**最简单的解决方法：**
1. 下载安装Android Studio：https://developer.android.google.cn/studio
2. 打开项目，等待自动同步
3. Build → Build Bundle(s) / APK(s) → Build APK(s)
4. 完成！

---

## 🔧 配置国内镜像（可选，加速下载）

如果下载依赖很慢，编辑 `build.gradle`：

```gradle
allprojects {
    repositories {
        maven { url 'https://maven.aliyun.com/repository/google' }
        maven { url 'https://maven.aliyun.com/repository/public' }
        google()
        mavenCentral()
    }
}
```

---

## ✅ 推荐方案对比

| 方案 | 难度 | 速度 | 推荐度 |
|------|------|------|--------|
| 双击脚本生成APK | ⭐⭐ | 中等 | ⭐⭐⭐⭐ |
| Android Studio | ⭐ | 快 | ⭐⭐⭐⭐⭐ |
| 命令行 | ⭐⭐⭐ | 中等 | ⭐⭐⭐ |

**结论：如果你有Android Studio，直接用它最方便！**

---

## 📞 需要帮助？

如果遇到问题：
1. 查看 `部署指南.md` 获取详细说明
2. 查看 `README.md` 了解项目功能
3. 确保JDK版本为17
4. 确保网络连接正常

