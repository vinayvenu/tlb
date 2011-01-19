@echo off

set pid_file=.server.pid

if "%1%"=="start" (
  call :start_server
  echo %PID% > %pid_file%
) else if "%1%"=="stop" (
  call :stop_server
) else if "%1%"=="status" (
  call :status
) else if "%1" == "cleanup" (
  if not exist %pid_file% (
    echo "No PID file (%pid_file%) found"
    exit /B 1
  ) else (
    call :delete_pid_file
  )
) else (
  echo '%1%' is not a supported option. Options have to be one of 'start', 'stop' or 'status'
)
goto :EOF

:start_server
  echo Starting TLB server
  if exist %pid_file% (
    echo PID file: %pid_file% already exists. It seems a TLB server is already running off this directory.
    set %pid_in_file%=type %pid_file%
    echo The process ID of this process, according to the PID file is: %pid_in_file%
    echo Please stop this process using 'server.bat stop' or cleanup using 'server.bat cleanup' to have this PID file removed
    exit /B 1
  )
  START "Test Load Balancer Server in %cd%" /MIN server.cmd
  call :populate_pid
  echo Server started. The PID is: %PID%
goto :EOF

:status
  if %PID% neq "NO" set not_running=true
  if %PID% neq "No" set not_running=true 
  if %not_running%=="true" (
    echo TLB Server is running with process ID: %PID%
  )
goto :EOF

:stop_server
  echo Stopping TLB server
  call :populate_pid
  if %PID% neq "NO" (
    TASKKILL /F /T /PID %PID%
  )
  call :delete_pid_file
  echo Stopped TLB server
goto :EOF

:populate_pid
  FOR /F "tokens=2" %%I in ('TASKLIST /NH /FI "WINDOWTITLE eq Test Load Balancer Server in %cd% - server.cmd"' ) DO set PID=%%I
goto :EOF

:delete_pid_file
  del /Q /F %pid_file%
goto :EOF