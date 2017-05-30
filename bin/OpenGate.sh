#!/bin/sh
if [ "$#" -gt 0 ];
then
	case "$1" in
		debug)
			java -jar -Dorg.slf4j.simpleLogger.logFile=/home/root/OpenGate/logs/OpenGate.log -Dorg.slf4j.simpleLogger.showDateTime=true -Dorg.slf4j.simpleLogger.levelInBrackets=true -Dorg.slf4j.simpleLogger.dateTimeFormat="[yyyy/MM/dd HH:mm:ss,SSS Z]" -Dorg.slf4j.simpleLogger.defaultLogLevel=debug /home/root/OpenGate/odmdevices-digi-ccimx6ul*.jar /home/root/OpenGate &
			;;
		*)
			echo "Use: $0 [debug]" >&2
			exit 3
			;;
	esac
else
	java -jar -Dorg.slf4j.simpleLogger.logFile=/home/root/OpenGate/logs/OpenGate.log -Dorg.slf4j.simpleLogger.showDateTime=true -Dorg.slf4j.simpleLogger.levelInBrackets=true -Dorg.slf4j.simpleLogger.dateTimeFormat="[yyyy/MM/dd HH:mm:ss,SSS Z]" /home/root/OpenGate/odmdevices-digi-ccimx6ul*.jar /home/root/OpenGate &
fi
exit 0
