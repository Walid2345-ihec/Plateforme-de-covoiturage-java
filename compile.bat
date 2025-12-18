@echo off
REM ═══════════════════════════════════════════════════════════════════════════
REM  Plateforme de Covoiturage - Compile Only Script
REM  Compiles the application without running it
REM ═══════════════════════════════════════════════════════════════════════════

title Covoiturage - Compilation
color 0B

echo.
echo  ╔═══════════════════════════════════════════════════════════════╗
echo  ║         🔧 COMPILATION UNIQUEMENT 🔧                          ║
echo  ╚═══════════════════════════════════════════════════════════════╝
echo.

cd /d "%~dp0"

echo [1/3] Nettoyage...
if exist "out" rmdir /s /q "out" 2>nul
mkdir out 2>nul
echo       ✓ Pret

echo.
echo [2/3] Compilation en cours...

javac -encoding UTF-8 -Xlint:all -d out ^
    src/Models/*.java ^
    src/Services/*.java ^
    src/GUI/*.java ^
    src/App/*.java 2>&1

if %ERRORLEVEL% neq 0 (
    color 0C
    echo.
    echo  ❌ ECHEC DE LA COMPILATION
    echo.
    pause
    exit /b 1
)

echo.
echo [3/3] Resultat:
echo.
echo  ╔═══════════════════════════════════════════════════════════════╗
echo  ║  ✓ COMPILATION REUSSIE - Aucune erreur                       ║
echo  ╚═══════════════════════════════════════════════════════════════╝
echo.
echo  Pour lancer l'application, executez: run.bat
echo.

pause
