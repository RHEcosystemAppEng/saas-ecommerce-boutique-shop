# build environment
FROM node:18.13.0-alpine as build
WORKDIR /app
ENV PATH /app/node_modules/.bin:$PATH
COPY package.json ./
COPY package-lock.json ./
COPY ./src/ /src
COPY ./public/ /public

RUN npm config set cache /tmp --global

# installing git cos https://github.com/mbrn/material-table/issues/2109
RUN npm config set cache /tmp --global
RUN apk add git
RUN npm i

COPY . ./
EXPOSE 3000

CMD ["npm", "run", "start"]
