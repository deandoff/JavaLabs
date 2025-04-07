package lab6;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ShapesApp {
    static int shapeCount = 0;

    public static void main(String[] args) {
        new ShapesController();
    }
}

class ShapesController implements ActionListener, ItemListener, Observer {
    private Frame controlFrame;
    private Frame displayFrame;
    private DisplayPanel displayPanel;
    private List<MovingShape> shapes = new ArrayList<>();

    private Button startButton;
    private Choice colorChoice;
    private TextField shapeField;
    private Choice speedChoice;
    private Choice selectShapeChoice;
    private TextField speedField;
    private Button changeSpeedButton;

    public ShapesController() {
        createControlFrame();
        createDisplayFrame();
    }

    private void createControlFrame() {
        controlFrame = new Frame("Управляющее окно");
        controlFrame.setLayout(new GridLayout(7, 2));
        controlFrame.setSize(400, 300);

        controlFrame.add(new Label("Тип фигуры (введите):"));
        shapeField = new TextField();
        controlFrame.add(shapeField);

        controlFrame.add(new Label("Цвет:"));
        colorChoice = new Choice();
        colorChoice.add("Синий");
        colorChoice.add("Зелёный");
        colorChoice.add("Красный");
        colorChoice.add("Чёрный");
        colorChoice.add("Жёлтый");
        colorChoice.add("Розовый");
        controlFrame.add(colorChoice);

        controlFrame.add(new Label("Начальная скорость:"));
        speedChoice = new Choice();
        speedChoice.add("1 (медленно)");
        speedChoice.add("2");
        speedChoice.add("3");
        speedChoice.add("4");
        speedChoice.add("5");
        speedChoice.add("6 (быстро)");
        controlFrame.add(speedChoice);

        startButton = new Button("Пуск");
        startButton.addActionListener(this);
        controlFrame.add(startButton);

        controlFrame.add(new Label());

        controlFrame.add(new Label("Выбрать фигуру:"));
        selectShapeChoice = new Choice();
        selectShapeChoice.addItemListener(this);
        controlFrame.add(selectShapeChoice);

        controlFrame.add(new Label("Новая скорость (1-6):"));
        speedField = new TextField();
        controlFrame.add(speedField);

        changeSpeedButton = new Button("Изменить скорость");
        changeSpeedButton.addActionListener(this);
        controlFrame.add(changeSpeedButton);


        controlFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        controlFrame.setVisible(true);
    }

    private void createDisplayFrame() {
        displayFrame = new Frame("Демонстрационное окно");
        displayFrame.setSize(600, 400);

        displayPanel = new DisplayPanel();
        displayFrame.add(displayPanel);

        displayFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension newSize = displayPanel.getSize();
                for (MovingShape shape : shapes) {
                    shape.updateBounds(newSize);
                }
                displayPanel.repaint();
            }
        });

        displayFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        displayFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            Color color = getSelectedColor();
            String shapeType = shapeField.getText().trim();
            int speed = speedChoice.getSelectedIndex() + 1;

            if (shapeType.isEmpty()) {
                shapeType = "Круг";
            }

            ShapesApp.shapeCount++;
            MovingShape shape = new MovingShape(ShapesApp.shapeCount, shapeType, color, speed, displayPanel.getSize());
            shape.addObserver(this);
            shapes.add(shape);

            selectShapeChoice.add(shape.getId() + ": " + shapeType);

            displayPanel.repaint();
        } else if (e.getSource() == changeSpeedButton) {
            changeSelectedShapeSpeed();
        }
    }

    private void changeSelectedShapeSpeed() {
        String selected = selectShapeChoice.getSelectedItem();
        if (selected != null && !selected.isEmpty()) {
            int shapeId = Integer.parseInt(selected.split(":")[0]);
            String newSpeedText = speedField.getText();

            try {
                int newSpeed = Integer.parseInt(newSpeedText);
                if (newSpeed > 0 && newSpeed <= 6) {
                    for (MovingShape shape : shapes) {
                        if (shape.getId() == shapeId) {
                            shape.setSpeed(newSpeed);
                            break;
                        }
                    }
                }
            } catch (NumberFormatException ex) {
                System.out.println("Ошибка: введите число от 1 до 6");
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {}

    @Override
    public void update(Observable o, Object arg) {
        displayPanel.repaint();
    }

    private Color getSelectedColor() {
        switch (colorChoice.getSelectedIndex()) {
            case 0: return Color.BLUE;
            case 1: return Color.GREEN;
            case 2: return Color.RED;
            case 3: return Color.BLACK;
            case 4: return Color.YELLOW;
            case 5: return Color.PINK;
            default: return Color.BLUE;
        }
    }

    class DisplayPanel extends Panel {
        private Image buffer;
        private Graphics bufferGraphics;

        @Override
        public void update(Graphics g) {
            paint(g);
        }

        @Override
        public void paint(Graphics g) {
            if (buffer == null || buffer.getWidth(this) != getWidth() || buffer.getHeight(this) != getHeight()) {
                buffer = createImage(getWidth(), getHeight());
                bufferGraphics = buffer.getGraphics();
            }

            bufferGraphics.setColor(getBackground());
            bufferGraphics.fillRect(0, 0, getWidth(), getHeight());

            for (MovingShape shape : shapes) {
                shape.draw(bufferGraphics);
            }

            g.drawImage(buffer, 0, 0, this);

            for (MovingShape shape : shapes) {
                System.out.printf("ID: %d | Тип: %s | Скорость: %d | Координаты: (%d, %d)%n",
                        shape.getId(),
                        shape.getType(),
                        shape.getSpeed(),
                        shape.getX(),
                        shape.getY());
            }
        }
    }
}

class MovingShape extends Observable implements Runnable {
    private final int id;
    private final String type;
    private Color color;
    private volatile int speed;
    private volatile Dimension bounds;

    private int x, y;
    private int dx, dy;
    private Thread thread;
    private boolean running = true;

    public MovingShape(int id, String type, Color color, int speed, Dimension bounds) {
        this.id = id;
        this.type = type;
        this.color = color;
        this.speed = speed;
        this.bounds = bounds;

        this.x = 10;
        this.y = 30;

        Random rand = new Random();
        this.dx = rand.nextInt(3) + 1;
        this.dy = rand.nextInt(3) + 1;

        if (rand.nextBoolean()) dx = -dx;
        if (rand.nextBoolean()) dy = -dy;

        this.thread = new Thread(this);
        this.thread.start();
    }

    public int getId() {
        return id;
    }

    public synchronized void setSpeed(int speed) {
        this.speed = speed;
    }

    public synchronized void updateBounds(Dimension newBounds) {
        this.bounds = newBounds;

        if (x >= bounds.width - 30) x = bounds.width - 40;
        if (y >= bounds.height - 30) y = bounds.height - 40;
    }

    @Override
    public void run() {
        while (running) {
            move();
            setChanged();
            notifyObservers();

            try {
                Thread.sleep(100 / speed);  // Чем больше speed, тем меньше задержка
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    private synchronized void move() {
        x += dx;
        y += dy;

        if (x <= 0) {
            x = 0;
            dx = Math.abs(dx);
        } else if (x >= bounds.width - 30) {
            x = bounds.width - 30;
            dx = -Math.abs(dx);
        }

        if (y <= 0) {
            y = 0;
            dy = Math.abs(dy);
        } else if (y >= bounds.height - 30) {
            y = bounds.height - 30;
            dy = -Math.abs(dy);
        }
    }

    public void draw(Graphics g) {
        g.setColor(color);

        switch (type.toLowerCase()) {
            case "круг":
                g.fillOval(x, y, 30, 30);
                break;
            case "овал":
                g.fillOval(x, y, 40, 30);
                break;
            case "квадрат":
                g.fillRect(x, y, 30, 30);
                break;
            case "прямоугольник":
                g.fillRect(x, y, 40, 30);
                break;
            case "треугольник":
                int[] xPoints = {x + 15, x, x + 30};
                int[] yPoints = {y, y + 30, y + 30};
                g.fillPolygon(xPoints, yPoints, 3);
                break;
            default:  // По умолчанию рисуем круг
                g.fillOval(x, y, 30, 30);
                break;
        }

        g.setColor(Color.BLACK);
        g.drawString(String.valueOf(id), x + 10, y + 15);
    }

    public Object getType() {
        return type;
    }


    public Object getSpeed() {
        return speed;
    }

    public Object getX() {
        return x;
    }

    public Object getY() {
        return y;
    }
}