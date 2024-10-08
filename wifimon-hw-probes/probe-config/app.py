from flask import Flask, request, render_template, redirect
from jinja2 import Environment, FileSystemLoader
import os
from os import environ
import time
from time import sleep
app = Flask(__name__)
@app.route("/")
def my_form():
    return render_template("index.html")
@app.route("/", methods = ["POST"])
def my_form_post():
    if request.form["token"] != os.environ['PROBE_SECRET']:
        return None
    probe_number = request.form["probe_number"]
    wts = request.form["wts"]
    was = request.form["was"]
    twamp_server = request.form["twamp_server"]
    location_name = request.form["location_name"]
    test_device_location_description = request.form["test_device_location_description"]
    nat_network = request.form["nat_network"]
    protocol = request.form["protocol"]
    if protocol == "https":
        port = "443"
    elif protocol == "http":
        port = "80"

    environment = Environment(loader=FileSystemLoader("templates/"))

    template = environment.get_template("wireless_template.txt")
    destination_filename = "./render_results/wireless.py"
    content = template.render(
        probe_number = probe_number,
        wts = wts,
        location_name = location_name,
        test_device_location_description = test_device_location_description,
        nat_network = nat_network,
        was = was,
        protocol = protocol,
        port = port
    )
    with open(destination_filename, mode = "w") as message:
        message.write(content)

    template = environment.get_template("twping_template.txt")
    destination_filename = "./render_results/twping_parser.py"
    content = template.render(
        probe_number = probe_number,
        twamp_server = twamp_server,
        was = was,
        protocol = protocol,
        port = port
    )
    with open(destination_filename, mode = "w") as message:
        message.write(content)

    template = environment.get_template("nettest-template.txt")
    destination_filename = "./render_results/nettest.sh"
    content = template.render(
        probe_number = probe_number,
        wts = wts,
        protocol = protocol,
        port = port
    )
    with open(destination_filename, mode = "w") as message:
        message.write(content)

    template = environment.get_template("boomerang-template.txt")
    destination_filename = "./render_results/boomerang.sh"
    content = template.render(
        probe_number = probe_number,
        wts = wts,
        protocol = protocol,
        port = port
    )
    with open(destination_filename, mode = "w") as message:
        message.write(content)

    template = environment.get_template("speedtest-template.txt")
    destination_filename = "./render_results/speedtest.sh"
    content = template.render(
        probe_number = probe_number,
        wts = wts,
        protocol = protocol,
        port = port
    )
    with open(destination_filename, mode = "w") as message:
        message.write(content)

    template = environment.get_template("crontab_pi_template.txt")
    destination_filename = "./render_results/crontab_pi.txt"
    content = template.render(
    )
    with open(destination_filename, mode = "w") as message:
        message.write(content)

    template = environment.get_template("crontab_root_template.txt")
    destination_filename = "./render_results/crontab_root.txt"
    content = template.render(
    )
    with open(destination_filename, mode = "w") as message:
        message.write(content)

    command = "salt-cp '" + str(probe_number) + "' './render_results/wireless.py' '/root/wireless.py'"
    os.system(command)

    command = "salt-cp '" + str(probe_number) + "' './render_results/twping_parser.py' '/root/twping_parser.py'"
    os.system(command)

    command = "salt-cp '" + str(probe_number) + "' './render_results/nettest.sh' '/usr/local/bin/nettest.sh'"
    os.system(command)
    command = "salt '" + str(probe_number) + "' cmd.run 'sudo chown pi:pi /usr/local/bin/nettest.sh'"
    os.system(command)
    command = "salt '" + str(probe_number) + "' cmd.run 'sudo chmod +x /usr/local/bin/nettest.sh'"
    os.system(command)

    command = "salt-cp '" + str(probe_number) + "' './render_results/boomerang.sh' '/usr/local/bin/boomerang.sh'"
    os.system(command)
    command = "salt '" + str(probe_number) + "' cmd.run 'sudo chown pi:pi /usr/local/bin/boomerang.sh'"
    os.system(command)
    command = "salt '" + str(probe_number) + "' cmd.run 'sudo chmod +x /usr/local/bin/boomerang.sh'"
    os.system(command)

    command = "salt-cp '" + str(probe_number) + "' './render_results/speedtest.sh' '/usr/local/bin/speedtest.sh'"
    os.system(command)
    command = "salt '" + str(probe_number) + "' cmd.run 'sudo chown pi:pi /usr/local/bin/speedtest.sh'"
    os.system(command)
    command = "salt '" + str(probe_number) + "' cmd.run 'sudo chmod +x /usr/local/bin/speedtest.sh'"
    os.system(command)

    command = "salt-cp '" + str(probe_number) + "' './render_results/crontab_root.txt' '/root/crontab.txt'"
    os.system(command)
    command = "salt '" + str(probe_number) + "' cmd.run 'sudo crontab /root/crontab.txt'"
    os.system(command)

    command = "salt-cp '" + str(probe_number) + "' './render_results/crontab_pi.txt' '/home/pi/crontab.txt'"
    os.system(command)
    command = "salt '" + str(probe_number) + "' cmd.run 'sudo crontab -u pi /home/pi/crontab.txt'"
    os.system(command)



    return redirect("https://krist-ui.netmode.ece.ntua.gr")
if __name__ == "__main__":
    app.run()
