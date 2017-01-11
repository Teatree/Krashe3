package com.fd.etf.utils;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.ParentNodeComponent;
import com.uwsoft.editor.renderer.components.ScriptComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.CompositeVO;
import com.uwsoft.editor.renderer.data.ProjectInfoVO;
import com.uwsoft.editor.renderer.data.SceneVO;
import com.uwsoft.editor.renderer.factory.EntityFactory;
import com.uwsoft.editor.renderer.resources.ETFResourceManager;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.systems.*;
import com.uwsoft.editor.renderer.systems.action.ActionSystem;
import com.uwsoft.editor.renderer.systems.render.Overlap2dRenderer;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.HashMap;
import java.util.Map;

public class ETFSceneLoader {

    private static final String RESULT_SCENE = "ResultScene";
    private static final String SHOP_SCENE = "ShopScene";
    public static final String MAIN_SCENE = "MainScene";
    public static final String MENU_SCENE = "MenuScene";

    public Entity rootEntity;
    public SceneVO sceneVO;
    public PooledEngine engine = null;

    public EntityFactory entityFactory;
    public Overlap2dRenderer renderer;
    public ETFResourceManager rm = null;
    private float pixelsPerWU = 1;

    public Map<String, PooledEngine> engineByScene = new HashMap<>();
    public Map<String, Entity> rootEntityByScene = new HashMap<>();


    public ETFSceneLoader(Viewport viewport) {
        this.rm = new ETFResourceManager();
        rm.initAllResources();

        this.engine = new PooledEngine();
        for (String sceneName : rm.loadedSceneVOs.keySet()) {
            if ((sceneName.equals(MENU_SCENE))) {
                loadScene(sceneName, viewport);
            }
        }
        System.gc();
        System.runFinalization();
    }

    /**
     * this method is called when rm has loaded all data
     */
    public void initSceneLoader() {
        addSystems();
        if (entityFactory == null) {
            entityFactory = new EntityFactory(engine, rm);
        }
    }


    public void unLoadScene(String sceneName) {
        engineByScene.remove(sceneName);
        rootEntityByScene.remove(sceneName);
        System.gc();
        System.runFinalization();
    }

    public SceneVO loadScene(String sceneName, Viewport viewport) {

        this.engine = engineByScene.get(sceneName);
        if (engine == null) {
            this.engine = new PooledEngine();
        } else {
            engine.removeEntity(rootEntity);
        }
        this.rootEntity = null;

        initSceneLoader();

        if (entityFactory == null) {
            entityFactory = new EntityFactory(engine, rm);
        }

        pixelsPerWU = rm.getProjectVO().pixelToWorld;

        sceneVO = rm.getSceneVO(sceneName);
        if (sceneVO.composite == null) {
            sceneVO.composite = new CompositeVO();
        }

        rootEntity = entityFactory.createRootEntity(sceneVO.composite, viewport);
        this.engine.addEntity(rootEntity);

        if (sceneVO.composite != null) {
            entityFactory.initAllChildren(engine, rootEntity, sceneVO.composite);
        }

        rootEntityByScene.put(sceneName, rootEntity);
        engineByScene.put(sceneName, engine);

        System.gc();
        System.runFinalization();

        return sceneVO;
    }

    public void setScene(String sceneName, Viewport viewport) {
        if (rootEntityByScene.get(sceneName) == null) {
            loadScene(sceneName, viewport);
            this.rootEntity = rootEntityByScene.get(sceneName);
            this.engine = engineByScene.get(sceneName);
        } else {
            this.rootEntity = rootEntityByScene.get(sceneName);
            this.engine = engineByScene.get(sceneName);
        }
        System.gc();
    }

    private void addSystems() {
        renderer = new Overlap2dRenderer(new SpriteBatch());
        engine.addSystem(new SpriteAnimationSystem());
        engine.addSystem(new ParticleSystem());
        engine.addSystem(new LayerSystem());
        engine.addSystem(new CompositeSystem());
        engine.addSystem(new LabelSystem());
        engine.addSystem(new ScriptSystem());
        engine.addSystem(new ActionSystem());
        engine.addSystem(renderer);

        // additional
        engine.addSystem(new ButtonSystem());

        addEntityRemoveListener();
    }

    private void addEntityRemoveListener() {
        engine.addEntityListener(new EntityListener() {
            @Override
            public void entityAdded(Entity entity) {
                // TODO: Gev knows what to do. (do this for all entities)

                // mae sure we assign correct z-index here
                /*
                ZindexComponent zindexComponent = ComponentRetriever.get(entity, ZindexComponent.class);
				ParentNodeComponent parentNodeComponent = ComponentRetriever.get(entity, ParentNodeComponent.class);
				if (parentNodeComponent != null) {
					NodeComponent nodeComponent = parentNodeComponent.parentEntity.getComponent(NodeComponent.class);
					zindexComponent.setZIndex(nodeComponent.children.size);
					zindexComponent.needReOrder = false;
				}*/

                // call init for a system
                ScriptComponent scriptComponent = entity.getComponent(ScriptComponent.class);
                if (scriptComponent != null) {
                    for (IScript script : scriptComponent.scripts) {
                        script.init(entity);
                    }
                }
            }

            @Override
            public void entityRemoved(Entity entity) {
                ParentNodeComponent parentComponent = ComponentRetriever.get(entity, ParentNodeComponent.class);

                if (parentComponent == null) {
                    return;
                }

                Entity parentEntity = parentComponent.parentEntity;
                NodeComponent parentNodeComponent = ComponentRetriever.get(parentEntity, NodeComponent.class);
                if (parentNodeComponent != null) {
                    parentNodeComponent.removeChild(entity);
                }

                // check if composite and remove all children
                NodeComponent nodeComponent = ComponentRetriever.get(entity, NodeComponent.class);
                if (nodeComponent != null) {
                    // it is composite
                    for (Entity node : nodeComponent.children) {
                        engine.removeEntity(node);
                    }
                }
            }
        });
    }

    public Entity loadFromLibrary(String libraryName) {
        ProjectInfoVO projectInfoVO = getRm().getProjectVO();
        CompositeItemVO compositeItemVO = projectInfoVO.libraryItems.get(libraryName);

        if (compositeItemVO != null) {
            Entity entity = entityFactory.createEntity(null, compositeItemVO);
            return entity;
        }

        return null;
    }

    public CompositeItemVO loadVoFromLibrary(String libraryName) {
        ProjectInfoVO projectInfoVO = getRm().getProjectVO();
        CompositeItemVO compositeItemVO = projectInfoVO.libraryItems.get(libraryName);

        return compositeItemVO;
    }

    public void addComponentsByTagName(String tagName, Class componentClass) {
        ImmutableArray<Entity> entities = engine.getEntities();
        for (Entity entity : entities) {
            MainItemComponent mainItemComponent = ComponentRetriever.get(entity, MainItemComponent.class);
            if (mainItemComponent.tags.contains(tagName)) {
                try {
                    entity.add(ClassReflection.<Component>newInstance(componentClass));
                } catch (ReflectionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    public IResourceRetriever getRm() {
        return rm;
    }

    public Engine getEngine() {
        return engine;
    }

    public Entity getRoot() {
        return rootEntity;
    }

    public Batch getBatch() {
        return renderer.getBatch();
    }
}
