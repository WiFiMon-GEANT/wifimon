#!/usr/bin/python

import subprocess
import datetime
import requests
from requests.packages.urllib3.exceptions import InsecureRequestWarning
requests.packages.urllib3.disable_warnings(InsecureRequestWarning)

def return_command_output(command):
    proc = subprocess.Popen(command, stdout = subprocess.PIPE, shell = True)
    (out, err) = proc.communicate()
    out_without_carriage_return = out.rstrip('\n')
    return out_without_carriage_return

def parse_iwconfig():
    command1 = "sudo iwconfig wlan0 | grep \"Link Quality\""
    command1_output = return_command_output(command1)
    command1_output = ' '.join(command1_output.split())
    command1_parsed = command1_output.split(" ")
    link_quality = command1_parsed[1].split("=")[1]
    link_quality = link_quality.split("/")[0]
    signal_level = command1_parsed[3].split("=")[1]
    command2 = "sudo iwconfig wlan0 | grep \"Tx-Power\""
    command2_output = return_command_output(command2)
    command2_output = ' '.join(command2_output.split())
    command2_parsed = command2_output.split(" ")
    bit_rate = command2_parsed[1].split("=")[1]
    tx_power = command2_parsed[3].split("=")[1]

    timestamp = int(datetime.datetime.now().strftime("%s")) * 1000
    probeNo = "1"

    headers = {'content-type':"application/json"}
    data = "{\"timestamp\":" + str(timestamp) + ", \"bitRate\":" + bit_rate + ", \"txPower\":" + tx_power + ", \"linkQuality\":" + link_quality + ", \"signalLevel\":" + signal_level + ", \"probeNo\":\"" + probeNo + "\"}"
    try:
        session = requests.Session()
        session.verify = False
        session.post(url='https://WAS_FQDN:8443/wifimon/probes/', data=data, headers=headers, timeout=15)
    except:
        pass
    return None

if __name__ == "__main__":
    parse_iwconfig()
