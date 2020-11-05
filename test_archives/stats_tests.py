from scipy.stats import mannwhitneyu
import json

def mannWhitney(arr1, arr2):
    #print(mannwhitneyu(arr1, arr2))
    stat, p = mannwhitneyu(arr1, arr2)
    return p

mannWhitney([1,2,3,4,5], [6,7,8,9,10])

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

baselineData = getTracksData(tracks, "graphData.txt")
generalisationData = getTracksData(tracks, "genGraphData.txt")

baseline_P_dict = {}
generalisation_P_dict = {}

for i in range(len(baselineData)):
    trackData = baselineData[i]
    f_h_pvalue = mannWhitney(trackData[0], trackData[1])
    f_n_pvalue = mannWhitney(trackData[0], trackData[2])
    h_n_pvalue = mannWhitney(trackData[1], trackData[2])
    baseline_P_dict[tracks[i]] = {
        "Objective - Hybrid": f_h_pvalue,
        "Objective - Novelty": f_n_pvalue,
        "Hybrid - Novelty": h_n_pvalue
    }

for i in range(len(generalisationData)):
    trackData = generalisationData[i]
    f_h_pvalue = mannWhitney(trackData[0], trackData[1])
    f_n_pvalue = mannWhitney(trackData[0], trackData[2])
    h_n_pvalue = mannWhitney(trackData[1], trackData[2])
    generalisation_P_dict[tracks[i]] = {
        "Objective - Hybrid": f_h_pvalue,
        "Objective - Novelty": f_n_pvalue,
        "Hybrid - Novelty": h_n_pvalue
    }


f = open(base_folder+"/baseline_pvalues.txt", "w")
f.write(json.dumps(baseline_P_dict))
f.close()

f = open(base_folder+"/generalisation_pvalues.txt", "w")
f.write(json.dumps(generalisation_P_dict))
f.close()

