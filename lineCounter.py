import os

lineAcc = 0

for file in os.listdir("ISU"):
    f = open("ISU\\" + file)
    lineAcc += len(f.read().split("\n"))
    f.close()

print(lineAcc)