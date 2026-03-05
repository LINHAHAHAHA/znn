@echo off
chcp 65001 >nul
echo ========================================
echo 公安接处警平台 - APK生成工具
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

echo 正在清理项目...
call gradlew.bat clean
if %errorlevel% neq 0 (
    echo [错误] 清理失败
    pause
    exit /b 1
)

echo.
echo 正在编译APK（首次编译需要下载依赖，可能需要10-15分钟）...
echo 请保持网络连接...
echo.

call gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo.
    echo [错误] 编译失败！
    echo.
    echo 可能的原因：
    echo 1. 网络问题，无法下载依赖
    echo 2. JDK版本不对（需要JDK 17）
    echo 3. 磁盘空间不足
    echo.
    echo 建议：使用Android Studio打开项目更简单！
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
echo 方法1：将APK文件复制到手机，点击安装
echo 方法2：手机连接电脑，执行命令：
echo        adb install app\build\outputs\apk\debug\app-debug.apk
echo.

explorer app\build\outputs\apk\debug

pause

