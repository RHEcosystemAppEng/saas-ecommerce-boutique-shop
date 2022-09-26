# build environment
FROM node:13.12.0-alpine as build
WORKDIR /app
ENV PATH /app/node_modules/.bin:$PATH
COPY package.json ./
COPY package-lock.json ./

# installing git cos https://github.com/mbrn/material-table/issues/2109
RUN apk add git
RUN npm ci --silent

COPY . ./
RUN npm run build

# production environment
FROM nginxinc/nginx-unprivileged
COPY --from=build /app/build /usr/share/nginx/html
# new
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]