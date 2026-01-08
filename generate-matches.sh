ODDS_TRAINING_DIR="./odds-training"
DATA_DIR="./data"
FILE="$DATA_DIR/matches.csv"

mkdir -p "$DATA_DIR"

if [ ! -f "$FILE" ]; then
    touch "$FILE"
fi

cd "$ODDS_TRAINING_DIR" || exit
mvn clean package -DskipTests

JAR_FILE=$(ls target/*.jar | grep -v "original")

java -jar "$JAR_FILE" --spring.profiles.active=dev