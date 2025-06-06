 # h5 model available as a version V.0.1
 # ANDROID APP READY AND AVAILABLE -> APPLYING OUR MODEL TO A REAL USE CASE  ->DOWNLOAD IT USING VERSION V.0.1


# AI & Real Image Detector (DeepFake Classifier)

A web-based **image classification tool** built with **TensorFlow** and **Streamlit** to detect whether an image is **REAL** or **AI-generated (DeepFake)**. This application uses a pre-trained Convolutional Neural Network (CNN) model to analyze and classify images with high accuracy.


## 🚀 Features

- **Upload and Analyze Images**: Upload an image and instantly get predictions.
- **Fast & Accurate**: Detects AI-generated images (DeepFakes) with high confidence.
- **Web Interface**: Easy-to-use interface built with Streamlit.
- **AI-Powered**: Built on a TensorFlow model for accurate results.

---

## 🛠️ Tech Stack

- **TensorFlow**: Deep learning library for model training and inference.
- **Streamlit**: Web framework for creating data apps.
- **NumPy**: For numerical computing in Python.
- **Pillow**: For image processing and manipulation.

---

## 🧠 Model Information

- **Model File**: `AI_IMAGE_DETECTOR_full_model(DEEP).h5`
- **Input Size**: 32x32 pixels (RGB)
- **Output**: The model outputs a probability distribution for the two classes: 
  - `REAL` (Class 1)
  - `FAKE` (Class 0)

---

## 📂 Project Structure

```
.
├── app.py                  # Streamlit application for image upload and prediction
├── requirements.txt        # Python dependencies
├── README.md               # Project documentation
├── .gitignore              # Ignore unnecessary files
└── model/
    └── AI_IMAGE_DETECTOR_full_model(DEEP).h5  # Pre-trained model (if available)
```

---
