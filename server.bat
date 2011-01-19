@echo off

if "%1%"=="start" (
  echo Starting TLB server
  call :start_server
) else if "%1%"=="stop" (
  echo Stopping TLB server
  call :stop_server
) else if "%1%"=="status" (
  call :status
) else (
  echo '%1%' is not a supported option. Options have to be one of 'start', 'stop' or 'status'
)
goto :EOF

:start_server
START "Test Load Balancer Server" /MIN server.cmd
goto :EOF

:status
FOR /F "tokens=2" %%I in ('TASKLIST /NH /FI "WINDOWTITLE eq Test Load Balancer Server - server.cmd"' ) DO echo TLB Server is running with process ID: %%I
goto :EOF

:stop_server
FOR /F "tokens=2" %%I in ('TASKLIST /NH /FI "WINDOWTITLE eq Test Load Balancer Server - server.cmd"' ) DO TASKKILL /F /T /PID %%I
goto :EOF