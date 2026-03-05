@echo off
chcp 65001 >nul
echo ========================================
echo 推送项目到GitHub
echo ========================================
echo.

echo 正在添加所有文件...
git add -f gradlew
git add -f gradlew.bat
git add -f gradle/wrapper/gradle-wrapper.jar
git add -f gradle/wrapper/gradle-wrapper.properties
git add .github/workflows/build-apk.yml
git add .gitattributes
git add app/
git add build.gradle
git add settings.gradle
git add gradle.properties

echo.
echo 正在提交...
git commit -m "完整的Android项目文件"

echo.
echo 正在推送到GitHub...
git push

echo.
echo ========================================
echo 推送完成！
echo ========================================
echo.
echo 现在可以：
echo 1. 进入GitHub仓库
echo 2. 点击 Actions 标签
echo 3. 查看自动构建进度
echo 4. 构建完成后下载APK
echo.

pause

