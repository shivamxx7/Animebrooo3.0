with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

content = content.strip() + "\n    }\n}\n"

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
