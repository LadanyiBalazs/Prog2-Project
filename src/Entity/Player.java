package Entity;

import TileMap.*;

import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends MapObject {

    //player stuff
    private boolean dead;
    private Long lastDeadTime;
    private Color respawnColor;
    private Font respawnFont;

    private int respawnX;
    private int respawnY;

    // animations
    private ArrayList<BufferedImage[]> sprites;
    private final int[] numFrames = {
            1, 2, 1, 1
    };

    // animation actions
    private static final int IDLE = 0;
    private static final int WALKING = 1;
    private static final int JUMPING = 2;
    private static final int FALLING = 3;

    public Player(TileMap tm) {

        super(tm);

        respawnColor = new Color(128, 0, 0);
        respawnFont = new Font("Century Githic", Font.PLAIN, 28);

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

        respawnX = 0;
        respawnY = 0;
        dead = false;

        //load sprites
        try {

            BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream(
                    "/Sprites/Player/playersprites.gif"));
            sprites = new ArrayList<BufferedImage[]>();

        //animation
            for(int i = 0; i < 4; i++) {
                BufferedImage[] bi = new BufferedImage[numFrames[i]];
                for(int j = 0; j < numFrames[i]; j++){
                bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);
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

    }

    public void update() {

        //update position
        getNextPosition();
        checkTileMapCollision();
        if (ytemp <= cheight)
            ytemp = cheight;
        if (ytemp >= getTileHeight() - cheight) {
            ytemp = getTileHeight() - cheight;

            if (!dead)
                lastDeadTime = System.currentTimeMillis();
            dead = true;
        }

        if (dead) {
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

    private void respawnEvent() {
        setPosition(respawnX, respawnY);
        dead = false;
    }

    public void draw(Graphics2D g) {

        setMapPosition();

        //Death text
        if (dead) {

            g.setFont(respawnFont);
            g.setColor(respawnColor);
            g.drawString("You Died!", 100, 70);

            if (lastDeadTime + 3000 < System.currentTimeMillis())
                respawnEvent();
        }

        //draw player
        g.drawImage(animation.getImage(),
                (int) (x + xmap - width / 2),
                (int) (y + ymap -height / 2), null);
    }

    public void setRespawnPosition(int x, int y) { respawnX = x; respawnY = y; }

}
