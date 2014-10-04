#!/bin/bash
echo ">> Sending JSON"
curl -H "Content-Type: application/json" -d '{"username":"test","password":"123"}' http://localhost:1234/login
echo " (json)"
echo ">> Uploading file"
curl -i --request PUT --data-binary "@input.pdf" http://127.0.0.1:1234/upload
# curl -v -include --form file=input.pdf --form upload=@input.pdf http://127.0.0.1:1234/upload
echo ">> Downloading file"
wget http://127.0.0.1:1234/download -O download.pdf

echo "== "
echo "== Registering event"
echo "== "
curl -H "Content-Type: application/json" -d '{"name":"test event"}' http://localhost:1234/event/new
echo ""
echo "== "
echo "== Retrieving all events"
echo "== "
curl -i --request GET http://127.0.0.1:1234/events
echo ""
echo "== "
echo "== Retrieving event"
echo "== "
curl -i --request GET http://127.0.0.1:1234/event/1
echo ""
