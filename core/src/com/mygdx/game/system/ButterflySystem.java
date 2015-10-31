package com.mygdx.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

/**
 * Created by Teatree on 9/3/2015.
 */
public class ButterflySystem extends IteratingSystem {

//    public void init(CompositeItem item) {
//            this.item = item;
//
//            pushUmbrella(310, 400, 45, 55);
//        }

    public ButterflySystem(Family family) {
        super(family);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
//        @Override
//        public void init(CompositeItem item) {
//            this.item = item;
//
//            pushUmbrella(310, 400, 45, 55);
//        }
//
//        @Override
//        public void dispose() {
//            item.dispose();
//        }
//
//        public void pushUmbrella(int randXmin, int randXmax, int randYmin, int randYmax) {
//            velocityX = ((random.nextInt(randXmax-randXmin)+randXmin)*-1)*speedIncrCoeficient;
////        gravity *= speedIncrCoeficient/2;
//            System.out.println("velocityX " + velocityX);
//            if(item.getY()> Gdx.graphics.getHeight()/2){
//                velocityY = (random.nextInt((randYmax-randYmin)+randYmin)*-1)*speedIncrCoeficient;
//            }else {
//                velocityY = (random.nextInt((randYmax - randYmin) + randYmin))*speedIncrCoeficient;
//            }
//            System.out.println("velocityY " + velocityY);
////        speedIncrCoeficient += 0.5f;
//            gravity = Math.abs(velocityX/(7-speedIncrCoeficient*gravityDecreaseMultiplier));
//            speedIncrCoeficient += 0.1f;
//            gravityDecreaseMultiplier -= 0.05f;
//            System.out.println("gravity " + gravity);
//        }
//
//        @Override
//        public void act(float delta) {
//            if(isGameAlive()) {
//                velocityX += gravity * delta;
//                item.setX(item.getX() + velocityX * delta);
//                item.setY(item.getY() + velocityY * delta);
//            }
//        }
//
//        public void updateRect() {
//            boundsRect.x = (int)item.getX();
//            boundsRect.y = (int)item.getY();
//            boundsRect.width = (int)item.getWidth();
//            boundsRect.height = (int)item.getHeight();
//        }
//
//        public Rectangle getBoundsRectangle() {
//            updateRect();
//            return boundsRect;
//        }
//
//        public CompositeItem getCompositeItem(){
//            return item;
//        }
//    }

        }

}
