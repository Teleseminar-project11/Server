#!/bin/bash
echo ">> Sending JSON"
curl -H "Content-Type: application/json" -d '{"username":"test","password":"123"}' http://localhost:1234/login
echo " (json)"
echo ">> Uploading file"
curl -i --request PUT --data-binary "@input.pdf" http://127.0.0.1:1234/upload
echo ">> Downloading file"
wget http://127.0.0.1:1234/download -O download.pdf
