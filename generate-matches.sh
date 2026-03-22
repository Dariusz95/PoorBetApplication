ODDS_TRAINING_DIR="./odds-training"
DATA_DIR="./data"
FILE="$DATA_DIR/matches.csv"
COMMONS_DIR="./poorbet-commons"

mkdir -p "$DATA_DIR"

if [ ! -f "$FILE" ]; then
    touch "$FILE"
fi

cd "$COMMONS_DIR" || exit
mvn clean install -DskipTests

cd ../odds-training || exit
mvn clean package -DskipTests

JAR_FILE=$(ls target/*.jar | grep -v "original")

java -jar "$JAR_FILE" --spring.profiles.active=dev