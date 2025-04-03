# OpenRSSI
开源空间三点定位（含高度，非伪3d）系统。严格来说更适合UWB？  
使用了拟动量法计算误差。

## 典型应用

### 动作捕捉
不同于SlimeVR，本方案的追踪结果是0累计误差的。你大可以趴在房间里面睡觉，几个小时后位置并不会漂移导致你的形象扭成麻花。  
我们推荐使用BU01或者BU03的UWB模块；每个模块的参考报价是￥75...考虑到电池和盒子，可能100？注意一下，你需要在地上丢3个beacon。  
BU03的误差是10cm（或者说±5cm）。参考内建的仿真代码，动捕的最终精度是半径为6cm的球；可以轻松松突破100fps。  

### RSSI定位
RSSI转半径后最大的问题也是误差处理，感觉目前的文献都没什么人提这个...啧...  
具体RSSI转距离可以自己推算；值得一提的是**不推荐**通过只有beacon有RSSI功能，节点是蓝牙/Zigbee标签的方法来做。  
如你所见，节点之间的位置关系也很重要。

### 科研用途
不需要我说了吧？（x

## 可改进点

### 提升精度
仅需悬空地安装一个信标。我祝你校准时不会自闭（不是）  

### 简化安装
仅需先计算beacon位置即可。我在Num3d中为你留下了不少实用的函数，例如Rodrigues' rotation formula的实现 ：）  

### 减少计算
唉，嵌入式狂魔。好吧，我也说一下：  
仅需在减少lr的if中将1.9改成2.1即可。

## 一些注释

### 代码结构
PCloudAnswer: 点云重建问题，包含了mock数据生成器。  
PCloudQuestion： 点云重建结果，包含了点云压缩表示数据结构。  
Num3d： 3d库。包含了诸多我没用到，但是我觉得你用的到的函数。  
PhiSolver： 求解器。  
Main： 测试程序。  

### 有疑问？
请开启issue，看到了会回...但是不保证及时。  

## 公式与引用
为了方便论文人在自己的论文中引用，我还是写一下（唉我真是个大善人）  
LaTeX格式的，抄吧（  

Citation：  
AlvinChou @ WHU, Licensed under MIT.  

目标节点初始位置通过加权基站位置计算：

$$
t_k^{(0)} = \left( \frac{\sum_{j=1}^n w_j s_j^x}{\sum_{j=1}^n w_j}, 
                 \frac{1}{n}\sum_{j=1}^n D_{kj},
                 \frac{\sum_{j=1}^n w_j s_j^z}{\sum_{j=1}^n w_j} \right)
$$

其中权重 $w_j = \frac{1}{D_{kj}^2}$

对于每对节点 $(i,j)$，计算运动向量：

$$
\Delta_{ij} = (t_i - t_j) \cdot \frac{D_{ij} - \|t_i - t_j\|}{\|t_i - t_j\|^{0.1}}
$$

总运动向量为所有配对运动向量的累加：

$$
\Delta_i = \sum_{j \neq i} \Delta_{ij}
$$

目标节点位置更新公式：

$$
t_k^{(new)} = t_k^{(old)} + \eta \cdot \Delta_k
$$

学习率根据运动向量方向变化自动调整：

$$
\eta^{(new)} = \begin{cases}
0.9 \cdot \eta^{(old)} & \text{if } \sum_{i=1}^{n+m} \mathbb{I}(\Delta_i^{(new)} \cdot \Delta_i^{(old)} < 0) > \frac{n+m}{1.9} \\
1.5 \cdot \eta^{(old)} & \text{otherwise}
\end{cases}
$$
