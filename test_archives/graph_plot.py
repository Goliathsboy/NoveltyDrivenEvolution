import matplotlib.pyplot as plt
import numpy as np
import json

base_folder = "../TestArchives/Second_Tests"

methods = ["Fitness", "Hybrid", "Novelty"]
tracks = [["four-way-2-lane-no-pedestrian","four-way-2-lane-pedestrian"], ["four-way-no-pedestrian", "four-way-pedestrian"]] #"four-way-2-lane-pedestrian"
trackLabels = [["Dual Lane", "Dual Lane - Pedestrians"], ["Single Lane", "Single Lane - Pedestrians"]]
baseTitles = ["Baseline Performance of Dual Lane Tracks", "Baseline Performance of Single Lane Tracks"]

plt.rcParams.update({'font.size': 16})

def getMethodsData (tracks, file):
    methodsData = []
    for method in methods:
        methodData = []
        for track in tracks:
            fileName = base_folder+"/"+method+"/"+track+"/results/" + file
            with open(fileName) as json_file:
                data = json.load(json_file)
                methodData.append(data["collisions"])

        methodsData.append(methodData)

    return methodsData

def set_box_color(bp, color):
    #plt.setp(bp['boxes'], color=color)
    #plt.setp(bp['whiskers'], color=color)
    #plt.setp(bp['caps'], color=color)
    plt.setp(bp['medians'], color="red")
    for patch in bp["boxes"]:
        patch.set_facecolor(color)

#two_lane_data = getMethodsData(tracks[0])
#one_lane_data = getMethodsData(tracks[1])
#colours = ['pink', 'lightblue', 'lightgreen']
colours = ['#ff8080', '#66b3ff', "#33ff57"]

#Baseline Graphs
for i in range(2):
    methodsData = getMethodsData(tracks[i], "graphData.txt")

    plt.figure(figsize=(8, 8))

    bpl = plt.boxplot(methodsData[0], positions=np.array(range(len(methodsData[0])))*3-0.7, sym='', widths=0.5, patch_artist=True)
    bpm = plt.boxplot(methodsData[1], positions=np.array(range(len(methodsData[1])))*3, sym='', widths=0.5, patch_artist=True)
    bpr = plt.boxplot(methodsData[2], positions=np.array(range(len(methodsData[2])))*3+0.7, sym='', widths=0.5, patch_artist=True)

    set_box_color(bpl, colours[0])
    set_box_color(bpm, colours[1])
    set_box_color(bpr, colours[2])

    # draw temporary red and blue lines and use them to create a legend
    plt.plot([], linewidth=3, c=colours[0], label='Objective')
    plt.plot([], linewidth=3, c=colours[1], label='Hybrid')
    plt.plot([], linewidth=3, c=colours[2], label='Novelty')
    plt.legend()

    plt.xticks(range(0, len(trackLabels[i]) * 3, 3), trackLabels[i])
    plt.xlim(-2, len(trackLabels[i])*3 - 1)
    #plt.ylim(0, 8)
    #plt.tight_layout()
    plt.title(baseTitles[i])
    plt.xlabel('Training tracks')
    plt.ylabel('Average number of collisions')

    plt.savefig(base_folder+"/baseGraph"+ str(i+1) +".png")


tracks = [["four-way-2-lane-no-pedestrian", "four-way-no-pedestrian"], ["four-way-2-lane-pedestrian"], ["four-way-pedestrian"]]
trackLabels = [["Dual Lane", "Single Lane"], ["Dual Lane - Pedestrians"], ["Single Lane - Pedestrians"]]
genTitles = ["Triple Lane", "Dual Lane Intersection With Complex Pedestrians", "Single Lane Intersection With Complex Pedestrians"]

#Generalisation Graphs
for i in range(len(tracks)):
    methodsData = getMethodsData(tracks[i], "genGraphData.txt")

    plt.figure(figsize=(8, 8))

    bpl = plt.boxplot(methodsData[0], positions=np.array(range(len(methodsData[0])))*3-0.7, sym='', widths=0.5, patch_artist=True)
    bpm = plt.boxplot(methodsData[1], positions=np.array(range(len(methodsData[1])))*3, sym='', widths=0.5, patch_artist=True)
    bpr = plt.boxplot(methodsData[2], positions=np.array(range(len(methodsData[2])))*3+0.7, sym='', widths=0.5, patch_artist=True)

    set_box_color(bpl, colours[0])
    set_box_color(bpm, colours[1])
    set_box_color(bpr, colours[2])

    # draw temporary red and blue lines and use them to create a legend
    plt.plot([], linewidth=3, c=colours[0], label='Objective')
    plt.plot([], linewidth=3, c=colours[1], label='Hybrid')
    plt.plot([], linewidth=3, c=colours[2], label='Novelty')
    if i==2:
        plt.legend(loc="upper right")
    else:
        plt.legend()

    plt.xticks(range(0, len(trackLabels[i]) * 3, 3), trackLabels[i])
    plt.xlim(-2, len(trackLabels[i])*3 - 1)
    #plt.ylim(0, 8)
    #plt.tight_layout()
    plt.title(genTitles[i])
    plt.xlabel('Training tracks')
    plt.ylabel('Average number of collisions')

    plt.savefig(base_folder+"/genGraph"+ str(i+1) +".png")