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
    signal_level = return_command_output("sudo iwconfig " + iface + " | grep Link | awk '{print $4}' | sed 's/level=//'").decode('utf8')
    accesspoint = return_command_output("sudo iwconfig " + iface + " | grep Mode | awk '{print $6}' | sed 's/Point: //'").decode('utf8')
    return bit_rate, tx_power, link_quality, signal_level, accesspoint

def parse_iwlist(iface, accesspoint):
    information = {}
    command = "sudo iwlist " + iface + " scan | grep -E \"Cell|Quality|ESSID\""
    aps = return_command_output(command).decode("utf8")
    aps = aps.split("\n")
    for index in range(0, len(aps)):
        if index % 3 == 0:
            line0 = ' '.join(aps[index].split())
            ap_mac = line0.split()[-1]
            if ap_mac == accesspoint:
                continue
            information[ap_mac] = {}
        elif index % 3 == 1:
            line1 = ' '.join(aps[index].split())
            parts = line1.split()
            if ap_mac == accesspoint:
                continue
            information[ap_mac]["quality"] = parts[0].split("=")[1]
            information[ap_mac]["signal_level"] = parts[2].split("=")[1]
        else:
            line2 = ' '.join(aps[index].split())
            parts = line2.split(":")
            if ap_mac == accesspoint:
                continue
            information[ap_mac]["essid"] = parts[1].replace('"', '')
    return information

def convert_info_to_json(timestamp, accesspoint, bit_rate, tx_power, link_quality, signal_level, probe_no, information):
    overall_dictionary = {}
    overall_dictionary["Timestamp"] = timestamp
    overall_dictionary["Accesspoint"] = accesspoint
    overall_dictionary["Bit_Rate"] = bit_rate
    overall_dictionary["Tx_Power"] = tx_power
    overall_dictionary["Link_Quality"] = link_quality
    overall_dictionary["Signal_Level"] = signal_level
    overall_dictionary["Probe_No"] = probe_no
    overall_dictionary["Monitor"] = information
    json_data = json.dumps(overall_dictionary)
    return json_data

def stream_data(json_data):
    headers = {'content-type':"application/json"}
    try:
        session = requests.Session()
        session.verify = False
        session.post(url='https://localhost:8443/wifimon/probes/', data=json_data, headers=headers, timeout=15)
    except:
        pass
    return None

def wireless_info():
    iface_name = find_wlan_iface_name()
    bit_rate, tx_power, link_quality, signal_level, accesspoint = parse_iwconfig(iface_name)
    information = parse_iwlist(iface_name, accesspoint)
    timestamp = int(datetime.datetime.now().strftime("%s")) * 1000
    probe_no = "1"
    json_data = convert_info_to_json(timestamp, accesspoint, bit_rate, tx_power, link_quality, signal_level, probe_no, information)
    stream_data(json_data)
    return None

if __name__ == "__main__":
    wireless_info()
