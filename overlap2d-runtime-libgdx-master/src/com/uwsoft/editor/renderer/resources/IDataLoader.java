package com.uwsoft.editor.renderer.resources;

import com.uwsoft.editor.renderer.data.ProjectInfoVO;
import com.uwsoft.editor.renderer.data.SceneVO;

/**
 * Created by azakhary on 9/9/2014.
 */
public interface IDataLoader {

    SceneVO loadSceneVO(String sceneName);
    ProjectInfoVO loadProjectVO();

}
