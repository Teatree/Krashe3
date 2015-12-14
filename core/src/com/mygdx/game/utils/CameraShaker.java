package com.mygdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.stages.GameStage;
import java.util.Random;

public class CameraShaker {

  public float time;
  Random random = new Random();
  float x, y;
  float current_time;
  float power;
  float current_power;

  public void initShaking(float power, float time) {
    this.power = power;
    this.time = time;
    this.current_time = 0;
  };
        
  public void shake(float delta){
    if(current_time <= time) {
      current_power = power * ((time - current_time) / time);

      x = (random.nextFloat() - 0.5f) * 2 * current_power;
      y = (random.nextFloat() - 0.5f) * 0.3f * current_power;
      int signX = random.nextBoolean() ? 1 : -1;
      GameStage.viewport.getCamera().translate(signX * x, y, 0);
      current_time += delta;
    } else {
      // When the shaking is over move the camera back to the center
//      GameStage.viewport.getCamera().position.x = Gdx.graphics.getWidth()/2;
//      GameStage.viewport.getCamera().position.y = Gdx.graphics.getHeight()/2 + 2;
      GameStage.viewport.apply(true);
    }
  }      
}