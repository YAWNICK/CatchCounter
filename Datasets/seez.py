import numpy as np
import matplotlib.pyplot as plt
from matplotlib.widgets import Slider
import re

with open("AccData3b4.txt") as f:
    lines = f.readlines()
lines = list(map(str.strip, lines))
xs, ys, zs = [], [], []
for line in lines:
    #dps = re.findall(r"\(-?\d+,-?\d+,-?\d+\)", line)
    #dps = list(map(lambda x: list(map(int, x[1:-1].split(','))), dps))
    dps = list(map(int, line.split(': ')[1][:-1].split(',')))
    for dp in dps:
        zs.append(dp)
    
length = len(zs)
print(length)

# plotting code snippet taken from 
# https://stackoverflow.com/questions/31001713/plotting-the-data-with-scrollable-x-time-horizontal-axis-on-linux

fig, ax = plt.subplots(figsize=(12, 5))
plt.subplots_adjust(bottom=0.25, left=0.1)

t = np.arange(0.0, length / 10, 0.1)
#l1, = plt.plot(t, xs, color='blue', label="xs")
#l2, = plt.plot(t, ys, color='green', label="ys")
l3, = plt.plot(t, zs, color='red', label="zs")
plt.legend(loc="lower left")
plt.xlabel("Time (sec)")
plt.ylabel("Acceleration (cm/s^2)")
plt.grid()
plt.axis([0, 5, -3000, 3000])

axcolor = 'lightgoldenrodyellow'
axpos = plt.axes([0.2, 0.1, 0.65, 0.03], facecolor=axcolor)

spos = Slider(axpos, 'Second', 0.1, float((length / 10) - 5))

def update(val):
    pos = spos.val
    ax.axis([pos,pos+5,-3000,3000])
    fig.canvas.draw_idle()

spos.on_changed(update)

plt.show()
