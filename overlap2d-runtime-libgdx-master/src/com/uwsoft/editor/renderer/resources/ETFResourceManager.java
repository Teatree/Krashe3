package com.uwsoft.editor.renderer.resources;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Json;
import com.uwsoft.editor.renderer.data.*;
import com.uwsoft.editor.renderer.utils.MySkin;

import java.util.*;

import static java.io.File.separator;

/**
 * Created by ARudyk on 1/2/2017.
 */
public class ETFResourceManager implements IResourceRetriever, IResourceLoader {

    public static final String MY_SEPARATOR = "/";
    public static final String SCML = ".scml";
    public static final String ORIG = "orig";
    public static final String TTF = ".ttf";
    public static final String DT = ".dt";
    public static final String PROJECT_DT = "project.dt";
    public static final String PACK_ATLAS = "pack.atlas";
    public static final String ATLAS = ".atlas";
    public static final String ORIG_SPRITER_ANIMATIONS = "\\orig\\spriter_animations\\";
    public static final String FLOWER_IDLE = "flower_idle";
    public static final String FLOWER_LEAFS_IDLE = "flower_leafs_idle";
    public String packResolutionName = ORIG;

    private static final String scenesPath = "scenes";
    private static final String particleEffectsPath = "particles";
    private static final String spriteAnimationsPath = "sprite_animations";
    private static final String spriterAnimationsPath = "spriter_animations";
    private static final String fontsPath = "freetypefonts";

    protected float resMultiplier;

    protected ProjectInfoVO projectVO;

    protected ArrayList<String> preparedSceneNames = new ArrayList<String>();
    public HashMap<String, SceneVO> loadedSceneVOs = new HashMap<String, SceneVO>();

    protected HashSet<String> particleEffectNamesToLoad = new HashSet<String>();
    protected HashSet<String> spriteAnimNamesToLoad = new HashSet<String>();
    protected HashSet<String> spriterAnimNamesToLoad = new HashSet<String>();
    protected HashSet<FontSizePair> fontsToLoad = new HashSet<FontSizePair>();

    protected HashMap<String, TextureAtlas> skeletonAtlases = new HashMap<String, TextureAtlas>();
    protected HashMap<String, FileHandle> skeletonJSON = new HashMap<String, FileHandle>();

    public HashMap<String, FileHandle> spriterAnimations = new HashMap<String, FileHandle>();
    protected HashMap<FontSizePair, BitmapFont> bitmapFonts = new HashMap<FontSizePair, BitmapFont>();

    List<String> animationsToOverride = new ArrayList<String>();
    public AssetManager manager = new AssetManager();

    public ETFResourceManager() {
        animationsToOverride.add(FLOWER_IDLE);
        animationsToOverride.add(FLOWER_LEAFS_IDLE);
    }

    /**
     * Sets working resolution, please set before doing any loading
     *
     * @param resolution String resolution name, default is "orig" later use resolution names created in editor
     */
    public void setWorkingResolution(String resolution) {
        ResolutionEntryVO resolutionObject = getProjectVO().getResolution(resolution);
        if (resolutionObject != null) {
            packResolutionName = resolution;
        }
    }

    public void initAllResources() {
        loadProjectVO();
        for (int i = 0; i < projectVO.scenes.size(); i++) {
            loadSceneVO(projectVO.scenes.get(i).sceneName);
            scheduleScene(projectVO.scenes.get(i).sceneName);
        }
        prepareAssetsToLoad();
        moveToLocal();
        loadAssets();
    }

    /**
     * Schedules scene for later loading
     * if later prepareAssetsToLoad function will be called it will only prepare assets
     * that are used in scheduled scene
     *
     * @param name - scene file name without ".dt" extension
     */
    public void scheduleScene(String name) {
        if (loadedSceneVOs.containsKey(name)) {
            preparedSceneNames.add(name);
        } else {
            //TODO: Throw exception that scene was not loaded to be prepared for asseting
        }
    }


    /**
     * Unschedule scene from later loading
     *
     * @param name
     */
    public void unScheduleScene(String name) {
        preparedSceneNames.remove(name);
    }


    /**
     * Creates the list of uniqe assets used in all of the scheduled scenes,
     * removes all the duplicates, and makes list of assets that are only needed.
     */
    public void prepareAssetsToLoad() {
        particleEffectNamesToLoad.clear();
        spriteAnimNamesToLoad.clear();
        spriterAnimNamesToLoad.clear();
        fontsToLoad.clear();

        for (String preparedSceneName : preparedSceneNames) {
            CompositeVO composite = loadedSceneVOs.get(preparedSceneName).composite;
            if (composite == null) {
                continue;
            }
            String[] particleEffects = composite.getRecursiveParticleEffectsList();
            String[] spriteAnimations = composite.getRecursiveSpriteAnimationList();
            String[] spriterAnimations = composite.getRecursiveSpriterAnimationList();
            FontSizePair[] fonts = composite.getRecursiveFontList();
            for (CompositeItemVO library : projectVO.libraryItems.values()) {
                FontSizePair[] libFonts = library.composite.getRecursiveFontList();
                Collections.addAll(fontsToLoad, libFonts);

//                // loading particle effects used in library items
                String[] libEffects = library.composite.getRecursiveParticleEffectsList();
                Collections.addAll(particleEffectNamesToLoad, libEffects);
            }

            Collections.addAll(particleEffectNamesToLoad, particleEffects);
            Collections.addAll(spriteAnimNamesToLoad, spriteAnimations);
            Collections.addAll(spriterAnimNamesToLoad, spriterAnimations);
            Collections.addAll(fontsToLoad, fonts);
        }
        loadAtlasPack();
        loadParticleEffects();
        loadSpriteAnimations();
        loadFonts();
    }

    /**
     * Loads all the scheduled assets into memory including
     * main atlas pack, particle effects, sprite animations, spine animations and fonts
     */
    public void loadAssets() {
        System.out.println();
        while (!manager.update()) {
        }
        loadSpriterAnimations();
        System.out.println();
    }

    @Override
    public void loadAtlasPack() {
        manager.load(packResolutionName + separator + PACK_ATLAS, TextureAtlas.class);
    }

    @Override
    public void loadParticleEffects() {
        for (String particleName : particleEffectNamesToLoad) {
            manager.load(particleEffectsPath + separator + particleName, ParticleEffect.class);
        }
    }

    @Override
    public void loadSpriteAnimations() {
        for (String name : spriteAnimNamesToLoad) {
            manager.load(packResolutionName + separator + spriteAnimationsPath + separator
                    + name + separator + name + ATLAS, TextureAtlas.class);
        }
    }

    public void moveToLocal() {
        if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            for (String ani : animationsToOverride) {
                if (!Gdx.files.local(ORIG_SPRITER_ANIMATIONS + ani).exists()) {
                    Gdx.files.internal("orig\\spriter_animations\\" + ani).
                            copyTo(Gdx.files.local(ORIG_SPRITER_ANIMATIONS + ani));
                }
            }
        }
    }

    @Override
    public void loadSpriterAnimations() {
        // empty existing ones that are not scheduled to load
        for (String key : spriterAnimations.keySet()) {
            if (!spriterAnimNamesToLoad.contains(key)) {
                spriterAnimations.remove(key);
            }
        }
        for (String name : spriterAnimNamesToLoad) {
            if (!animationsToOverride.contains(name)) {
                FileHandle animFile = Gdx.files.internal(ORIG + separator + spriterAnimationsPath + separator +
                        name + separator + name + SCML);
                spriterAnimations.put(name, animFile);
            } else {
                loadLocalSpriter(name);
            }
        }
    }

    private void loadLocalSpriter(String name) {
        FileHandle animFile = Gdx.files.local(ORIG + separator + spriterAnimationsPath + separator +
                name + separator + name + SCML);
        spriterAnimations.put(name, animFile);
    }

    public void reloadFlowerAni(boolean flower, boolean leaves){
        if (flower) {
            FileHandle animFileFlower = Gdx.files.local(ORIG + separator + spriterAnimationsPath + separator +
                    FLOWER_IDLE + separator + FLOWER_IDLE + SCML);
            spriterAnimations.put(FLOWER_IDLE, animFileFlower);
        }

        if (leaves) {
            FileHandle animFileLeaves = Gdx.files.local(ORIG + separator + spriterAnimationsPath + separator +
                    FLOWER_LEAFS_IDLE + separator + FLOWER_LEAFS_IDLE + SCML);
            spriterAnimations.put(FLOWER_LEAFS_IDLE, animFileLeaves);
        }
    }

    @Override
    public void loadSpineAnimations() {
    }

    @Override
    public void loadFonts() {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, TTF, new FreetypeFontLoader(resolver));
        for (FontSizePair pair : fontsToLoad) {
            loadFont(pair);
        }
    }

    public void loadFont(FontSizePair pair) {
        FreetypeFontLoader.FreeTypeFontLoaderParameter sizeParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        sizeParams.fontFileName = fontsPath + MY_SEPARATOR + pair.fontName + TTF;
        sizeParams.fontParameters.size = pair.fontSize;
        // This was once a solution to the no border problem
//        sizeParams.fontParameters.borderWidth = 4;
//        sizeParams.fontParameters.borderColor = Color.BLACK;
        manager.load(pair.fontName + pair.fontSize + TTF, BitmapFont.class, sizeParams);
    }

    @Override
    public SceneVO loadSceneVO(String sceneName) {
        FileHandle file = Gdx.files.internal(scenesPath + separator + sceneName + DT);
        Json json = new Json();
        SceneVO sceneVO = json.fromJson(SceneVO.class, file.readString());

        loadedSceneVOs.put(sceneName, sceneVO);

        return sceneVO;
    }

    public void unLoadSceneVO(String sceneName) {
        loadedSceneVOs.remove(sceneName);
    }

    @Override
    public ProjectInfoVO loadProjectVO() {

        FileHandle file = Gdx.files.internal(PROJECT_DT);
        Json json = new Json();
        projectVO = json.fromJson(ProjectInfoVO.class, file.readString());

        return projectVO;
    }

    @Override
    public void loadShaders() {
    }

    @Override
    public TextureRegion getTextureRegion(String name) {
        return manager.get(packResolutionName + MY_SEPARATOR + PACK_ATLAS, TextureAtlas.class).findRegion(name);
    }

    @Override
    public ParticleEffect getParticleEffect(String name) {
        return manager.get(particleEffectsPath + MY_SEPARATOR + name, ParticleEffect.class);
    }

    @Override
    public TextureAtlas getSkeletonAtlas(String name) {
        return skeletonAtlases.get(name);
    }

    @Override
    public FileHandle getSkeletonJSON(String name) {
        return skeletonJSON.get(name);
    }

    @Override
    public TextureAtlas getSpriteAnimation(String name) {
        return manager.get(packResolutionName + MY_SEPARATOR + spriteAnimationsPath + MY_SEPARATOR
                + name + MY_SEPARATOR + name + ATLAS, TextureAtlas.class);
    }

    @Override
    public BitmapFont getBitmapFont(String name, int size) {
        return manager.get(name + size + TTF, BitmapFont.class);
//        return bitmapFonts.get(new FontSizePair(name, size));
    }

    @Override
    public MySkin getSkin() {
        return null;
    }

    @Override
    public SceneVO getSceneVO(String sceneName) {
        return loadedSceneVOs.get(sceneName);
    }

    @Override
    public ProjectInfoVO getProjectVO() {
        return projectVO;
    }

    @Override
    public ResolutionEntryVO getLoadedResolution() {
        if (packResolutionName.equals(ORIG)) {
            return getProjectVO().originalResolution;
        }
        return getProjectVO().getResolution(packResolutionName);
    }

    public void dispose() {
        manager.dispose();
    }

    @Override
    public FileHandle getSCMLFile(String name) {
        return spriterAnimations.get(name);
    }

    @Override
    public ShaderProgram getShaderProgram(String shaderName) {
        return null;
    }

    public void addSpriterToLoad(String aniName) {
        FileHandle animFilePet = Gdx.files.internal(ORIG + separator + spriterAnimationsPath + separator +
                aniName + separator + aniName + SCML);
        spriterAnimations.put(aniName, animFilePet);
    }

    public void addSPRITEtoLoad(String aniName) {
        manager.load(packResolutionName + separator + spriteAnimationsPath + separator +
                aniName + separator + aniName + ATLAS, TextureAtlas.class);
        while (!manager.update()) {
        }
    }

}
