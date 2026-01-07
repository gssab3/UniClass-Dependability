@echo off
echo Inizio stress test Login (50 richieste)...

FOR /L %%i IN (1,1,50) DO (
   curl -s -o nul -X POST ^
   -d "email=test@test.it&password=passwordTest123" ^
   http://localhost:8080/UniClass-Dependability/Login
)

echo Test Login completato.
