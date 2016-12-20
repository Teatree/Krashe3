package com.fd.etf.utils;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.commons.IExternalItemType;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.ParentNodeComponent;
import com.uwsoft.editor.renderer.components.ScriptComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.CompositeVO;
import com.uwsoft.editor.renderer.data.ProjectInfoVO;
import com.uwsoft.editor.renderer.data.SceneVO;
import com.uwsoft.editor.renderer.factory.EntityFactory;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.resources.ResourceManager;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.systems.*;
import com.uwsoft.editor.renderer.systems.action.ActionSystem;
import com.uwsoft.editor.renderer.systems.render.Overlap2dRenderer;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.HashMap;
import java.util.Map;

public class ETFSceneLoader extends SceneLoader {

    public Entity rootEntity;
    public SceneVO sceneVO;
    public Engine engine = null;

    public EntityFactory entityFactory;
    public Overlap2dRenderer renderer;
    private String curResolution = "orig";
    public ResourceManager rm = null;
    private float pixelsPerWU = 1;

    public Map<String, Engine> engineByScene = new HashMap<>();
    public Map<String, Entity> rootEntityByScene = new HashMap<>();

    public ETFSceneLoader() {
        ResourceManager rm = new ResourceManager();
        rm.initAllResources();
        this.rm = rm;
        this.engine = new Engine();
        initSceneLoader();
        for (String sceneName : rm.loadedSceneVOs.keySet()) {
            loadScene(sceneName);
        }
    }

    public ETFSceneLoader(Viewport viewport) {
        ResourceManager rm = new ResourceManager();
        rm.initAllResources();
        this.rm = rm;
        this.engine = new Engine();
        initSceneLoader();
        for (String sceneName : rm.loadedSceneVOs.keySet()) {
            if (!(sceneName.equals("ResultScene") || sceneName.equals("ShopScene"))) {
                loadScene(sceneName, viewport);
            }
        }
        Runtime.getRuntime().gc();
    }

    public ETFSceneLoader(ResourceManager rm) {
        this.engine = new Engine();
        this.rm = rm;
        initSceneLoader();
    }


    /**
     * Returns a new instance of the default shader used by SpriteBatch for GL2 when no shader is specified.
     */
    static public ShaderProgram createDefaultShader() {
        String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "uniform mat4 u_projTrans;\n" //
                + "varying vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "\n" //
                + "void main()\n" //
                + "{\n" //
                + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "   v_color.a = v_color.a * (255.0/254.0);\n" //
                + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "}\n";
        String fragmentShader = "#ifdef GL_ES\n" //
                + "#define LOWP lowp\n" //
                + "precision mediump float;\n" //
                + "#else\n" //
                + "#define LOWP \n" //
                + "#endif\n" //
                + "varying LOWP vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "uniform sampler2D u_texture;\n" //
                + "uniform vec2 atlasCoord;\n" //
                + "uniform vec2 atlasSize;\n" //
                + "uniform int isRepeat;\n" //
                + "void main()\n"//
                + "{\n" //
                + "vec4 textureSample = vec4(0.0,0.0,0.0,0.0);\n"//
                + "if(isRepeat == 1)\n"//
                + "{\n"//
                + "textureSample = v_color * texture2D(u_texture, atlasCoord+mod(v_texCoords, atlasSize));\n"//
                + "}\n"//
                + "else\n"//
                + "{\n"//
                + "textureSample = v_color * texture2D(u_texture, v_texCoords);\n"//
                + "}\n"//
                + "  gl_FragColor = textureSample;\n" //
                + "}";

        ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (shader.isCompiled() == false)
            throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        return shader;
    }

    /**
     * this method is called when rm has loaded all data
     */
    public void initSceneLoader() {
        addSystems();
        entityFactory = new EntityFactory( rm);
    }

    public SceneVO getSceneVO() {
        return sceneVO;
    }

    public SceneVO loadScene(String sceneName, Viewport viewport) {

        this.engine = new Engine();
        initSceneLoader();

        pixelsPerWU = rm.getProjectVO().pixelToWorld;

        sceneVO = rm.getSceneVO(sceneName);
        if (sceneVO.composite == null) {
            sceneVO.composite = new CompositeVO();
        }
        Entity rootEntity = entityFactory.createRootEntity(sceneVO.composite, viewport);
        this.engine.addEntity(rootEntity);

        if (sceneVO.composite != null) {
            entityFactory.initAllChildren(engine, rootEntity, sceneVO.composite);
        }

        rootEntityByScene.put(sceneName, rootEntity);
        engineByScene.put(sceneName, engine);
        System.out.println();

        setAmbienceInfo(sceneVO);
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
    }

    public SceneVO loadScene(String sceneName) {
        ProjectInfoVO projectVO = rm.getProjectVO();
        Viewport viewport = new ScalingViewport(Scaling.stretch, (float) projectVO.originalResolution.width / pixelsPerWU,
                (float) projectVO.originalResolution.height / pixelsPerWU, new OrthographicCamera());
        return loadScene(sceneName, viewport);
    }

    public void injectExternalItemType(IExternalItemType itemType) {
        itemType.injectDependencies(rm);
        itemType.injectMappers();
        entityFactory.addExternalFactory(itemType);
        engine.addSystem(itemType.getSystem());
        renderer.addDrawableType(itemType);
    }

    private void addSystems() {
        ParticleSystem particleSystem = new ParticleSystem();
        SpriteAnimationSystem animationSystem = new SpriteAnimationSystem();
        LayerSystem layerSystem = new LayerSystem();
        CompositeSystem compositeSystem = new CompositeSystem();
        LabelSystem labelSystem = new LabelSystem();
        ScriptSystem scriptSystem = new ScriptSystem();
        ActionSystem actionSystem = new ActionSystem();
        renderer = new Overlap2dRenderer(new SpriteBatch());
        engine.addSystem(animationSystem);
        engine.addSystem(particleSystem);
        engine.addSystem(layerSystem);
        engine.addSystem(compositeSystem);
        engine.addSystem(labelSystem);
        engine.addSystem(scriptSystem);
        engine.addSystem(actionSystem);
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
                parentNodeComponent.removeChild(entity);

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

    /**
     * Sets ambient light to the one specified in scene from editor
     *
     * @param vo - Scene data file to invalidate
     */
    public void setAmbienceInfo(SceneVO vo) {
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
