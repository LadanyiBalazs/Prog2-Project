package Entity;

import TileMap.*;

import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends MapObject {

    //player stuff
    private boolean dead;

    //scratch
    private boolean scratching;
    private int scratchRange;

    private int respawnX;
    private int respawnY;

    // animations
    private ArrayList<BufferedImage[]> sprites;
    private final int[] numFrames = {
            1, 2, 1, 1, 2
    };

    // animation actions
    private static final int IDLE = 0;
    private static final int WALKING = 1;
    private static final int JUMPING = 2;
    private static final int FALLING = 3;
    private static final int SCRATCHING = 4;

    public Player(TileMap tm) {

        super(tm);

        width = 30;
        height = 30;
        cwidth = 16;
        cheight = 20;

        moveSpeed = 0.3;
        maxSpeed = 1.6;
        stopSpeed = 0.4;
        fallSpeed = 0.15;
        maxFallSpeed = 4.0;
        jumpStart = -4.8;
        stopJumpSpeed = 0.3;

        scratchRange = 40;

        respawnX = 0;
        respawnY = 0;
        dead = false;

        //load sprites
        try {

            BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream(
                    "/Sprites/Player/playersprites.gif"));
            sprites = new ArrayList<BufferedImage[]>();

        //animation
            for(int i = 0; i < 5; i++) {
                BufferedImage[] bi = new BufferedImage[numFrames[i]];
                for(int j = 0; j < numFrames[i]; j++){

                    if(i != 5) {
                        bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);
                    }
                    else {
                        bi[j] = spritesheet.getSubimage(j * width * 2, i * height, width, height);
                    }
                }
                sprites.add(bi);
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }

        animation = new Animation();
        currentAction = IDLE;
        animation.setFrames(sprites.get(IDLE));
        animation.setDelay(-1);

    }

    public void setScratching() {
        scratching = true;
    }

    private void getNextPosition() {

        //movement
        if (left) {
            dx -= moveSpeed;
            if (dx < -maxSpeed) {
                dx = -maxSpeed;
            }
        }
        else if (right) {
            dx += moveSpeed;
            if (dx > maxSpeed) {
                dx = maxSpeed;
            }
        }
        else {
            if (dx > 0) {
                dx -= stopSpeed;
                if (dy < 0) {
                    dx = 0;
                }
            }
            else if (dx < 0) {
                dx += stopSpeed;
                if (dx > 0) {
                    dx = 0;
                }
            }
        }

        //jumping
        if (jumping && !falling) {
            dy = jumpStart;
            falling = true;
        }

        //falling
        if (falling) {
            if (dy > 0)
                dy += fallSpeed;

            if (dy > 0) jumping = false;
            if (dy < 0 && !jumping) dy += stopJumpSpeed;

            if (dy > maxFallSpeed) dy = maxFallSpeed;

        }

        //cannot move while attacking
        if((currentAction == SCRATCHING) && !(jumping || falling)){
            dx = 0;
        }

    }

    public void update() {

        //update position
        getNextPosition();
        checkTileMapCollision();
        if (ytemp <= cheight)
            ytemp = cheight;
        if (ytemp >= getTileHeight() - cheight) {
            ytemp = getTileHeight() - cheight;

            dead = true;
        }

        if (dead) {
            setPosition(respawnX, respawnY);
            dead = false;
            return;
        }

        setPosition(xtemp, ytemp);

        //set animation
        if(dy > 0) {
            if(currentAction != FALLING) {
                currentAction = FALLING;
                animation.setFrames(sprites.get(FALLING));
                animation.setDelay(-1);
                width = 30;
            }
        }
        else if (dy < 0) {
            if (currentAction != JUMPING) {
                currentAction = JUMPING;
                animation.setFrames(sprites.get(JUMPING));
                animation.setDelay(-1);
                width = 30;
            }
        }
        else if (scratching) {
            if (currentAction != SCRATCHING) {
                currentAction = SCRATCHING;
                animation.setFrames(sprites.get(SCRATCHING));
                animation.setDelay(50);
                width = 60;
            }
        }
        else if (left || right) {
            if (currentAction != WALKING) {
                currentAction = WALKING;
                animation.setFrames(sprites.get(WALKING));
                animation.setDelay(50);
                width = 30;
            }
        }
        else {
            if (currentAction != IDLE) {
                currentAction = IDLE;
                animation.setFrames(sprites.get(IDLE));
                animation.setDelay(-1);
                width = 30;
            }
        }
        animation.update();

    }

    public void draw(Graphics2D g) {

        setMapPosition();

        //draw player
        g.drawImage(animation.getImage(),
                (int) (x + xmap - width / 2),
                (int) (y + ymap -height / 2), null);
    }

    public void setRespawnPosition(int x, int y) { respawnX = x; respawnY = y; }

}
