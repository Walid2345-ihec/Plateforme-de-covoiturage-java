@echo off
REM ═══════════════════════════════════════════════════════════════════════════
REM  Plateforme de Covoiturage - Build and Run Script
REM  Automates compilation and execution of the Java application
REM ═══════════════════════════════════════════════════════════════════════════

title Covoiturage Application
color 0A

echo.
echo  ╔═══════════════════════════════════════════════════════════════╗
echo  ║         🚗 PLATEFORME DE COVOITURAGE 🚗                       ║
echo  ╠═══════════════════════════════════════════════════════════════╣
echo  ║  Compilation et execution automatisees                        ║
echo  ╚═══════════════════════════════════════════════════════════════╝
echo.

REM Set the project directory (where this batch file is located)
cd /d "%~dp0"

echo [1/4] Nettoyage des fichiers compiles precedents...
if exist "out" (
    rmdir /s /q "out" 2>nul
    echo       ✓ Dossier 'out' supprime
) else (
    echo       ✓ Aucun fichier a nettoyer
)

echo.
echo [2/4] Creation du dossier de sortie...
mkdir out 2>nul
echo       ✓ Dossier 'out' cree

echo.
echo [3/4] Compilation des fichiers Java...
echo.

javac -encoding UTF-8 -d out ^
    src/Models/*.java ^
    src/Services/*.java ^
    src/GUI/*.java ^
    src/App/*.java 2>&1

if %ERRORLEVEL% neq 0 (
    color 0C
    echo.
    echo  ╔═══════════════════════════════════════════════════════════════╗
    echo  ║  ❌ ERREUR DE COMPILATION                                     ║
    echo  ╚═══════════════════════════════════════════════════════════════╝
    echo.
    echo Verifiez les erreurs ci-dessus et corrigez le code source.
    echo.
    pause
    exit /b 1
)

echo       ✓ Compilation reussie!

echo.
echo [4/4] Lancement de l'application...
echo.
echo  ═══════════════════════════════════════════════════════════════════
echo.

java -cp out App.AppGUI

if %ERRORLEVEL% neq 0 (
    color 0C
    echo.
    echo  ╔═══════════════════════════════════════════════════════════════╗
    echo  ║  ❌ ERREUR D'EXECUTION                                        ║
    echo  ╚═══════════════════════════════════════════════════════════════╝
    echo.
    pause
    exit /b 1
)

echo.
echo  ╔═══════════════════════════════════════════════════════════════╗
echo  ║  ✓ Application terminee avec succes                          ║
echo  ╚═══════════════════════════════════════════════════════════════╝
echo.

pause
