#!/usr/bin/python3

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

def get_mac(iface):
    command = "cat /sys/class/net/" + str(iface) + "/address"
    mac = return_command_output(command).decode('utf8')
    mac = mac.replace(":", "-")
    return mac

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
    essid = return_command_output("sudo iwconfig " + iface + " | grep ESSID | awk '{print $4}' | sed 's/ESSID://'").decode('utf8')
    essid = essid.replace("\"", "")
    return bit_rate, tx_power, link_quality, signal_level, accesspoint, essid

def parse_iwlist(iface, accesspoint):
    information = {}
    command = "sudo iwlist " + iface + " scan | grep -E \"Cell|Frequency|Quality|ESSID\""
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
        information[ap_mac] = {}
        line1 = ' '.join(aps[index + 1].split())
        frequency = line1.split()[0].split(":")[1]
        information[ap_mac]["frequency"] = str(frequency)
        line2 = ' '.join(aps[index + 2].split())
        parts = line2.split()
        information[ap_mac]["drillTest"] = float(parts[2].split("=")[1])
        line3 = ' '.join(aps[index + 3].split())
        parts = line3.split(":")
        information[ap_mac][str(parts[1].replace('"', ''))] = information[ap_mac]["drillTest"]

    return information

def convert_info_to_json(accesspoint, essid, mac, bit_rate, tx_power, link_quality, signal_level, probe_no, information, location_name, test_device_location_description, nat_network, system_dictionary):
    overall_dictionary = {}
    overall_dictionary["macAddress"] = "\"" + str(mac) + "\""
    overall_dictionary["accesspoint"] = "\"" + str(accesspoint) + "\""
    overall_dictionary["essid"] = "\"" + str(essid) + "\""
    bit_rate = int(float(bit_rate))
    overall_dictionary["bitRate"] = str(bit_rate)
    tx_power = int(float(tx_power))
    overall_dictionary["txPower"] = str(tx_power)
    link_quality = int(float(link_quality))
    overall_dictionary["linkQuality"] = str(link_quality)
    signal_level = int(float(signal_level))
    overall_dictionary["signalLevel"] = str(signal_level)
    overall_dictionary["probeNo"] = str(probe_no)
    information = json.dumps(information)
    overall_dictionary["monitor"] = information
    overall_dictionary["locationName"] = "\"" + str(location_name) + "\""
    overall_dictionary["testDeviceLocationDescription"] = "\"" + str(test_device_location_description) + "\""
    overall_dictionary["nat"] = "\"" + str(nat_network) + "\""
    system_dictionary = json.dumps(system_dictionary)
    overall_dictionary["system"] = system_dictionary
    json_data = json.dumps(overall_dictionary)
    return json_data

def processing_info():
    command = '''echo "$(iostat | head -1 | awk '{print $1}')"'''
    operating_system = return_command_output(command).decode('utf8')
    command = '''echo "$(iostat | head -1 | awk '{print $2}')"'''
    driver_version = return_command_output(command).decode('utf8')
    command = '''echo "$(iostat | head -1 | awk '{print $6}' | cut -c 2-)"'''
    total_cores = return_command_output(command).decode('utf8')
    command = '''echo "$(vmstat 1 2|tail -1|awk '{print $15}')"'''
    cpu_utilization = 100 - int(return_command_output(command).decode('utf8'))
    command = '''echo "$(vmstat --stats | grep 'total memory' | tail -1 | awk '{print $1}')"'''
    total_memory = return_command_output(command).decode('utf8')
    command = '''echo "$(vmstat --stats | grep 'used memory' | tail -1 | awk '{print $1}')"'''
    used_memory = return_command_output(command).decode('utf8')
    command = '''echo "$(df -h / | tail -1 | awk '{print $2}')"'''
    total_disk_size = return_command_output(command).decode('utf8')
    command = '''echo "$(df -h / | tail -1 | awk '{print $3}')"'''
    used_disk_size = return_command_output(command).decode('utf8')

    system_dictionary = {}
    system_dictionary["operatingSystem"] = str(operating_system)
    system_dictionary["driverVersion"] = str(driver_version)
    system_dictionary["totalCores"] = str(total_cores) 
    system_dictionary["cpuUtilization"] = str(cpu_utilization)
    system_dictionary["totalMemory"] = str(total_memory) 
    system_dictionary["usedMemory"] = str(used_memory)
    system_dictionary["totalDiskSize"] = str(total_disk_size) 
    system_dictionary["usedDiskSize"] = str(used_disk_size)

    return system_dictionary

def stream_data(data):
    headers = {'content-type':"application/json"}
    try:
        session = requests.Session()
        session.verify = False
        session.post(url='https://WAS_FQDN:443/wifimon/probes/', data=data, headers=headers, timeout=30)
    except:
        pass

def set_location_information():
    location_name = ""
    test_device_location_description = ""
    nat_network = ""
    return location_name, test_device_location_description, nat_network

def wireless_info():
    system_dictionary = processing_info()
    location_name, test_device_location_description, nat_network = set_location_information()
    iface_name = find_wlan_iface_name()
    mac = get_mac(iface_name)
    bit_rate, tx_power, link_quality, signal_level, accesspoint, essid = parse_iwconfig(iface_name)
    information = parse_iwlist(iface_name, accesspoint)
    probe_no = ""
    json_data = convert_info_to_json(accesspoint, essid, mac, bit_rate, tx_power, link_quality, signal_level, probe_no, information, location_name, test_device_location_description, nat_network, system_dictionary)
    stream_data(json_data)

if __name__ == "__main__":
    wireless_info()
