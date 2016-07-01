package com.uwsoft.editor.renderer.resources;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.uwsoft.editor.renderer.data.ProjectInfoVO;
import com.uwsoft.editor.renderer.data.ResolutionEntryVO;
import com.uwsoft.editor.renderer.data.SceneVO;
import com.uwsoft.editor.renderer.utils.MySkin;

/**
 * Created by azakhary on 9/9/2014.
 */
public interface IResourceRetriever {

    TextureRegion getTextureRegion(String name);
    ParticleEffect getParticleEffect(String name);
    TextureAtlas getSkeletonAtlas(String name);
    FileHandle getSkeletonJSON(String name);
    FileHandle getSCMLFile(String name);
    TextureAtlas getSpriteAnimation(String name);
    BitmapFont getBitmapFont(String name, int size);
    MySkin getSkin();

    SceneVO getSceneVO(String sceneName);
    ProjectInfoVO getProjectVO();

    ResolutionEntryVO getLoadedResolution();
    ShaderProgram getShaderProgram(String shaderName);
}
