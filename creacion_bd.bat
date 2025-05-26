@echo off
echo ====================================================
echo  Preparando base de datos Checkinn en MySQL
echo ====================================================

REM — Pedirá la contraseña de root/admin
set /p ROOT_PASS=Introduce la contraseña de root (o admin) de MySQL:

echo.
echo Conectando a MySQL como root y creando recursos...
mysql -u root -p%ROOT_PASS% -e ^
"CREATE DATABASE IF NOT EXISTS checkinn CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; ^
 CREATE USER IF NOT EXISTS 'checkinn'@'localhost' IDENTIFIED BY 'checkinn'; ^
 GRANT ALL PRIVILEGES ON checkinn.* TO 'checkinn'@'localhost'; ^
 FLUSH PRIVILEGES;"

if errorlevel 1 (
  echo.
  echo [ERROR] No se pudo ejecutar el script. Comprueba la contraseña de root y que MySQL esté en el PATH.
  pause
  exit /b 1
)

echo.
echo [OK] Base de datos y usuario creados correctamente.
echo.
echo — Propiedades para tu application.properties —
echo spring.datasource.url=jdbc:mysql://localhost:3306/checkinn
echo spring.datasource.username=checkinn
echo spring.datasource.password=checkinn
echo ====================================================
pause