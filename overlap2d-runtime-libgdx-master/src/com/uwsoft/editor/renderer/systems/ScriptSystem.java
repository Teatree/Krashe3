package com.uwsoft.editor.renderer.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.ScriptComponent;
import com.uwsoft.editor.renderer.scripts.IScript;

import javax.activation.MailcapCommandMap;

/**
 * Created by azakhary on 6/19/2015.
 */
public class ScriptSystem extends IteratingSystem {

    private ComponentMapper<ScriptComponent> scriptComponentComponentMapper = ComponentMapper.getFor(ScriptComponent.class);

    public ScriptSystem() {
        super(Family.all(ScriptComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
//        try {
            if(scriptComponentComponentMapper.get(entity) == null){
                scriptComponentComponentMapper = ComponentMapper.getFor(ScriptComponent.class);
            }else {
                for (IScript script : scriptComponentComponentMapper.get(entity).scripts) {
                    script.act(deltaTime);
                }
            }
//        }catch (Throwable throwable){
//            System.err.print(throwable);
//            System.err.print(throwable.getMessage());
//            System.err.println(entity.getComponent(MainItemComponent.class).itemIdentifier);
//        }
    }
}
