<!DOCTYPE html>
<html lang="en">
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

    <!-- material symbols -->
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0" />

    <!-- jquery -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

    <!-- plotly -->
    <script src="https://cdn.plot.ly/plotly-2.27.0.min.js" charset="utf-8"></script>

    <link rel="stylesheet" href="${contextPath}/styles/styles.css">
</head>
<body>
    <div class="d-flex justify-content-between align-items-center">
        <h1>😂🎉😂</h1>
        <h1>Dashboard</h1>
        <p id="last-updated">Last updated:</p>
    </div>

    <script>
        if (localStorage.getItem("scroll-position") != null) {
            $(window).scrollTop(localStorage.getItem("scroll-position"));
        }

        $(window).on("scroll", function() {
            localStorage.setItem("scroll-position", $(window).scrollTop());
        });

        const plotId = "plot"

        let plotMeasurements = null

        function decimalToHex(d, padding) {
            let hex = Number(d).toString(16);
            padding = typeof (padding) === "undefined" || padding === null ? padding = 2 : padding;

            while (hex.length < padding) {
                hex = "0" + hex;
            }

            return hex.toUpperCase();
        }

        function getData(measures) {
            return [{
                x: measures.map((val, index, arr) => new Date(val.time)),
                y: measures.map((val, index, arr) => val.value),
                line: {shape: 'spline'},
            }];
        }
        function getLayout(measures) {
            return {
                xaxis: {
                    tickmode: "linear",
                    tick0: measures[0].time,
                    dtick: 60 * 1000,
                },
                yaxis: {
                    range: [0, 100],
                    type: "linear",
                },
                paper_bgcolor: "#0000",
                plot_bgcolor: "#0000",
                colorway: ["#0a3622"],
                margin: {
                    t: 24,
                    b: 48,
                },
                height: 350
            }
        }

        function lastChangedTime(measurements) {
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

        function makeDashboardValueCard(title, value, secondary, icon, styleClass) {
            return $('<div class="alert card col-md ' + styleClass + '">' +
                         '<span class="material-symbols-outlined">' + icon + '</span>' +
                         '<p class="card-name">' + title + '</p>' +
                         '<p class="card-value">' + value + '</p>' +
                         '<p class="card-secondary-text">' + secondary + '</p>' +
                     '</div>')
        }

        function makeDashboardPlotCard(title, value, icon, styleClass, plotId) {
            return $('<div class="alert card col-12-md ' + styleClass + '">' +
                         '<span class="material-symbols-outlined">' + icon + '</span>' +
                         '<p class="card-name">' + title + '</p>' +
                         '<p class="card-value">' + value + '</p>' +
                         '<div id="' + plotId + '" class="card-value"></div>' +
                     '</div>')
        }

        function buildDashboard(sensors) {
            const cards = [];
            let plotCard = null;
            for (const sensor of sensors) {
                const lastValue = sensor.measurements[sensor.measurements.length - 1].value;
                let icon = ''
                let value = ''
                let styleClass = ''
                switch (sensor.type) {
                    case "Door And Window Sensor":
                        icon = 'door_front'
                        value = lastValue === 0 ? "Closed" : "Open"
                        styleClass = lastValue === 0 ? "alert-success" : "alert-warning"
                        break;
                    case "Moving sensor":
                        icon = 'directions_run'
                        value = lastValue === 0 ? "Undetected" : "Detected"
                        styleClass = lastValue === 0 ? "alert-success" : "alert-warning"
                        break;
                    case "Water Leakage Sensor":
                        icon = 'water_damage'
                        value = lastValue === 0 ? "Undetected" : "Detected"
                        styleClass = lastValue === 0 ? "alert-success" : "alert-danger"
                        break;
                    case "Window Blind Sensor":
                        icon = 'blinds'
                        value = lastValue === 0 ? "Closed" : "Open"
                        styleClass = lastValue === 0 ? "alert-success" : "alert-warning"
                        break;
                    case "Smoke Sensor":
                        icon = 'local_fire_department'
                        value = lastValue === 0 ? "Undetected" : "Detected"
                        styleClass = lastValue === 0 ? "alert-success" : "alert-danger"
                        break;
                    case "Lighting Sensor":
                        if (sensor.measurementName === "intensity") {
                            icon = 'lightbulb'
                            value = lastValue
                        } else {
                            icon = 'palette'
                            value = "#" + decimalToHex(lastValue, 6)
                        }
                        styleClass = "alert-success"
                        break;
                }
                let title = sensor.type + "/" + sensor.measurementName
                let lastChanged = lastChangedTime(sensor.measurements)
                if (lastChanged == null) {
                    lastChanged = "Did not change yet"
                } else {
                    lastChanged = "Last changed: " + lastChanged
                }
                let card = null;
                if (sensor.measurementName === "intensity") {
                    plotCard = makeDashboardPlotCard(title, value, icon, "alert-success", plotId)
                    if (sensor.measurements.length < 20) {
                        plotMeasurements = sensor.measurements
                    } else {
                        plotMeasurements = sensor.measurements.slice(sensor.measurements.length - 20, sensor.measurements.length)
                    }
                } else if (sensor.measurementName === "color") {
                    card = makeDashboardValueCard(title, value, lastChanged, icon, styleClass)
                } else {
                    card = makeDashboardValueCard(title, value, lastChanged, icon, styleClass)
                }
                cards.push(card)
            }
            if (plotCard !== null) {
                cards.push(plotCard)
            }

            const rows = []
            let index = 0;
            let row = null;
            for (const card of cards) {
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

            $(".dashboard").remove()
            $("body").append(dashboard)
        }

        function drawPlot(plotId) {
            Plotly.newPlot(plotId, getData(plotMeasurements), getLayout(plotMeasurements));
        }

        function update() {
            $.post(
                "${contextPath}/sensors",
                (response) => {
                    buildDashboard(response)
                    drawPlot(plotId)
                    $("#last-updated").text("Last updated: " + new Date().toUTCString())
                }
            )
        }

        update()
        setInterval(update, 10000)
    </script>
</body>
</html>
