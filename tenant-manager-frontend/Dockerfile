# build environment
FROM node:13.12.0-alpine as build
WORKDIR /app
ENV PATH /app/node_modules/.bin:$PATH
COPY package.json ./
COPY package-lock.json ./
COPY ./src/ /src
COPY ./public/ /public


# installing git cos https://github.com/mbrn/material-table/issues/2109
RUN apk add git
RUN npm ci

COPY . ./
EXPOSE 3000

CMD ["npm", "run", "start"]