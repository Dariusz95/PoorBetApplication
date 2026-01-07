import pandas as pd
from sklearn.linear_model import LogisticRegression
from sklearn.preprocessing import LabelEncoder
import joblib
from skl2onnx import convert_sklearn
from skl2onnx.common.data_types import FloatTensorType
import os

CSV_PATH = "/app/data/matches.csv"
MODEL_ONNX_PATH = "/app/data/models/match_predictor.onnx"
MODEL_PKL_PATH = "/app/data/models/match_predictor.pkl"
FEATURES = ["home_attack", "home_defence", "away_attack", "away_defence"]
TARGET = "result"  # H/D/A

df = pd.read_csv(CSV_PATH, delimiter=",")

le = LabelEncoder()
df[TARGET] = le.fit_transform(df[TARGET])  # H=0, D=1, A=2

X = df[FEATURES].values
y = df[TARGET].values

model = LogisticRegression(max_iter=500)

model.fit(X, y)

os.makedirs(os.path.dirname(MODEL_PKL_PATH), exist_ok=True)
joblib.dump(model, MODEL_PKL_PATH)

initial_type = [('float_input', FloatTensorType([None, len(FEATURES)]))]
onnx_model = convert_sklearn(model, initial_types=initial_type)

os.makedirs(os.path.dirname(MODEL_ONNX_PATH), exist_ok=True)
with open(MODEL_ONNX_PATH, "wb") as f:
    f.write(onnx_model.SerializeToString())

print(f"Model zapisany do ONNX: {MODEL_ONNX_PATH}")
print(f"Model zapisany do Pickle: {MODEL_PKL_PATH}")
