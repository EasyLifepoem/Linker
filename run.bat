@echo off
setlocal

:: 【設定區】請改成你自己電腦上的資料夾路徑！
set PROJECT_DIR=D:\JavaProjects\Linker\Linker
set OUTPUT_DIR=%PROJECT_DIR%\out\artifacts\Linker_jar
set JAR_NAME=Linker.jar
set MAIN_CLASS=com.example.linker.HelloApplication
set JAVA_FX_LIB=D:\JavaLibraries\javafx-sdk-19\lib
set DIST_DIR=%PROJECT_DIR%\dist

echo.
echo [1/5] 清理舊的打包資料...
if exist "%DIST_DIR%" rmdir /s /q "%DIST_DIR%"
mkdir "%DIST_DIR%"

echo.
echo [2/5] 打包 Fat JAR 中 (請確認 IntelliJ Artifact設定正確)...
cd /d "%PROJECT_DIR%"
call mvn clean package

echo.
echo [3/5] 複製 JAR 與 NoteList.yaml 到 dist 資料夾...
copy "%OUTPUT_DIR%\%JAR_NAME%" "%DIST_DIR%\%JAR_NAME%"
copy "%PROJECT_DIR%\NoteList.yaml" "%DIST_DIR%\NoteList.yaml"

echo.
echo [4/5] 用 jpackage 打包成 EXE...
cd /d "%DIST_DIR%"

jpackage ^
--input "%DIST_DIR%" ^
--name Linker ^
--main-jar "%JAR_NAME%" ^
--main-class %MAIN_CLASS% ^
--type exe ^
--module-path "%JAVA_FX_LIB%" ^
--add-modules javafx.controls,javafx.fxml ^
--win-console

echo.
echo [5/5] 完成！Linker.exe 產生成功！
pause
