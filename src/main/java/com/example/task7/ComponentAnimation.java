package com.example.task7;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ComponentAnimation implements Observer {
    private GraphicsContext graphicsContext;
    private int period = 20; // По умолчанию 20 секунд
    private int lastAnimatedTime = 0;
    private boolean isActive = false;
    private Timeline timeline;
    private DoubleProperty sunX = new SimpleDoubleProperty(0); // Позиция солнца по X
    private DoubleProperty sunY = new SimpleDoubleProperty(0); // Позиция солнца по Y
    private SimpleObjectProperty<Color> skyColor = new SimpleObjectProperty<>(Color.DEEPSKYBLUE); // Цвет неба
    private double sunRadius = 50; // Радиус солнца
    private boolean isSunset = true; // Флаг для определения текущей анимации (закат или восход)

    public ComponentAnimation(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    @Override
    public void update(Subject st) {
        if (isActive) {
            TimeServer timeServer = (TimeServer) st;
            if (timeServer.getState() - lastAnimatedTime >= period) {
                animate();
                lastAnimatedTime = timeServer.getState();
            }
        }
    }

    private void animate() {
        // Очистка холста
        graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());

        // Начальная позиция солнца
        sunX.set(isSunset ? 0 : graphicsContext.getCanvas().getWidth()); // Слева или справа
        sunY.set(graphicsContext.getCanvas().getHeight() / 2); // По центру по вертикали

        // Конечная позиция солнца
        double endX = isSunset ? graphicsContext.getCanvas().getWidth() : 0; // Справа или слева

        // Создание анимации перемещения солнца
        timeline = new Timeline();

        // Добавляем ключевые кадры для плавного перемещения солнца
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(20), new KeyValue(sunX, endX))
        );

        // Анимация цвета неба (переход от светлого к темному и обратно)
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(20), new KeyValue(skyColor, isSunset ? Color.DARKBLUE : Color.DEEPSKYBLUE))
        );

        timeline.setOnFinished(event -> {
            // После завершения анимации меняем направление (закат/восход)
            isSunset = !isSunset;
            animate(); // Запускаем анимацию снова
        });

        timeline.play();

        // Рисование солнца и неба на холсте
        drawSunset();
    }

    private void drawSunset() {
        // Установка цвета неба
        graphicsContext.setFill(skyColor.get());
        graphicsContext.fillRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());

        // Установка цвета заливки для солнца
        graphicsContext.setFill(Color.YELLOW);

        // Рисование солнца
        graphicsContext.fillOval(sunX.get() - sunRadius, sunY.get() - sunRadius, sunRadius * 2, sunRadius * 2);
    }

    public void start(int period) {
        this.period = period;
        isActive = true;
        animate(); // Запуск анимации сразу при старте
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
        isActive = false;
    }
}