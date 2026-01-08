# Odds Service

Microservice do predykcji szans (prawdopodobieństwa wygranej/remisu/przegranej) na podstawie siły drużyn.

## Build

```bash
./mvnw clean package
```

## Run

```bash
./mvnw spring-boot:run
```

## API

### POST /api/odds/predict

Predykcja szans na mecz na podstawie statystyk ataku i obrony drużyn.

**Request:**

```json
{
  "homeTeamAttack": 75,
  "homeTeamDefense": 68,
  "awayTeamAttack": 72,
  "awayTeamDefense": 70
}
```

**Response:**

```json
{
  "winProbability": 0.55,
  "drawProbability": 0.25,
  "lossProbability": 0.2
}
```
