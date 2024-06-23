#!/bin/bash

# Function to close all terminal sessions
close_all_terminals() {
    echo "Closing all terminal sessions..."

    # Array of common terminal emulator process names
    terminal_emulators=("gnome-terminal" "xterm" "konsole" "terminator" "xfce4-terminal" "lxterminal" "mate-terminal" "tilix" "alacritty")

    # Loop through each terminal emulator and kill all its instances
    for terminal in "${terminal_emulators[@]}"; do
        # Get the process IDs of the terminal emulator
        pids=$(pgrep $terminal)

        # If there are any processes found, kill them
        if [ ! -z "$pids" ]; then
            echo "Closing all instances of $terminal..."
            kill $pids
        fi
    done
    echo "All terminal sessions have been closed."
}

# Function to kill process running on a specific port
kill_port() {
    PORT=$1
    PID=$(lsof -t -i:$PORT)
    if [ -n "$PID" ]; then
        echo "Killing process $PID on port $PORT"
        kill -9 $PID
    else
        echo "No process found on port $PORT"
    fi
}

# Close all terminal sessions
close_all_terminals

# Kill processes on specified ports
kill_port 8080
kill_port 8081
kill_port 8082
kill_port 8083
kill_port 8084
kill_port 9090

# Start Firebase emulators
echo "Starting Firebase emulators..."
# shellcheck disable=SC2164
gnome-terminal -- bash -c "cd ~/dapp2024/dsgt-final-project/broker && firebase emulators:start --project demo-distributed-systems-kul; exec bash"

# Open a new terminal and run Maven spring-boot:run for SOAPWine submodule
gnome-terminal -- bash -c "mvn -f ~/dapp2024/dsgt-final-project/dsgt-soap-wine/consumingwebservice/pom.xml spring-boot:run; exec bash"

# Open a new terminal and run Maven spring-boot:run for Broker submodule
gnome-terminal -- bash -c "mvn -f ~/dapp2024/dsgt-final-project/broker/pom.xml spring-boot:run; exec bash"

echo "Script execution completed."
