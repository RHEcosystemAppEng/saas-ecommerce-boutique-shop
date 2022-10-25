# build environment
FROM node:13.12.0-alpine as build
WORKDIR /app
ENV PATH /app/node_modules/.bin:$PATH
COPY package.json ./
COPY .env ./
COPY package-lock.json ./
COPY ./src/ /src
COPY ./public/ /public


# installing git cos https://github.com/mbrn/material-table/issues/2109
RUN apk add git
RUN npm ci

COPY . ./
RUN npm run build

# production environment
FROM nginxinc/nginx-unprivileged
COPY --from=build /app/build /usr/share/nginx/html
COPY --chown=185 init.sh /usr/share
# new
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 8080
USER 185

ENTRYPOINT ["/usr/share/init.sh"]