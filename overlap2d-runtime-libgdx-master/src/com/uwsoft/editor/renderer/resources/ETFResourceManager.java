package com.uwsoft.editor.renderer.resources;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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
    public String packResolutionName = "orig";

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
        animationsToOverride.add("flower_idle");
        animationsToOverride.add("flower_leafs_idle");
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
     * Initializes scene by loading it's VO data object and loading all the assets
     * needed for this particular scene only
     *
     * @param sceneName - scene file name without ".dt" extension
     */
    public void initScene(String sceneName) {
        loadSceneVO(sceneName);
        scheduleScene(sceneName);
        prepareAssetsToLoad();
        loadAssets();
    }

    /**
     * Unloads scene from the memory, and clears all the freed assets
     *
     * @param sceneName - scene file name without ".dt" extension
     */
    public void unLoadScene(String sceneName) {
        unScheduleScene(sceneName);
        loadedSceneVOs.remove(sceneName);
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

                // loading particle effects used in library items
                String[] libEffects = library.composite.getRecursiveParticleEffectsList();
                Collections.addAll(particleEffectNamesToLoad, libEffects);
            }

            Collections.addAll(particleEffectNamesToLoad, particleEffects);
            Collections.addAll(spriteAnimNamesToLoad, spriteAnimations);
            Collections.addAll(spriterAnimNamesToLoad, spriterAnimations);
            Collections.addAll(fontsToLoad, fonts);
        }

        manager.load(packResolutionName + separator + "pack.atlas", TextureAtlas.class);
        loadParticleEffects();
        loadSpriteAnimations();
    }

    /**
     * Loads all the scheduled assets into memory including
     * main atlas pack, particle effects, sprite animations, spine animations and fonts
     */
    public void loadAssets() {
        while (!manager.update()) {}
        loadSpriterAnimations();
        loadFonts();
    }

    @Override
    public void loadAtlasPack() {
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
                    + name + separator + name + ".atlas", TextureAtlas.class);
        }
    }

    public void moveToLocal() {
        if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            for (String ani : animationsToOverride) {
                if (!Gdx.files.local("\\orig\\spriter_animations\\" + ani).exists()) {
                    Gdx.files.internal("orig\\spriter_animations\\" + ani).
                            copyTo(Gdx.files.local("\\orig\\spriter_animations\\" + ani));
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
                FileHandle animFile = Gdx.files.internal("orig" + separator + spriterAnimationsPath + separator +
                        name + separator + name + ".scml");
                spriterAnimations.put(name, animFile);
            } else {
                FileHandle animFile = Gdx.files.local("orig" + separator + spriterAnimationsPath + separator +
                        name + separator + name + ".scml");
                spriterAnimations.put(name, animFile);
            }
        }
    }

    @Override
    public void loadSpineAnimations() {
    }

    @Override
    public void loadFonts() {
        //resolution related stuff
        ResolutionEntryVO curResolution = getProjectVO().getResolution(packResolutionName);
        resMultiplier = 1;
        if (!packResolutionName.equals("orig")) {
            if (curResolution.base == 0) {
                resMultiplier = (float) curResolution.width / (float) getProjectVO().originalResolution.width;
            } else {
                resMultiplier = (float) curResolution.height / (float) getProjectVO().originalResolution.height;
            }
        }

        // empty existing ones that are not scheduled to load
        for (FontSizePair pair : bitmapFonts.keySet()) {
            if (!fontsToLoad.contains(pair)) {
                bitmapFonts.remove(pair);
            }
        }

        for (FontSizePair pair : fontsToLoad) {
            loadFont(pair);
        }
    }

    public void loadFont(FontSizePair pair) {
        FileHandle fontFile;
        fontFile = Gdx.files.internal(fontsPath + separator + pair.fontName + ".ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = Math.round(pair.fontSize * resMultiplier);
        BitmapFont font = generator.generateFont(parameter);
        bitmapFonts.put(pair, font);
    }

    @Override
    public SceneVO loadSceneVO(String sceneName) {
        FileHandle file = Gdx.files.internal(scenesPath + separator + sceneName + ".dt");
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

        FileHandle file = Gdx.files.internal("project.dt");
        Json json = new Json();
        projectVO = json.fromJson(ProjectInfoVO.class, file.readString());

        return projectVO;
    }

    @Override
    public void loadShaders() {
    }

    @Override
    public TextureRegion getTextureRegion(String name) {
        return manager.get(packResolutionName + MY_SEPARATOR + "pack.atlas", TextureAtlas.class).findRegion(name);
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
                + name + MY_SEPARATOR + name + ".atlas", TextureAtlas.class);
    }

    @Override
    public BitmapFont getBitmapFont(String name, int size) {
//        return manager.get(fontsPath + "/" + name + ".ttf", BitmapFont.class);
        return bitmapFonts.get(new FontSizePair(name, size));
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
        if (packResolutionName.equals("orig")) {
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
        FileHandle animFilePet = Gdx.files.internal("orig" + separator + spriterAnimationsPath + separator +
                aniName + separator + aniName + ".scml");
        spriterAnimations.put(aniName, animFilePet);
    }

    public void addSPRITEtoLoad(String aniName) {
        manager.load(packResolutionName + separator + spriteAnimationsPath + separator +
                aniName + separator + aniName + ".atlas", TextureAtlas.class);
        while (!manager.update()) {
        }
    }

}
