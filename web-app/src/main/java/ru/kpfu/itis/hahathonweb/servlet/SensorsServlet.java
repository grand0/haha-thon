package ru.kpfu.itis.hahathonweb.servlet;

import com.google.gson.Gson;
import ru.kpfu.itis.hahathonweb.dao.MeasurementIntDataDao;
import ru.kpfu.itis.hahathonweb.dao.MeasurementNamesDao;
import ru.kpfu.itis.hahathonweb.dao.SensorDao;
import ru.kpfu.itis.hahathonweb.dao.TypeDao;
import ru.kpfu.itis.hahathonweb.dto.SensorDto;
import ru.kpfu.itis.hahathonweb.model.MeasurementIntData;
import ru.kpfu.itis.hahathonweb.service.SensorsService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "sensorsServlet", urlPatterns = "/sensors")
public class SensorsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SensorsService sensorsService = new SensorsService(
                new MeasurementIntDataDao(),
                new MeasurementNamesDao(),
                new SensorDao(),
                new TypeDao()
        );

        List<SensorDto> sensors = sensorsService.getAll();

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(sensors);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.close();
    }
}
