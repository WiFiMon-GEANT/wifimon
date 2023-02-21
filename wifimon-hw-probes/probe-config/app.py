from flask import Flask, request, render_template, redirect
from jinja2 import Environment, FileSystemLoader
import os
import time
from time import sleep

app = Flask(__name__)

@app.route("/")
def my_form():
    return render_template("index.html")

@app.route("/", methods = ["POST"])
def my_form_post():
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

    template = environment.get_template("crontab_template.txt")

    destination_filename = "./render_results/crontab.txt"
    content = template.render(
        probe_number = probe_number,
        wts = wts,
        protocol = protocol
    )

    with open(destination_filename, mode = "w") as message:
        message.write(content)

    command = "crontab " + str(destination_filename)
    os.system(command)

    command = "salt-cp '" + str(probe_number) + "' './render_results/crontab.txt' '/root/crontab.txt'"
    print(command)
    os.system(command)

    return redirect("https://rabbit-ui.netmode.ece.ntua.gr")

if __name__ == "__main__":
    app.run()
