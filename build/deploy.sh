#!/bin/sh

export PATH=$HOME/bin:$HOME/.local/bin:$PATH
export JAVA_HOME=$HOME/.local/opt/java
export SCALA_HOME=$HOME/.local/opt/scala

APPLICATION_NAME='idl'

echo 'Stopping application...'
killall -w -u $APPLICATION_NAME -q java

if [ -d "$HOME/$APPLICATION_NAME" ]; then
	rm -rf $HOME/$APPLICATION_NAME
fi

echo 'Unpacking application...'
unzip -d $HOME/$APPLICATION_NAME nadams_$APPLICATION_NAME/$APPLICATION_NAME.zip
PACKAGED_FOLDER=`ls $HOME/$APPLICATION_NAME`
mv $HOME/$APPLICATION_NAME/$PACKAGED_FOLDER/* $HOME/$APPLICATION_NAME
rm -rf $HOME/$APPLICATION_NAME/$PACKAGED_FOLDER

echo 'Updating SBT packages...'
cd $HOME/nadams_$APPLICATION_NAME
sbt update

cd $HOME

echo 'Starting application...'
$HOME/start-$APPLICATION_NAME.sh &
