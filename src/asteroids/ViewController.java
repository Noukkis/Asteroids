/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroids;

import helpers.Geometry;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Jordan
 */
public class ViewController implements Initializable {

    @FXML
    private Label lblScore;
    @FXML
    private Pane pane;
    @FXML
    private Polygon ship;

    private final double PUISSANCE = 0.05;
    private final int MARGE = 50;
    private final int VITESSE_MAX = 5;

    private double mouseX;
    private double mouseY;

    private boolean playable;
    
    private ArrayList<Bullet> bullets;
    private ArrayList<Asteroid> asteroids;
    private ArrayList<Asteroid> news;
    ArrayList lost;

    private double moveX;
    private double moveY;
    private boolean thrusting;

    private int score;
    @FXML
    private Label lblHighScore;
    @FXML
    private Text txtFinalScore;

    @FXML
    private void onKeyPressed(KeyEvent event) {
        if (playable) {
            switch (event.getCode().getName()) {
                case "Space":
                    thrusting = true;
            }
        } else if (!thrusting){
            playable = true;
            pane.getChildren().add(ship);
            txtFinalScore.setText("");
        }
    }

    @FXML
    private void clicked(MouseEvent event) {
        if (playable) {
            Bullet bullet = new Bullet(ship);
            bullets.add(bullet);
            pane.getChildren().add(bullet);
        }
    }

    @FXML
    private void onDrag(MouseEvent event) {
        mouseX = event.getX();
        mouseY = event.getY();
    }

    @FXML
    private void onKeyReleased(KeyEvent event) {
        switch (event.getCode().getName()) {
            case "Space":
                thrusting = false;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        playable = true;
        score = 0;
        lost = new ArrayList();
        bullets = new ArrayList<>();
        asteroids = new ArrayList<>();
        news = new ArrayList<>();
        thrusting = false;
        mouseX = 0;
        mouseY = 0;
        Timeline timeLinePong = new Timeline(new KeyFrame(Duration.millis(10), (ActionEvent) -> actualize()));
        timeLinePong.setCycleCount(Timeline.INDEFINITE);
        timeLinePong.play();
    }

    private void actualize() {
        if (mouseX != Geometry.getCenterX(ship) || mouseY != Geometry.getCenterY(ship)) {
            ship.setRotate(360 - Math.toDegrees(Math.atan2(ship.getLayoutX() - mouseX, ship.getLayoutY() - mouseY)));
        }

        if (thrusting && playable) {
            double[] vecteur = Geometry.polar2Cartesian(180 - ship.getRotate(), PUISSANCE);
            moveX += vecteur[0];
            moveY += vecteur[1];
        }
        if (moveX > VITESSE_MAX) {
            moveX = VITESSE_MAX;
        }
        if (moveY > VITESSE_MAX) {
            moveY = VITESSE_MAX;
        }
        ship.setLayoutX(ship.getLayoutX() + moveX);
        ship.setLayoutY(ship.getLayoutY() + moveY);

        posOnScreen(ship);

        for (Bullet bullet : bullets) {
            if (!bullet.move(pane)) {
                lost.add(bullet);
            }
        }
        pane.getChildren().removeAll(lost);
        bullets.removeAll(lost);
        asteroids.removeAll(lost);
        lost.clear();

        for (Asteroid asteroid : asteroids) {
            asteroid.move();
            posOnScreen(asteroid);
        }

        if (Geometry.random(0, (asteroids.size() + 1) * 42) == 5 && playable) {
            for (int i = 0; i < 1; i++) {
                Asteroid asteroid = new Asteroid(Geometry.random(20, 100));
                asteroids.add(asteroid);
                pane.getChildren().add(asteroid);
            }
        }

        for (Asteroid asteroid : asteroids) {
            testExplose(asteroid);
        }
        asteroids.addAll(news);
        pane.getChildren().addAll(news);
        news.clear();

        for (Asteroid asteroid : asteroids) {
            if (asteroid.getBoundsInParent().intersects(ship.getBoundsInParent())) {
                stop();
            }
        }
    }

    public void initialize() {
        ship.setLayoutX(Geometry.getCenterX(pane) - Geometry.getCenterX(ship));
        ship.setLayoutY(Geometry.getCenterY(pane) - Geometry.getCenterY(ship));
        moveX = 0;
        moveY = 0;
        if(Integer.parseInt(lblHighScore.getText()) < score){
            lblHighScore.setText(Integer.toString(score));
        }
        score = 0;
        lblScore.setText("0");
    }

    public boolean isThrusting() {
        return thrusting;
    }

    private void posOnScreen(Node node) {
        if (node.getLayoutX() > pane.getWidth() + MARGE) {
            node.setLayoutX(-MARGE);
        } else if (node.getLayoutX() < -MARGE) {
            node.setLayoutX(pane.getWidth() + MARGE);
        }

        if (node.getLayoutY() > pane.getHeight() + MARGE) {
            node.setLayoutY(-MARGE);
        } else if (node.getLayoutY() < -MARGE) {
            node.setLayoutY(pane.getHeight() + MARGE);
        }
    }

    private void testExplose(Asteroid asteroid) {
        for (Bullet bullet : bullets) {
            if (asteroid.getBoundsInParent().intersects(bullet.getBoundsInParent())) {
                score += asteroid.explose(lost, news, pane);
                lblScore.setText(Integer.toString(score));
                lost.add(bullet);
            }
        }
    }

    public void stop() {
        lost.addAll(pane.getChildren());
        lost.remove(txtFinalScore);
        playable = false;
        txtFinalScore.setLayoutX(Geometry.getCenterX(pane) - Geometry.getCenterX(txtFinalScore));
        txtFinalScore.setLayoutY(Geometry.getCenterY(pane) - Geometry.getCenterY(txtFinalScore));
        txtFinalScore.setText(score + " pts");
        initialize();
    }
}
