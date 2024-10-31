# WiFiMon

![WiFiMon Logo](https://raw.githubusercontent.com/WiFiMon-GEANT/wifimon-images/refs/heads/main/wifimon-logo.png)

<div align="justify">

## What is WiFiMon?  
**WiFiMon** is a Wi-Fi network monitoring and performance verification system. It is capable of detecting performance issues, visualising the achievable throughput of a wireless network per user and providing technical information about a Wi-Fi network (e.g. signal strength, link quality, bit rate, etc.). **WiFiMon** leverages on well-known performance verification tools (e.g. [Akamai Boomerang](https://github.com/akamai/boomerang) and [LibreSpeed Speedtest](https://github.com/librespeed/speedtest)) and, in addition, uses data from the Wi-Fi physical layer in order to gather a comprehensive set of Wi-Fi network performance metrics. 

## Types of WiFiMon Measurements
WiFiMon supports two kinds of measurements: (i) Software Crodsourced Measurements and (ii) Hardware Probe Measurements

### Software Crowdsourced Measurements
Measurements are gathered from users' mobile devices (phones, laptops, tablets, etc.) while they use the network. This approach does not require any additional software to be installed on the mobile devices. Measurements are recorded while the end users use the network and impose a minimal additional network overhead. Crowdsourced measurements capture the subjective perception of the WiFi network quality of service and responsiveness.

![Software measurements](https://raw.githubusercontent.com/WiFiMon-GEANT/wifimon-images/refs/heads/main/software-measurements.png)

### Hardware Probe Measurements
Measurements are gathered from dedicated small form factor hardware devices (currently Raspberry Pi devices). Fixed WiFiMon measurements capture objective measurements of WiFi network quality (signal strength, link quality, bit rate etc.).

![Hardware probe measurements](https://raw.githubusercontent.com/WiFiMon-GEANT/wifimon-images/refs/heads/main/hardware-measurements.png)

## WiFiMon Features
The main WiFiMon features are summarized below:

### Technology and Vendor Agnostic Operation
**WiFiMon** can be deployed on any WiFi network as it monitors the performance on the network layer. It can also provide additional benefits in 802.1x enabled networks including [eduroam](https://eduroam.org/) in which case users can make various performance analyses per access point, per user, etc.

![Easy to deploy](https://raw.githubusercontent.com/WiFiMon-GEANT/wifimon-images/refs/heads/main/features-1.png)

### Easy to Deploy
WiFiMon is a software image (also available as a Docker Image) and can be easily deployed on an NREN/University network on hardware or software probes.

<img src="https://raw.githubusercontent.com/WiFiMon-GEANT/wifimon-images/refs/heads/main/features-2.png" alt="Technology and vendor agnostic" width="300" />

### Fine-grained Information on Network Performance
WiFiMon shows the end-user (mobile client) behaviour on a network, its perception about the responsiveness of the network and the speed of web resource downloads, correlation of the performance data with end-user data, and data analysis with an effective query builder. 

![Fine-grained information on network performance](https://raw.githubusercontent.com/WiFiMon-GEANT/wifimon-images/refs/heads/main/features-3.png)

### Active Monitoring with Low Network Overhead
WiFiMon active measurements are not significantly invasive and do not use any significant bandwidth. One WiFiMon measurement is comparable to one average web-page download (load speed). 

![Active monitoring with low network overhead](https://raw.githubusercontent.com/WiFiMon-GEANT/wifimon-images/refs/heads/main/features-4.png)

</div>
