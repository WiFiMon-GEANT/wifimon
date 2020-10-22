#!/usr/bin/python

import subprocess
import datetime
import requests
from requests.packages.urllib3.exceptions import InsecureRequestWarning
requests.packages.urllib3.disable_warnings(InsecureRequestWarning)
import json

def return_command_output(command):
    proc = subprocess.Popen(command, stdout = subprocess.PIPE, shell = True)
    (out, err) = proc.communicate()
    output = out.rstrip('\n'.encode('utf8'))
    return output

def find_wlan_iface_name():
    command = "printf '%s\n' /sys/class/net/*/wireless | awk -F'/' '{print $5 }'"
    wlan_iface_name = return_command_output(command)
    return wlan_iface_name.decode('utf8')

def parse_iwconfig(iface):
    bit_rate = return_command_output("sudo iwconfig " + iface + " | grep Bit | awk '{print $2}' | sed 's/Rate=//'").decode('utf8')
    tx_power = return_command_output("sudo iwconfig " + iface + " | grep Bit | awk '{print $4}' | sed 's/Tx-Power=//'").decode('utf8')
    link_quality = return_command_output("sudo iwconfig " + iface + " | grep Link | awk '{print $2}' | sed 's/Quality=//'").decode('utf8')
    link_quality = link_quality.split("/")[0]
    signal_level = return_command_output("sudo iwconfig " + iface + " | grep Link | awk '{print $4}' | sed 's/level=//'").decode('utf8')
    accesspoint = return_command_output("sudo iwconfig " + iface + " | grep Mode | awk '{print $6}' | sed 's/Point: //'").decode('utf8')
    accesspoint = accesspoint.replace(":", "-")
    return bit_rate, tx_power, link_quality, signal_level, accesspoint

def parse_iwlist(iface, accesspoint):
    information = {}
    command = "sudo iwlist " + iface + " scan | grep -E \"Cell|Quality|ESSID\""
    aps = return_command_output(command).decode("utf8")
    aps = aps.split("\n")

    cell_indices = list()
    for index in range(0, len(aps)):
        line_no_whitespace = ' '.join(aps[index].split())
        parts = line_no_whitespace.split()
        if parts[0] == "Cell":
            cell_indices.append(index)

    for index in cell_indices:
        line0 = ' '.join(aps[index].split())
        ap_mac = line0.split()[-1]
        ap_mac = ap_mac.replace(":", "-")
        if ap_mac == accesspoint:
            continue
        information[ap_mac] = {}
        line1 = ' '.join(aps[index + 1].split())
        parts = line1.split()
        information[ap_mac]["linkQuality"] = parts[0].split("=")[1]
        information[ap_mac]["signalLevel"] = parts[2].split("=")[1]
        line2 = ' '.join(aps[index + 2].split())
        parts = line2.split(":")
        information[ap_mac]["essid"] = parts[1].replace('"', '')
    return information

def convert_info_to_json(timestamp, accesspoint, bit_rate, tx_power, link_quality, signal_level, probe_no, information):
    overall_dictionary = {}
    overall_dictionary["timestamp"] = str(timestamp)
    overall_dictionary["accesspoint"] = str(accesspoint)
    overall_dictionary["bitRate"] = str(bit_rate)
    overall_dictionary["txPower"] = str(tx_power)
    overall_dictionary["linkQuality"] = str(link_quality)
    overall_dictionary["signalLevel"] = str(signal_level)
    overall_dictionary["probeNo"] = str(probe_no)
    information = json.dumps(information)
    overall_dictionary["monitor"] = information
    json_data = json.dumps(overall_dictionary)
    return json_data

def stream_data(data):
    headers = {'content-type':"application/json"}
    try:
        session = requests.Session()
        session.verify = False
        session.post(url='https://fl-5-205.unil.cloud.switch.ch:8443/wifimon/probes/', data=data, headers=headers, timeout=15)
    except:
        pass

def wireless_info():
    iface_name = find_wlan_iface_name()
    bit_rate, tx_power, link_quality, signal_level, accesspoint = parse_iwconfig(iface_name)
    information = parse_iwlist(iface_name, accesspoint)
    timestamp = int(datetime.datetime.now().strftime("%s")) * 1000
    probe_no = "10"
    json_data = convert_info_to_json(timestamp, accesspoint, bit_rate, tx_power, link_quality, signal_level, probe_no, information)
    stream_data(json_data)

if __name__ == "__main__":
    wireless_info()
