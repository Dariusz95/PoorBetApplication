import pandas as pd
from sklearn.linear_model import LogisticRegression
from skl2onnx import convert_sklearn
from skl2onnx.common.data_types import FloatTensorType
import joblib
import os

CSV_PATH = "/app/data/matches.csv"
MODEL_ONNX_PATH = "/app/data/models/match_predictor.onnx"
MODEL_PKL_PATH = "/app/data/models/match_predictor.pkl"

FEATURES = ["home_attack", "home_defence", "away_attack", "away_defence"]
TARGET = "result"  # H / X / A

df = pd.read_csv(CSV_PATH)

df[TARGET] = df[TARGET].map({
    "H": 0,
    "X": 1,
    "A": 2
})

X = df[FEATURES].astype("float32").values
y = df[TARGET].values

model = LogisticRegression(
    max_iter=1000,
    solver="lbfgs",
    multi_class="multinomial"
)

model.fit(X, y)

os.makedirs(os.path.dirname(MODEL_PKL_PATH), exist_ok=True)
joblib.dump(model, MODEL_PKL_PATH)

initial_type = [("float_input", FloatTensorType([None, len(FEATURES)]))]

onnx_model = convert_sklearn(
    model,
    initial_types=initial_type,
    options={id(model): {"zipmap": False}}
)

os.makedirs(os.path.dirname(MODEL_ONNX_PATH), exist_ok=True)
with open(MODEL_ONNX_PATH, "wb") as f:
    f.write(onnx_model.SerializeToString())

print("✅ Model trained")
print("ONNX:", MODEL_ONNX_PATH)
