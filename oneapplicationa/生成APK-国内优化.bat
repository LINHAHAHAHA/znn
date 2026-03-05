@echo off
chcp 65001 >nul
echo ========================================
echo 公安接处警平台 - APK生成工具（国内优化版）
echo ========================================
echo.

echo 正在检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到Java，请先安装JDK 17
    echo 下载地址: https://www.oracle.com/java/technologies/downloads/#java17
    pause
    exit /b 1
)

echo [✓] Java环境正常
echo.

echo ========================================
echo 重要提示：
echo 1. 首次编译需要下载约500MB的依赖文件
echo 2. 已配置国内镜像源
echo 3. 预计需要10-20分钟，请耐心等待
echo 4. 请确保网络连接正常
echo ========================================
echo.
pause

echo.
echo [1/2] 正在清理项目...
call gradlew.bat clean
if %errorlevel% neq 0 (
    echo.
    echo [错误] 清理失败！
    echo.
    echo 可能的原因：
    echo 1. 网络连接问题（即使用了国内镜像）
    echo 2. 防火墙或代理设置
    echo 3. 磁盘空间不足
    echo.
    echo 解决方案：
    echo 1. 检查网络连接
    echo 2. 关闭VPN或代理
    echo 3. 使用Android Studio
    echo.
    pause
    exit /b 1
)

echo.
echo [2/2] 正在编译APK...
echo 这一步会下载大量依赖，请耐心等待...
echo.

call gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo.
    echo [错误] 编译失败！
    echo.
    echo 建议：使用Android Studio打开项目
    echo 1. 下载：https://developer.android.google.cn/studio
    echo 2. 打开项目
    echo 3. Build → Build APK
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo [成功] APK编译完成！
echo ========================================
echo.
echo APK文件位置：
echo %cd%\app\build\outputs\apk\debug\app-debug.apk
echo.
echo 安装方法：
echo 1. 将APK文件复制到手机
echo 2. 在手机上点击安装
echo 3. 允许安装未知应用
echo.

if exist "app\build\outputs\apk\debug" (
    explorer app\build\outputs\apk\debug
)

pause

