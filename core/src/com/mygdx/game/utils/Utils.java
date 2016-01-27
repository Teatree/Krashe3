package com.mygdx.game.utils;

import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;

import java.text.SimpleDateFormat;

public class Utils {

    public static SimpleDateFormat getDateFormat(){
        return new SimpleDateFormat("MM/dd/yyyy");
    }

    public static void fadeChildren(NodeComponent nc, int fadeCoefficient) {
        if (nc != null && nc.children != null && nc.children.size != 0) {
            for (Entity e : nc.children) {
                TintComponent tc = e.getComponent(TintComponent.class);
                tc.color.a += fadeCoefficient * 0.1f;
                fadeChildren(e.getComponent(NodeComponent.class), fadeCoefficient);
            }
        }
    }

    public static void fade(Entity entity, boolean isPause) {
        NodeComponent nc = entity.getComponent(NodeComponent.class);
        TintComponent tcp = entity.getComponent(TintComponent.class);

        boolean appear = (tcp.color.a < 1 && isPause) ||
                (tcp.color.a > 0 && !isPause);

        int fadeCoefficient = isPause ? 1 : -1;

        if (appear) {
            tcp.color.a += fadeCoefficient * 0.1f;
            fadeChildren(nc, fadeCoefficient);
        }

        if (!isPause && tcp.color.a <= 0) {
            TransformComponent dialogTc = entity.getComponent(TransformComponent.class);
            dialogTc.x = -1000;
        }
    }
}
