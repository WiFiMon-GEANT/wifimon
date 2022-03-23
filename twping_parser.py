'''
Sample twping output (MIND THE NAMING OF THE LINES)

line 0: --- twping statistics from [192.168.1.1]:9706 to [192.168.1.2]:19642 ---
line 1: SID:    c0a80102e5e36a42b8a73f74cec8780e
line 2: first:  2022-03-21T23:18:58.819
line 3: last:   2022-03-21T23:19:10.456
line 4: 100 sent, 0 lost (0.000%), 0 send duplicates, 0 reflect duplicates
line 5: round-trip time min/median/max = 0.109/0.3/1.07 ms, (err=3.8 ms)
line 6: send time min/median/max = 936/936/936 ms, (err=1.9 ms)
line 7: reflect time min/median/max = -936/-936/-935 ms, (err=1.9 ms)
line 8: reflector processing time min/max = 0.00191/0.021 ms
line 9: two-way jitter = 0.1 ms (P95-P50)
line 10: send jitter = 0.1 ms (P95-P50)
line 11: reflect jitter = 0 ms (P95-P50)
line 12: send hops = 0 (consistently)
line 13:reflect hops = 0 (consistently)
'''

import subprocess
import json
import requests
from requests.packages.urllib3.exceptions import InsecureRequestWarning
requests.packages.urllib3.disable_warnings(InsecureRequestWarning)

def return_command_output(command):
    '''
        Execute a command and return its output
    '''
    proc = subprocess.Popen(command, stdout = subprocess.PIPE, shell = True)
    (out, err) = proc.communicate()
    output = out.rstrip('\n'.encode('utf8'))
    return output

def perform_twping(twamp_server_ip):
    '''
        Perform the twping command and retrieve its output in milliseconds
    '''
    command = "twping " + str(twamp_server_ip) + " -n m"
    twping_results = return_command_output(command).decode('utf8')
    return twping_results

def locate_twping_data(twping_output):
    '''
        Find the line at which the important part of the twping output starts
    '''
    twping_output_parts = twping_output.split('\n')
    line_to_start = 0
    for line in twping_output_parts:
        initial_three_chars = line[0:3]
        if initial_three_chars == "---":
            break
        line_to_start += 1
    return line_to_start

# Parse lines one by one. Look at the top for the numbering of the lines
def parse_line4(line4):
    parts = line4.split(" ")
    sent, lost, send_dups, reflect_dups = parts[0], parts[2], parts[5], parts[8]
    return sent, lost, send_dups, reflect_dups

def parse_times(line):
    parts = line.split(" ")
    min_median_max = parts[4].split("/")
    minimum, median, maximum = min_median_max[0], min_median_max[1], min_median_max[2]
    err = parts[6].split("=")[1]
    return minimum, median, maximum, err

def parse_line8(line):
    parts = line.split(" ")
    time_unit = parts[-1]
    minimum = parts[-2].split("/")[0]
    maximum = parts[-2].split("/")[1]
    return minimum, maximum

def parse_jitter(line):
    parts = line.split(" ")
    value = parts[3]
    characterization = parts[5][1:-1]
    return value, characterization

def parse_hops(line):
    parts = line.split(" ")
    value = parts[3]
    characterization = parts[4][1:-1]
    return value, characterization

def form_json(probe_number, twamp_server, sent, lost, send_dups, reflect_dups, 
        min_rtt, median_rtt, max_rtt, err_rtt, min_send, median_send, max_send, 
        err_send, min_reflect, median_reflect, max_reflect, err_reflect, 
        min_reflector_processing_time, max_reflector_processing_time,
        two_way_jitter_value, two_way_jitter_char, send_jitter_value, send_jitter_char,
        reflect_jitter_value, reflect_jitter_char, send_hops_value, send_hops_char,
        reflect_hops_value, reflect_hops_char):
    '''
        Create a json object with the parsed values. Values are first stored in a dictionary.
    '''
    overall_dictionary = {}
    overall_dictionary["probeNumber"] = probe_number
    overall_dictionary["twampServer"] = twamp_server
    overall_dictionary["sent"] = sent
    overall_dictionary["lost"] = lost
    overall_dictionary["sendDups"] = send_dups
    overall_dictionary["reflectDups"] = reflect_dups
    overall_dictionary["minRtt"] = min_rtt
    overall_dictionary["medianRtt"] = median_rtt
    overall_dictionary["maxRtt"] = max_rtt
    overall_dictionary["errRtt"] = err_rtt
    overall_dictionary["minSend"] = min_send
    overall_dictionary["medianSend"] = median_send
    overall_dictionary["maxSend"] = max_send
    overall_dictionary["errSend"] = err_send
    overall_dictionary["minReflect"] = min_reflect
    overall_dictionary["medianReflect"] = median_reflect
    overall_dictionary["maxReflect"] = max_reflect
    overall_dictionary["errReflect"] = err_reflect
    overall_dictionary["minReflectorProcessingTime"] = min_reflector_processing_time
    overall_dictionary["maxReflectorProcessingTime"] = max_reflector_processing_time
    overall_dictionary["twoWayJitterValue"] = two_way_jitter_value
    overall_dictionary["twoWayJitterChar"] = two_way_jitter_char
    overall_dictionary["sendJitterValue"] = send_jitter_value
    overall_dictionary["sendJitterChar"] = send_jitter_char
    overall_dictionary["reflectJitterValue"] = reflect_jitter_value
    overall_dictionary["reflectJitterChar"] = reflect_jitter_char
    overall_dictionary["sendHopsValue"] = send_hops_value
    overall_dictionary["sendHopsChar"] = send_hops_char
    overall_dictionary["reflectHopsValue"] = reflect_hops_value
    overall_dictionary["reflectHopsChar"] = reflect_hops_char
    json_data = json.dumps(overall_dictionary)
    return json_data

def parse_twping(twping_output, line_to_start, probe_number):
    '''
        Parse twping output line by line
    '''
    twping_output_parts = twping_output.split('\n')
    sent, lost, send_dups, reflect_dups = parse_line4(twping_output_parts[line_to_start + 4])
    min_rtt, median_rtt, max_rtt, err_rtt = parse_times(twping_output_parts[line_to_start + 5])
    min_send, median_send, max_send, err_send = parse_times(twping_output_parts[line_to_start + 6])
    min_reflect, median_reflect, max_reflect, err_reflect = parse_times(twping_output_parts[line_to_start + 7])
    min_reflector_processing_time, max_reflector_processing_time = parse_line8(twping_output_parts[line_to_start +8])
    two_way_jitter_value, two_way_jitter_char = parse_jitter(twping_output_parts[line_to_start + 9])
    send_jitter_value, send_jitter_char = parse_jitter(twping_output_parts[line_to_start + 10])
    reflect_jitter_value, reflect_jitter_char = parse_jitter(twping_output_parts[line_to_start + 11])
    send_hops_value, send_hops_char = parse_hops(twping_output_parts[line_to_start + 12])
    reflect_hops_value, reflect_hops_char = parse_hops(twping_output_parts[line_to_start + 13])
    json_data = form_json(probe_number, twamp_server, sent, lost, send_dups, reflect_dups, 
            min_rtt, median_rtt, max_rtt, err_rtt, min_send, median_send, max_send, err_send, 
            min_reflect, median_reflect, max_reflect, err_reflect, min_reflector_processing_time,
            max_reflector_processing_time, two_way_jitter_value, two_way_jitter_char, 
            send_jitter_value, send_jitter_char, reflect_jitter_value, reflect_jitter_char, 
            send_hops_value, send_hops_char, reflect_hops_value, reflect_hops_char)
    return json_data

def stream_data(json_data):
    '''
        Stream JSON data to the WiFiMon Analysis Server
        Set the FQDN of the WiFiMon Analysis Server
    '''
    headers = {'content-type' : "application/json"}
    try:
        session = requests.Session()
        session.verify = False
        session.post(url = 'https://WAS_FQDN:443/wifimon/twamp/', data = json_data, headers = headers, timeout = 30)
    except:
        pass
    return None

if __name__ == "__main__":
    # Define the number of the WiFiMon Hardware Probe
    PROBE_NO = "1"
    # Define the FQDN of the TWAMP Server
    twamp_server = "TWAMP_SERVER_FQDN"
    # Perform twping against the TWAMP Server
    twping_results = perform_twping(twamp_server)
    # Parse twping results
    line_to_start = locate_twping_data(twping_results)
    json_data = parse_twping(twping_results, line_to_start, PROBE_NO)
    # Stream data to the WiFiMon Analysis Server
    stream_data(json_data)
