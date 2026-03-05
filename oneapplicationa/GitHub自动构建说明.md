# GitHub Actions 自动构建APK

## 📦 已配置完成

项目已经配置好GitHub Actions，可以自动构建APK。

## 🚀 使用方法

### 方法1：推送代码自动构建

1. **将项目推送到GitHub**
   ```bash
   git init
   git add .
   git commit -m "初始提交：公安接处警平台"
   git branch -M main
   git remote add origin https://github.com/你的用户名/你的仓库名.git
   git push -u origin main
   ```

2. **自动触发构建**
   - 每次推送到main或master分支时自动构建
   - 构建完成后可以下载APK

### 方法2：手动触发构建

1. 进入GitHub仓库
2. 点击 **Actions** 标签
3. 选择 **构建Android APK** 工作流
4. 点击 **Run workflow** 按钮
5. 等待构建完成（约5-10分钟）

## 📥 下载APK

构建完成后：

1. 进入 **Actions** 标签
2. 点击最新的构建记录
3. 在页面底部找到 **Artifacts**
4. 下载 **公安接处警平台-debug.zip**
5. 解压得到 `app-debug.apk`

## 📋 配置文件说明

### `.github/workflows/build-apk.yml`
GitHub Actions工作流配置文件，定义了构建步骤。

### `.gitattributes`
确保gradlew文件在Linux环境下有正确的执行权限。

## ⚠️ 注意事项

### 1. 确保所有文件都已提交

特别是这些文件：
- ✅ `gradlew`
- ✅ `gradlew.bat`
- ✅ `gradle/wrapper/gradle-wrapper.jar`
- ✅ `gradle/wrapper/gradle-wrapper.properties`

### 2. 检查.gitignore

确保`.gitignore`没有忽略必要的文件。如果有，需要修改：

```gitignore
# 不要忽略这些文件
!gradle/wrapper/gradle-wrapper.jar
```

### 3. 首次构建时间较长

- 首次构建需要下载依赖（约5-10分钟）
- 后续构建会使用缓存，速度更快（约2-3分钟）

## 🔧 故障排查

### 问题1：找不到gradlew

**解决方法：**
```bash
# 确保gradlew已提交
git add gradlew
git add gradlew.bat
git add gradle/wrapper/
git commit -m "添加gradle wrapper文件"
git push
```

### 问题2：权限错误

**解决方法：**
已在`.gitattributes`中配置，确保该文件已提交：
```bash
git add .gitattributes
git commit -m "添加gitattributes配置"
git push
```

### 问题3：构建失败

查看构建日志：
1. 进入Actions标签
2. 点击失败的构建
3. 查看详细日志
4. 根据错误信息修复

## 📊 构建状态徽章

在README.md中添加构建状态徽章：

```markdown
![构建状态](https://github.com/你的用户名/你的仓库名/actions/workflows/build-apk.yml/badge.svg)
```

## 🎯 优势

使用GitHub Actions构建APK的优势：

- ✅ 无需本地环境配置
- ✅ 自动化构建
- ✅ 构建历史记录
- ✅ 多人协作方便
- ✅ 免费（公开仓库）
- ✅ 可以在任何设备上下载APK

## 📝 完整推送命令

```bash
# 1. 初始化Git仓库（如果还没有）
git init

# 2. 添加所有文件
git add .

# 3. 提交
git commit -m "初始提交：公安接处警平台Android应用"

# 4. 设置主分支
git branch -M main

# 5. 添加远程仓库（替换为你的仓库地址）
git remote add origin https://github.com/你的用户名/police-dispatch-platform.git

# 6. 推送到GitHub
git push -u origin main
```

推送完成后，GitHub Actions会自动开始构建！

## 🌐 查看构建进度

推送后：
1. 打开GitHub仓库页面
2. 点击 **Actions** 标签
3. 可以实时查看构建进度
4. 构建完成后下载APK

---

**提示**：如果是私有仓库，GitHub Actions有使用时长限制。公开仓库则完全免费。

