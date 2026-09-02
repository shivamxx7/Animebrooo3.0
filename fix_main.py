with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

if not content.strip().endswith("}"):
    content += "\n    }\n}\n"

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
