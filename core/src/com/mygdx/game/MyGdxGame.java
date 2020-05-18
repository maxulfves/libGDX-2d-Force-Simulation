package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Texture bg;

	private OrthographicCamera cam;
	
	private Sprite circle;

	//private Apple apple;
	private List<Apple> apples = new ArrayList<Apple>();
	
	@Override
	public void create() {
		Gdx.graphics.setTitle("");
		Gdx.graphics.setWindowedMode(900, 900);

		center = new Vector(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		batch = new SpriteBatch();
		
		img = new Texture("apple.png");

		//apple = new Apple(new Texture("apple.png"), new Vector(center.x, center.y));
		for(int i = 0; i < 40; i ++) {
			float x = (float)Math.random() * 200;
			float y = (float)Math.random() * 200;
			apples.add(new Apple(img, new Vector(center.x - x, center.y - y)));
			
		}
		
		bg = new Texture("circle.png");
		
		circle = new Sprite(bg);

		// circle.setSize(spaceRadius*10, spaceRadius*10);
		circle.setSize(spaceRadius * 2, spaceRadius * 2);
		circle.setColor(new Color(0.25f, 1f, 0f, 0.7f));
		
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//System.out.println(cam.position);
		cam.position.set(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, 0);
		//cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		cam.update();
		
	}

	@Override
	public void render() {
		update();
		handleInput();
		
		batch.setProjectionMatrix(cam.combined);
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		batch.begin();
		
		circle.setPosition(center.x - spaceRadius, center.y - spaceRadius);
		circle.draw(batch);

		// apple.setPosition(pos.x - ballRadius, pos.y - ballRadius);
		for(Apple apple:apples) {
			apple.draw(batch);
		}

		batch.end();
	}

	private class Apple extends Sprite {
		float mass = 500f;
		Apple(Texture t, Vector pos) {
			super(t);
			setSize(ballRadius * 2, ballRadius * 2);
			this.pos = pos;
		}

		void update(Vector mouse) {
			Vector dif = mouse.sub(pos);


			pos = pos.add(vel);

			setPosition(pos.x - ballRadius, pos.y - ballRadius);
			
			float dist = dif.magnitude();

			if (dist < 100) {
				dist = 100;
			}

			Vector accMouse = dif.unit().mul(-mouseMass).div(dist * dist);
			vel = vel.add(accMouse);
			float disToCenter = pos.sub(center).magnitude();

			float wallDis = spaceRadius - 50 - disToCenter;
			Vector accWall = pos.sub(center).unit().mul(-5000/2).div(wallDis * wallDis);
			
			vel = vel.add(accWall);

			for(Apple apple:apples) {
				if(!apple.equals(this)) {
					Vector separation = this.pos.sub(apple.pos);
					float magn = separation.magnitude();
					Vector accApple = separation.unit().mul(apple.mass).div(magn * magn);
					//System.out.println(accApple.magnitude());
					if(magn > 4) {
						vel = vel.add(accApple);
					}else {
						//vel = vel.mul(-1);
						//apple.vel = apple.vel.mul(-1);
					}
					
					
				}
			}
			
			
			Vector friction = vel.mul(-1f).mul(0.02f);
			
			vel = vel.add(friction);

			if (vel.magnitude() > 40) {
				vel = vel.unit().mul(40);
				System.out.println(vel.magnitude());
				System.out.println("OVER!");
			}

		}

		Vector pos;
		Vector vel = new Vector(0, 0);
	}
	
	private float rotationSpeed = 0.5f;
	private void handleInput() {
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			cam.zoom += 0.02;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
			cam.zoom -= 0.02;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			cam.translate(-3, 0, 0);
			System.out.println(cam.view);
			
			
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			cam.translate(3, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			cam.translate(0, -3, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			cam.translate(0, 3, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			cam.rotate(-rotationSpeed, 0, 0, 1);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.E)) {
			cam.rotate(rotationSpeed, 0, 0, 1);
		}

		cam.zoom = MathUtils.clamp(cam.zoom, 0.1f, 100/cam.viewportWidth);

		float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
		float effectiveViewportHeight = cam.viewportHeight * cam.zoom;

		cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f, 100 - effectiveViewportWidth / 2f);
		cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f, 100 - effectiveViewportHeight / 2f);
	
		
	}

	private class Vector {
		float x = 0;
		float y = 0;

		Vector(float x, float y) {
			this.x = x;
			this.y = y;
		}

		float magnitude() {
			return (float) Math.sqrt(x * x + y * y);
		}

		Vector unit() {
			float len = magnitude();
			if (len == 0) {
				return new Vector(0, 0);
			} else {
				return new Vector(x / len, y / len);
			}
		}

		public Vector sub(Vector other) {
			return new Vector(this.x - other.x, this.y - other.y);
		}

		public Vector add(Vector other) {
			return new Vector(this.x + other.x, this.y + other.y);
		}

		public Vector mul(float factor) {
			return new Vector(this.x * factor, this.y * factor);
		}

		@Override
		public String toString() {
			return x + "/" + y + "";
		}

		public Vector div(float f) {
			return new Vector(this.x / f, this.y / f);
		}

	}

	Vector center;

	float mouseMass = 15000f;
	float ballRadius = 30f;
	float spaceRadius = 400f;

	private void update() {
		Vector mouse = new Vector(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

		for(Apple apple : apples) {
			apple.update(mouse);
		}
	}

	@Override
	public void dispose() {
		
		batch.dispose();
		img.dispose();
		bg.dispose();
	}
}
