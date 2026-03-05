@echo off
echo ================================
echo 公安接处警平台 - 构建脚本
echo ================================
echo.

echo [1/3] 清理项目...
call gradlew.bat clean

echo.
echo [2/3] 构建调试版APK...
call gradlew.bat assembleDebug

echo.
echo [3/3] 完成！
echo.
echo APK文件位置：
echo app\build\outputs\apk\debug\app-debug.apk
echo.

echo 安装到手机：
echo adb install app\build\outputs\apk\debug\app-debug.apk
echo.

pause

