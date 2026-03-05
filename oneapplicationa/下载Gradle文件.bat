@echo off
chcp 65001 >nul
echo ========================================
echo 下载 Gradle Wrapper JAR 文件
echo ========================================
echo.

set "WRAPPER_DIR=gradle\wrapper"
set "WRAPPER_JAR=%WRAPPER_DIR%\gradle-wrapper.jar"

if not exist "%WRAPPER_DIR%" (
    echo 创建目录: %WRAPPER_DIR%
    mkdir "%WRAPPER_DIR%"
)

echo 正在下载 gradle-wrapper.jar ...
echo 这可能需要几分钟，请耐心等待...
echo.

powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://github.com/gradle/gradle/raw/v8.0.0/gradle/wrapper/gradle-wrapper.jar' -OutFile '%WRAPPER_JAR%'}"

if exist "%WRAPPER_JAR%" (
    echo.
    echo [成功] gradle-wrapper.jar 下载完成！
    echo.
    echo 现在可以运行 生成APK.bat 了
    echo.
) else (
    echo.
    echo [失败] 下载失败！
    echo.
    echo 可能的原因：
    echo 1. 网络连接问题
    echo 2. 防火墙阻止
    echo 3. GitHub访问受限
    echo.
    echo 建议：
    echo 1. 使用Android Studio打开项目（最简单）
    echo 2. 手动下载文件：
    echo    https://github.com/gradle/gradle/raw/master/gradle/wrapper/gradle-wrapper.jar
    echo    然后放到：gradle\wrapper\gradle-wrapper.jar
    echo.
)

pause

