@echo off
title %username% @ Git Control - Version 1.0.1 [ Developed by Rellik Jaeger ]
echo.
set /p url=[!] Enter your username/reponame: 
set /p msg=[!] Enter your commit message: 
echo.
echo -------------------------------------------------------------------------
echo.
git init
echo.
echo -------------------------------------------------------------------------
echo.
git add -A
git commit -m "%msg%"
echo.
echo -------------------------------------------------------------------------
echo.
git remote add origin https://github.com/%url%
echo.
echo -------------------------------------------------------------------------
echo.
git push -u origin master
echo.
echo -------------------------------------------------------------------------
echo.
git status
echo.
echo -------------------------------------------------------------------------
echo.
pause