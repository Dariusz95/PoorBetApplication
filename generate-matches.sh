ODDS_TRAINING_DIR="./odds-training"
DATA_DIR="./data"
FILE="$DATA_DIR/matches.csv"

mkdir -p "$DATA_DIR"

if [ ! -f "$FILE" ]; then
    touch "$FILE"
    echo "Utworzono plik $FILE"
fi

# mvn clean package -DskipTests

# JAR_FILE=$(ls target/*.jar | grep -v "original")
# echo "Uruchamianie $JAR_FILE..."
# java -jar "$JAR_FILE" --spring.profiles.active=dev

cd "$ODDS_TRAINING_DIR" || exit
mvn clean package -DskipTests

JAR_FILE=$(ls target/*.jar | grep -v "original")
echo "Uruchamianie $JAR_FILE..."

java -jar "$JAR_FILE" --spring.profiles.active=dev