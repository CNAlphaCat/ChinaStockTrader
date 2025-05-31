FROM node:23-alpine3.19 AS build

WORKDIR /app

COPY package*.json ./

RUN npm ci

COPY . .

RUN rm -rf build && npm run build

FROM nginx:1.27-alpine-slim

COPY --from=build /app/build /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]