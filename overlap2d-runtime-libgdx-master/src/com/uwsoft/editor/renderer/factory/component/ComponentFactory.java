/*
 * ******************************************************************************
 *  * Copyright 2015 See AUTHORS file.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *****************************************************************************
 */

package com.uwsoft.editor.renderer.factory.component;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.data.MainItemVO;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by azakhary on 5/22/2015.
 */
public abstract class ComponentFactory {

    protected IResourceRetriever rm;
    protected PooledEngine engine;

    protected ComponentMapper<NodeComponent> nodeComponentMapper;

    public ComponentFactory() {
        nodeComponentMapper = ComponentMapper.getFor(NodeComponent.class);
    }

    public ComponentFactory( IResourceRetriever rm) {
        this();
        injectDependencies( rm);
    }

    public ComponentFactory(PooledEngine engine, IResourceRetriever rm) {
        this();
        this.engine = engine;
        injectDependencies( rm);
    }

    public void injectDependencies( IResourceRetriever rm) {
        this.rm = rm;
    }

    public abstract void createComponents(Entity root, Entity entity, MainItemVO vo);

    protected void createCommonComponents(Entity entity, MainItemVO vo, int entityType) {
        DimensionsComponent dimensionsComponent = createDimensionsComponent(entity, vo);
        createMainItemComponent(entity, vo, entityType);
        createTransformComponent(entity, vo, dimensionsComponent);
        createTintComponent(entity, vo);
        createZIndexComponent(entity, vo);
        createScriptComponent(entity, vo);
    }

	protected MainItemComponent createMainItemComponent(Entity entity, MainItemVO vo, int entityType) {
        MainItemComponent component = engine.createComponent(MainItemComponent.class);
        component.customVars = vo.customVars;
        component.uniqueId = vo.uniqueId;
        component.itemIdentifier = vo.itemIdentifier;
        component.libraryLink = vo.itemName;
        if(vo.tags != null) {
            component.tags = new HashSet<String>(Arrays.asList(vo.tags));
        }
        component.entityType = entityType;

        entity.add(component);

        return component;
    }

    protected TransformComponent createTransformComponent(Entity entity, MainItemVO vo, DimensionsComponent dimensionsComponent) {
        TransformComponent component = engine.createComponent(TransformComponent.class);
        component.rotation = vo.rotation;
        component.scaleX = vo.scaleX;
        component.scaleY = vo.scaleY;
        component.x = vo.x;
        component.y = vo.y;

        if(Float.isNaN(vo.originX)) component.originX = dimensionsComponent.width/2f;
        else component.originX = vo.originX;

        if(Float.isNaN(vo.originY)) component.originY = dimensionsComponent.height/2f;
        else component.originY = vo.originY;

        entity.add(component);

        return component;
    }

    protected abstract DimensionsComponent createDimensionsComponent(Entity entity, MainItemVO vo);

    protected TintComponent createTintComponent(Entity entity, MainItemVO vo) {
        TintComponent component = engine.createComponent(TintComponent.class);
        component.color.set(vo.tint[0], vo.tint[1], vo.tint[2], vo.tint[3]);

        entity.add(component);

        return component;
    }

    protected ZIndexComponent createZIndexComponent(Entity entity, MainItemVO vo) {
        ZIndexComponent component = engine.createComponent(ZIndexComponent.class);

        if(vo.layerName == "" || vo.layerName == null) vo.layerName = "Default";

        component.layerName = vo.layerName;
        component.setZIndex(vo.zIndex);
        component.needReOrder = false;
        entity.add(component);

        return component;
    }

    protected ScriptComponent createScriptComponent(Entity entity, MainItemVO vo) {
        ScriptComponent component = engine.createComponent(ScriptComponent.class);
        entity.add(component);
        return component;
    }

    protected ParentNodeComponent createParentNodeComponent(Entity root, Entity entity) {
        ParentNodeComponent component = engine.createComponent(ParentNodeComponent.class);
        component.parentEntity = root;
        entity.add(component);

        //set visible to true depending on parent
        // TODO: I do not likes this part
        MainItemComponent mainItemComponent = ComponentRetriever.get(entity, MainItemComponent.class);
        LayerMapComponent layerMapComponent = ComponentRetriever.get(root, LayerMapComponent.class);
        ZIndexComponent zIndexComponent = ComponentRetriever.get(root, ZIndexComponent.class);
        mainItemComponent.visible = layerMapComponent.isVisible(zIndexComponent.layerName);

        return component;
    }

    protected void createNodeComponent(Entity root, Entity entity) {
        NodeComponent component = nodeComponentMapper.get(root);
//        component.children.add(entity);
        component.addChild(entity);
    }

//    protected PolygonComponent createMeshComponent(Entity entity, MainItemVO vo) {
//        PolygonComponent component = new PolygonComponent();
//        if(vo.shape != null) {
//            component.vertices = vo.shape.polygons.clone();
//            entity.add(component);
//
//            return component;
//        }
//        return null;
//    }

    public void setResourceManager(IResourceRetriever rm) {
        this.rm = rm;
    }

}
