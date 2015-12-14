package com.mygdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.stages.GameStage;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.Random;

public class CameraShaker {

  public float time;
  Random random = new Random();
  float x, y;
  float current_time;
  float power;
  float current_power;
  int blinkCounter;

  public void initShaking(float power, float time) {
    this.power = power;
    this.time = time;
    this.current_time = 0;
    this.blinkCounter = 7;
    LayerMapComponent lc = ComponentRetriever.get(GameScreenScript.background, LayerMapComponent.class);
    lc.getLayer("blink").isVisible = true;
  };
        
  public void shake(float delta){

    LayerMapComponent lc = ComponentRetriever.get(GameScreenScript.background, LayerMapComponent.class);

    this.blinkCounter--;
    if(current_time <= time) {
      if (this.blinkCounter == 0 ){
        lc.getLayer("blink").isVisible = !lc.getLayer("blink").isVisible;
        this.blinkCounter = 7;
      }
      current_power = power * ((time - current_time) / time);

      x = (random.nextFloat() - 0.5f) * 2.3f * current_power;
      y = (random.nextFloat() - 0.5f) * 0.3f * current_power;
      int signX = random.nextBoolean() ? 1 : -1;
      GameStage.viewport.getCamera().translate(signX * x, y, 0);
      current_time += delta;
    } else {
      // When the shaking is over move the camera back to the center
//      GameStage.viewport.getCamera().position.x = Gdx.graphics.getWidth()/2;
//      GameStage.viewport.getCamera().position.y = Gdx.graphics.getHeight()/2 + 2;
      lc.getLayer("blink").isVisible = false;
      GameStage.viewport.apply(true);
    }
  }      
}