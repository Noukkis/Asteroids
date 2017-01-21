/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroids;

import helpers.Geometry;
import java.util.ArrayList;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;

/**
 *
 * @author Jordan
 */
public class Asteroid extends Polygon {

    private double moveX;
    private double moveY;
    private double VITESSE_ROTATION;
    private int radius;

    public Asteroid(int r) {
        super(Geometry.AsteroidGen(r));
        this.radius = r;
        this.VITESSE_ROTATION = Geometry.random(-1.1, 1);
        super.setFill(Color.BLACK);
        super.setStroke(Color.WHITE);
        super.setStrokeWidth(5);
        this.moveX = Geometry.random(-4.2, 4.2);
        this.moveY = Geometry.random(-4.2, 4.2);
    }

    public void move() {
        super.setLayoutX(super.getLayoutX() + moveX);
        super.setLayoutY(super.getLayoutY() + moveY);
        super.setRotate(super.getRotate() + VITESSE_ROTATION);
    }

    public int explose(ArrayList lost, ArrayList<Asteroid> news, Pane pane) {
        lost.add(this);
        if (radius > 40) {
            Asteroid a = new Asteroid((int) (radius / 1.5));
            Asteroid b = new Asteroid((int) (radius / 1.5));
            a.setLayoutX(super.getLayoutX());
            a.setLayoutY(super.getLayoutY());
            b.setLayoutX(super.getLayoutX());
            b.setLayoutY(super.getLayoutY());
            news.add(a);
            news.add(b);
        }
        return (radius >= 100) ? 10 : 110 - radius;

    }
}
