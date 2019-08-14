@echo off
title %username% @ Git Control - Version 1.0.1 [ Developed by Rellik Jaeger ]
echo.
echo  [!] This gitpull was designed for force merge command!
echo  [!] Please backup this hole directory before use this command!
echo  [!] To stop this command, press [Ctrl + C]!
echo.
pause
echo.
echo  [!] This is last warning! Please backup everything before next line!
echo.
pause
pause
pause
git pull origin master --allow-unrelated-histories
git merge origin origin/master
echo.
pause