FROM node:22.12.0-alpine AS build

WORKDIR /app

COPY package*.json ./

RUN npm ci

COPY . .

RUN npm run build


FROM nginx:1.27-alpine


RUN addgroup -S appgroup \
    && adduser -S -u 1001 -G appgroup appuser


COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=build \
    /app/dist/poor-bet-ui/browser \
    /usr/share/nginx/html


RUN chown -R appuser:appgroup \
    /usr/share/nginx/html \
    /var/cache/nginx \
    /var/log/nginx \
    /etc/nginx \
    /run


USER appuser


EXPOSE 8080


CMD ["nginx", "-g", "daemon off;"]