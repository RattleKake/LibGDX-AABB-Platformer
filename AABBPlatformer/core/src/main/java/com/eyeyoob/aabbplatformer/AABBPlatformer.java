package com.eyeyoob.aabbplatformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;


class Wall {
    public float x, y;
    public float width, height;
    public Rectangle rect;

    public Wall(float x, float y) {
        // Set values
        this.x = x;
        this.y = y;
        this.width = 32;
        this.height = 32;
        this.rect = new Rectangle(this.x, this.y, this.width, this.height);
    }

    public void draw(ShapeRenderer shapeRenderer) {
        // Draw the wall as a gray rectangle
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }
}


class Player {
    // Initialize variables
    AABBPlatformer world;
    public float x, y;
    float width, height;
    public Rectangle rect;
    public float velocityX = 0;
    public float velocityY = 0;

    float moveSpeed = 3f;
    float jumpPower = 10f;
    float fallSpeed = 0.5f;
    boolean isGrounded = false;
    boolean canPressJump = false;

    public Player(AABBPlatformer world, float x, float y) {
        // Set values
        this.world = world;
        this.x = x;
        this.y = y;
        this.width = 32;
        this.height = 32;
        this.rect = new Rectangle(this.x, this.y, this.width, this.height);
    }

    private boolean overlapOffset(Rectangle otherRect, float xOffset, float yOffset) {
        return rect.x + xOffset < otherRect.x + otherRect.width &&
            rect.x + rect.width + xOffset > otherRect.x &&
            rect.y + yOffset < otherRect.y + otherRect.height &&
            rect.y + rect.height + yOffset > otherRect.y;
    }

    public void update() {
        // Inputs
        boolean inputLeft = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean inputRight = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean inputJump = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        float inputHorizontal = ((inputRight ? 1 : 0) - (inputLeft ? 1 : 0));

        // Horizontal movement
        velocityX = moveSpeed * inputHorizontal;

        // Jumping
        if (isGrounded && inputJump && canPressJump) {
            velocityY = jumpPower;
            isGrounded = false;
            canPressJump = false;
        }

        if (isGrounded && !inputJump) {canPressJump = true;}

        // Falling
        if (!isGrounded) {
            velocityY -= fallSpeed;
        }

        // Collision with wall objects
        boolean groundedThisFrame = false;
        for (Wall wall : world.walls) {

            // Horizontal collision
            if (overlapOffset(wall.rect, velocityX, 0)) {
                // Move as close as the player can
                if (velocityX > 0) {
                    rect.x = wall.rect.x - rect.width;
                } else if (velocityX < 0) {
                    rect.x = wall.rect.x + wall.rect.width;
                }

                // Don't let the player move horizontally
                velocityX = 0;
            }

            // Vertical collision
            if (overlapOffset(wall.rect, 0, velocityY)) {
                // Move down as close as we can
                if (velocityY > 0) {
                    rect.y = wall.rect.y - rect.height;
                }
                // Move up as close as we can
                else if (velocityY < 0) {
                    rect.y = wall.rect.y + wall.rect.height;
                    groundedThisFrame = true;
                }

                // Don't let the player move vertically
                velocityY = 0;
            }
        }

        // Update isGrounded variables
        isGrounded = groundedThisFrame;

        System.out.println(isGrounded);

        // Update X and Y position
        rect.x += velocityX;
        rect.y += velocityY;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        // Draw the player as a green rectangle
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

}


public class AABBPlatformer extends ApplicationAdapter {
    // Initialize variables
    Array<Wall> walls;
    Player player;
    ShapeRenderer shapeRenderer;
    String[] map = new String[] {
        "XXXXXXXXXXXXXXXXXXXX",
        "XOOOOOOOOOOOOOOOOOOX",
        "XOOOOOOOOOOOOOOOOOOX",
        "XOOOOOOOOOOOOOOOOOOX",
        "XOOOOOOOOOOOOOOOOOOX",
        "XOOOOOOOOOOOOOOOOOOX",
        "XOOOOOOOOOOOOOOOOOOX",
        "XOOOOOOOOOOOOOOOOOOX",
        "XOOOOOOOOOOOOOOOOOOX",
        "XOOOOOOOOOOOOOOOOOOX",
        "XOOOOOOOOOOOOOOOOOOX",
        "XOOOOOOOOOOOOOOOOOOX",
        "XOOOOOOOOXOOOOOOOOOX",
        "XOOOOXOO0XOOOXOOOOOX",
        "XXXXXXXXXXXXXXXXXXXX"
    };


    @Override
    public void create() {
        // Set values
        walls = new Array<>();
        player = new Player(this, 64, 64);
        shapeRenderer = new ShapeRenderer();
        int mapWidth = map[0].length() * 32; // Any index will do
        int mapHeight = map.length * 32;

        System.out.println(mapWidth);


        // Add walls to the world
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length(); j++) {
                if (map[i].charAt(j) == 'X') {walls.add(new Wall(j * 32, mapHeight - 32 - (i * 32)));}
            }
        }
    }

    @Override
    public void render() {

        // Set clear color
        ScreenUtils.clear(Color.BLACK);

        // Update player
        player.update();

        // Draw everything
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw all walls
        for (Wall wall : walls) {
            wall.draw(shapeRenderer);
        }

        // Draw the player
        player.draw(shapeRenderer);



        shapeRenderer.end();

    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
