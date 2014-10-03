#!/bin/bash
curl -H "Content-Type: application/json" -d '{"username":"test","password":"123"}' http://localhost:1234/login
curl --request POST --data-binary "@test.pdf" http://localhost:1234/upload
