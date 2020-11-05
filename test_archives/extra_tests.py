from scipy.stats import mannwhitneyu
import json

def mannWhitney(arr1, arr2):
    #print(mannwhitneyu(arr1, arr2))
    stat, p = mannwhitneyu(arr1, arr2)
    return p

base_folder = "../TestArchives/Second_Tests"

methods = ["Fitness", "Hybrid", "Novelty"]
tracks = ["four-way-2-lane-no-pedestrian","four-way-2-lane-pedestrian", "four-way-no-pedestrian", "four-way-pedestrian"]

def getTracksData (tracks, file):
    tracksData = []
    for track in tracks:
        trackData = []
        for method in methods:
            fileName = base_folder+"/"+method+"/"+track+"/results/" + file
            with open(fileName) as json_file:
                data = json.load(json_file)
                trackData.append(data["collisions"])

        tracksData.append(trackData)

    return tracksData


pedestrians_dict = {
    "single_lane": {
        "SL - SLP": [],
        "SLP - SLP2": []
    },
    "dual_lane": {
        "DL - DLP": [],
        "DLP - DLP2": []
    }
}

tracks = ["four-way-no-pedestrian", "four-way-pedestrian"]

methodsData = []
for method in methods:
    methodData = []
    for track in tracks:
        fileName = base_folder+"/"+method+"/"+track+"/results/graphData.txt"
        with open(fileName) as json_file:
            data = json.load(json_file)
            methodData.append(data["collisions"])

    methodsData.append(methodData)

for i in range(len(methodsData)):
    methodData = methodsData[i]
    pvalue = mannWhitney(methodData[0], methodData[1])
    pedestrians_dict["single_lane"]["SL - SLP"].append(pvalue)

methodsData = []
for method in methods:
    methodData = []
    fileName = base_folder+"/"+method+"/four-way-pedestrian/results/graphData.txt"
    with open(fileName) as json_file:
        data = json.load(json_file)
        methodData.append(data["collisions"])
    fileName = base_folder+"/"+method+"/four-way-pedestrian/results/genGraphData.txt"
    with open(fileName) as json_file:
        data = json.load(json_file)
        methodData.append(data["collisions"])

    methodsData.append(methodData)

for i in range(len(methodsData)):
    methodData = methodsData[i]
    pvalue = mannWhitney(methodData[0], methodData[1])
    pedestrians_dict["single_lane"]["SLP - SLP2"].append(pvalue)


tracks = ["four-way-2-lane-no-pedestrian", "four-way-2-lane-pedestrian"]

methodsData = []
for method in methods:
    methodData = []
    for track in tracks:
        fileName = base_folder+"/"+method+"/"+track+"/results/graphData.txt"
        with open(fileName) as json_file:
            data = json.load(json_file)
            methodData.append(data["collisions"])

    methodsData.append(methodData)

for i in range(len(methodsData)):
    methodData = methodsData[i]
    pvalue = mannWhitney(methodData[0], methodData[1])
    pedestrians_dict["dual_lane"]["DL - DLP"].append(pvalue)

methodsData = []
for method in methods:
    methodData = []
    fileName = base_folder+"/"+method+"/four-way-2-lane-pedestrian/results/graphData.txt"
    with open(fileName) as json_file:
        data = json.load(json_file)
        methodData.append(data["collisions"])
    fileName = base_folder+"/"+method+"/four-way-2-lane-pedestrian/results/genGraphData.txt"
    with open(fileName) as json_file:
        data = json.load(json_file)
        methodData.append(data["collisions"])

    methodsData.append(methodData)

for i in range(len(methodsData)):
    methodData = methodsData[i]
    pvalue = mannWhitney(methodData[0], methodData[1])
    pedestrians_dict["dual_lane"]["DLP - DLP2"].append(pvalue)

f = open(base_folder+"/pedestrian_pvalues.txt", "w")
f.write(json.dumps(pedestrians_dict))
f.close()


## Triple lane test
triple_lane_dict = {
    "DL - SL": []
}

tracks = ["four-way-2-lane-no-pedestrian", "four-way-no-pedestrian"]

methodsData = []
for method in methods:
    methodData = []
    for track in tracks:
        fileName = base_folder+"/"+method+"/"+track+"/results/genGraphData.txt"
        with open(fileName) as json_file:
            data = json.load(json_file)
            methodData.append(data["collisions"])

    methodsData.append(methodData)

for i in range(len(methodsData)):
    methodData = methodsData[i]
    pvalue = mannWhitney(methodData[0], methodData[1])
    triple_lane_dict["DL - SL"].append(pvalue)

f = open(base_folder+"/triple_lane_pvalues.txt", "w")
f.write(json.dumps(triple_lane_dict))
f.close()


## Baseline track size comparison

track_size_dict = {
    "DL - SL": [],
    "DLP - SLP": []
}

tracks = ["four-way-2-lane-no-pedestrian", "four-way-no-pedestrian"]

methodsData = []
for method in methods:
    methodData = []
    for track in tracks:
        fileName = base_folder+"/"+method+"/"+track+"/results/graphData.txt"
        with open(fileName) as json_file:
            data = json.load(json_file)
            methodData.append(data["collisions"])

    methodsData.append(methodData)

for i in range(len(methodsData)):
    methodData = methodsData[i]
    pvalue = mannWhitney(methodData[0], methodData[1])
    track_size_dict["DL - SL"].append(pvalue)

tracks = ["four-way-2-lane-pedestrian", "four-way-pedestrian"]

methodsData = []
for method in methods:
    methodData = []
    for track in tracks:
        fileName = base_folder+"/"+method+"/"+track+"/results/graphData.txt"
        with open(fileName) as json_file:
            data = json.load(json_file)
            methodData.append(data["collisions"])

    methodsData.append(methodData)

for i in range(len(methodsData)):
    methodData = methodsData[i]
    pvalue = mannWhitney(methodData[0], methodData[1])
    track_size_dict["DLP - SLP"].append(pvalue)

f = open(base_folder+"/track_size_dict.txt", "w")
f.write(json.dumps(triple_lane_dict))
f.close()


