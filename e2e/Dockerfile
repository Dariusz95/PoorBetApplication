FROM mcr.microsoft.com/playwright:v1.61.1-noble

ARG E2E_UID=1000
ARG E2E_GID=1000

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

RUN chown -R ${E2E_UID}:${E2E_GID} /app

USER ${E2E_UID}:${E2E_GID}

CMD ["npx", "playwright", "test"]
