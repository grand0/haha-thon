<!DOCTYPE html>
<html lang="en" data-bs-theme="light">
<head>
    <meta charset="UTF-8">
    <title>hahathon</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- google fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Nunito&display=swap" rel="stylesheet">

    <!-- bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>

    <!-- bootstrap icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <!-- material symbols -->
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0" />

    <!-- jquery -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

    <!-- plotly -->
    <script src="https://cdn.plot.ly/plotly-2.27.0.min.js" charset="utf-8"></script>

    <link rel="stylesheet" href="${contextPath}/styles/styles.css">
</head>
<body>
    <div id="top-bar" class="d-flex justify-content-between">
        <h1>😂🎉😂</h1>
        <h1>Dashboard</h1>
        <div class="d-flex align-items-center">
            <div id="last-updated">Last updated:</div>
            <div class="vr m-3"></div>
            <a href="#" id="theme-switcher">
                <i class="bi bi-moon"></i>
            </a>
        </div>

    </div>

    <div class="offcanvas offcanvas-start" tabindex="-1" id="offcanvas-details">
        <div class="offcanvas-header">
            <h5 class="offcanvas-title">Sensor</h5>
            <button type="button" class="btn-close" data-bs-dismiss="offcanvas"></button>
        </div>
        <div class="offcanvas-body">
            <p id="offcanvas-sensor-id">ID:</p>
            <p id="offcanvas-measurement-name">Measurement name:</p>
            <p id="offcanvas-last-measurement">Last measurement:</p>
            <p id="offcanvas-time-last-measurement">Time of last measurement:</p>
            <p id="offcanvas-exploitation-start">Exploitation start:</p>
            <button type="button" id="disable-button" class="btn btn-secondary icon-link">Disable</button>
        </div>
    </div>

    <script>
        const OFFCANVAS_DETAILS = new bootstrap.Offcanvas($("#offcanvas-details"))

        const MOVING_SENSOR_TYPE = "Moving Sensor";
        const LEAKAGE_SENSOR_TYPE = "Water Leakage Sensor";
        const WINDOW_BLIND_SENSOR_TYPE = "Window Blind Sensor";
        const SMOKE_SENSOR_TYPE = "Smoke Sensor";
        const LIGHTING_SENSOR_TYPE = "Lighting Sensor";
        const DOOR_WINDOW_SENSOR_TYPE = "Door And Window Sensor";

        const DETECTED_MEASURE_NAME = "detected";
        const INTENSITY_MEASURE_NAME = "intensity";
        const COLOR_MEASURE_NAME = "color";

        const DISABLED = 0
        const OK = 1
        const WARNING = 2
        const DANGER = 3

        const SENSORS_CONFIG = {
            [MOVING_SENSOR_TYPE]: {
                [DETECTED_MEASURE_NAME]: {
                    icon: "directions_run",
                    detectedValue: "Detected",
                    undetectedValue: "Undetected",
                    detectedState: WARNING,
                    undetectedState: OK,
                },
            },
            [LEAKAGE_SENSOR_TYPE]: {
                [DETECTED_MEASURE_NAME]: {
                    icon: "water_damage",
                    detectedValue: "Detected",
                    undetectedValue: "Undetected",
                    detectedState: DANGER,
                    undetectedState: OK,
                },
            },
            [WINDOW_BLIND_SENSOR_TYPE]: {
                [DETECTED_MEASURE_NAME]: {
                    icon: "blinds",
                    detectedValue: "Open",
                    undetectedValue: "Closed",
                    detectedState: WARNING,
                    undetectedState: OK,
                },
            },
            [SMOKE_SENSOR_TYPE]: {
                [DETECTED_MEASURE_NAME]: {
                    icon: "local_fire_department",
                    detectedValue: "Detected",
                    undetectedValue: "Undetected",
                    detectedState: DANGER,
                    undetectedState: OK,
                },
            },
            [LIGHTING_SENSOR_TYPE]: {
                [INTENSITY_MEASURE_NAME]: {
                    icon: "lightbulb",
                },
                [COLOR_MEASURE_NAME]: {
                    icon: "palette",
                },
            },
            [DOOR_WINDOW_SENSOR_TYPE]: {
                [DETECTED_MEASURE_NAME]: {
                    icon: "door_front",
                    detectedValue: "Open",
                    undetectedValue: "Closed",
                    detectedState: WARNING,
                    undetectedState: OK,
                },
            }
        }

        function decimalToHex(d, padding) {
            let hex = Number(d).toString(16);
            padding = typeof(padding) === "undefined" || padding === null ? 2 : padding;

            while (hex.length < padding) {
                hex = "0" + hex;
            }

            return hex.toUpperCase();
        }

        function preparePlotData(measures) {
            return [{
                x: measures.map((val, index, arr) => new Date(val.time)),
                y: measures.map((val, index, arr) => val.value),
                line: {shape: 'spline'},
            }];
        }
        function preparePlotLayout(measures) {
            const color = "#75b798"
            return {
                xaxis: {
                    tickmode: "linear",
                    tick0: new Date(measures[0].time),
                    dtick: 60 * 1000,
                    color: color,
                },
                yaxis: {
                    range: [0, 100],
                    type: "linear",
                    color: color,
                },
                paper_bgcolor: "#0000",
                plot_bgcolor: "#0000",
                colorway: [color],
                margin: {
                    t: 24,
                    b: 48,
                },
                height: 350
            }
        }

        function findLastChangedTime(measurements) {
            if (measurements.length > 0) {
                let lastValue = measurements[measurements.length - 1].value
                for (let i = measurements.length - 2; i >= 0; i--) {
                    if (measurements[i].value !== lastValue) {
                        return measurements[i].time
                    }
                }
            }
            return null
        }

        function getCardStyleClassForState(state) {
            let styleClass = 'alert-success'
            if (state === DISABLED) styleClass = 'alert-secondary'
            else if (state === WARNING) styleClass = 'alert-warning'
            else if (state === DANGER) styleClass = 'alert-danger'
            return styleClass
        }

        function makeDashboardValueCard(title, value, lastChanged, iconName, state) {
            const styleClass = getCardStyleClassForState(state)
            return $('<div class="alert card col-md ' + styleClass + '">' +
                         '<span class="material-symbols-outlined">' + iconName + '</span>' +
                         '<p class="card-name">' + title + '</p>' +
                         '<p class="card-value">' + value + '</p>' +
                         '<p class="card-secondary-text">' + lastChanged + '</p>' +
                     '</div>')
        }

        function makeDashboardPlotCard(title, value, iconName, state, plotId) {
            const styleClass = getCardStyleClassForState(state)
            return $('<div class="alert card col-12-md ' + styleClass + '">' +
                         '<span class="material-symbols-outlined">' + iconName + '</span>' +
                         '<p class="card-name">' + title + '</p>' +
                         '<p class="card-value">' + value + '</p>' +
                         '<div id="' + plotId + '" class="card-value"></div>' +
                     '</div>')
        }

        function prepareOffcanvas(sensor) {
            $(".offcanvas-title")
                .text(sensor.type)
            $("#offcanvas-sensor-id")
                .text("ID: " + sensor.id)
            $("#offcanvas-measurement-name")
                .text("Measurement name: " + sensor.measurementName)
            $("#offcanvas-last-measurement")
                .text("Last measurement: " + sensor.measurements[sensor.measurements.length - 1].value)
            $("#offcanvas-time-last-measurement")
                .text("Time of last measurement: " + new Date(sensor.measurements[sensor.measurements.length - 1].time).toLocaleString())
            $("#offcanvas-exploitation-start")
                .text("Exploitation start: " + new Date(sensor.exploitationStartDate).toLocaleString())

            if (sensor.state) {
                $("#disable-button")
                    .removeClass("btn-primary")
                    .addClass("btn-secondary")
                    .text("")
                    .append($('<span class="material-symbols-outlined">power_settings_new</span>'))
                    .append("Disable")
            } else {
                $("#disable-button")
                    .removeClass("btn-secondary")
                    .addClass("btn-primary")
                    .text("")
                    .append($('<span class="material-symbols-outlined">power_settings_new</span>'))
                    .append("Enable")
            }
            $("#disable-button")
                .off()
                .on("click", () => {
                    $.post(
                        "${contextPath}/sensors",
                        {
                            action: "toggle",
                            sensorId: sensor.id
                        },
                        (resp) => {
                            OFFCANVAS_DETAILS.hide()
                            update()
                        }
                    )
                })
        }

        function buildDashboard(sensors) {
            const valueCards = [];
            const plotCards = [];
            const plots = [];
            for (const sensor of sensors) {
                const lastValue = sensor.measurements[sensor.measurements.length - 1].value;

                let icon = SENSORS_CONFIG[sensor.type][sensor.measurementName].icon

                let value = ''
                if (!sensor.state) {
                    value = "Disabled"
                } else if (sensor.measurementName === DETECTED_MEASURE_NAME) {
                    if (lastValue) {
                        value = SENSORS_CONFIG[sensor.type][sensor.measurementName].detectedValue
                    } else {
                        value = SENSORS_CONFIG[sensor.type][sensor.measurementName].undetectedValue
                    }
                } else if (sensor.measurementName === COLOR_MEASURE_NAME) {
                    value = '#' + (lastValue === null ? '------' : decimalToHex(lastValue, 6))
                } else {
                    value = lastValue === null ? '--' : lastValue
                }

                let state = OK
                if (!sensor.state) {
                    state = DISABLED
                } else if (sensor.measurementName === DETECTED_MEASURE_NAME) {
                    if (lastValue) {
                        state = SENSORS_CONFIG[sensor.type][sensor.measurementName].detectedState
                    } else {
                        state = SENSORS_CONFIG[sensor.type][sensor.measurementName].undetectedState
                    }
                }

                let title = sensor.type + " - " + sensor.measurementName

                let lastChanged = findLastChangedTime(sensor.measurements)
                if (lastChanged == null) {
                    lastChanged = "Did not change yet"
                } else {
                    lastChanged = "Last changed: " + new Date(lastChanged).toLocaleString()
                }

                const bindOnClick = (card) => card
                    .on("click", () => {
                        prepareOffcanvas(sensor)
                        OFFCANVAS_DETAILS.show()
                    })
                if (sensor.measurementName === INTENSITY_MEASURE_NAME) {
                    const plotId = "plot" + plots.length
                    const card = makeDashboardPlotCard(title, value, icon, state, plotId)
                    bindOnClick(card)
                    plotCards.push(card)
                    let plotMeasurements = []
                    if (sensor.measurements.length < 20) {
                        plotMeasurements = sensor.measurements
                    } else {
                        plotMeasurements = sensor.measurements.slice(sensor.measurements.length - 20, sensor.measurements.length)
                    }
                    plots.push({
                        id: plotId,
                        data: preparePlotData(plotMeasurements),
                        layout: preparePlotLayout(plotMeasurements),
                    })
                } else {
                    const card = makeDashboardValueCard(title, value, lastChanged, icon, state)
                    bindOnClick(card)
                    valueCards.push(card)
                }
            }

            const rows = []
            let index = 0;
            let row = null;
            for (const card of valueCards) {
                if (index % 4 === 0) {
                    if (row != null) {
                        rows.push(row)
                    }
                    row = $('<div class="row"></div>')
                }
                row.append(card)
                index++
            }
            if (row != null) {
                rows.push(row)
            }

            const dashboard = $('<div class="dashboard"></div>')
            for (const row of rows) {
                dashboard.append(row)
            }
            for (const card of plotCards) {
                dashboard.append(card)
            }

            $(".dashboard").remove()
            $("body").append(dashboard)

            for (const plot of plots) {
                Plotly.newPlot(plot.id, plot.data, plot.layout)
            }
        }

        function update() {
            $.post(
                "${contextPath}/sensors",
                (response) => {
                    buildDashboard(response)
                    $("#last-updated").text("Last updated: " + new Date().toLocaleString())
                }
            )
        }

        update()
        setInterval(update, 10000)
    </script>

    <script>
        function getThemeCookieValue() {
            return document.cookie
                .split("; ")
                .find((row) => row.startsWith("theme="))
                ?.split("=")[1];
        }
        if (getThemeCookieValue() === "dark") {
            document.getElementsByTagName("html")[0].setAttribute("data-bs-theme", "dark")
            document.getElementById("theme-switcher").firstElementChild.classList.add("bi-sun")
            document.getElementById("theme-switcher").firstElementChild.classList.remove("bi-moon")
        }

        document.getElementById("theme-switcher").addEventListener("click", function () {
            if (getThemeCookieValue() === "dark") {
                document.cookie = "theme=light; path=/; max-age=31536000"
                document.getElementsByTagName("html")[0].setAttribute("data-bs-theme", "light")
                document.getElementById("theme-switcher").firstElementChild.classList.remove("bi-sun")
                document.getElementById("theme-switcher").firstElementChild.classList.add("bi-moon")
            } else {
                document.cookie = "theme=dark; path=/; max-age=31536000"
                document.getElementsByTagName("html")[0].setAttribute("data-bs-theme", "dark")
                document.getElementById("theme-switcher").firstElementChild.classList.add("bi-sun")
                document.getElementById("theme-switcher").firstElementChild.classList.remove("bi-moon")
            }
        })
    </script>
</body>
</html>
