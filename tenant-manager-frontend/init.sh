#!/usr/bin/env bash

pattern="main.*.js"

files=( $(compgen -W "$pattern") )
mainFile=$files

sed -i 's|http://localhost:8080|'"$REACT_APP_BACKEND_URI"'|g' "$mainFile"

nginx -g daemon off;